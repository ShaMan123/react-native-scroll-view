import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Text, View, PanResponder, Image, Animated, StyleSheet, Dimensions, ViewPropTypes, Platform, PixelRatio, I18nManager, findNodeHandle, StatusBar } from 'react-native';

const screenScale = Platform.OS === 'ios' ? 1 : PixelRatio.get();
const isRTL = I18nManager.isRTL;

const ScrollEvents = {
    onMomentumScrollBegin: 'onMomentumScrollBegin',
    onMomentumScrollEnd: 'onMomentumScrollEnd',
    onScroll: 'onScroll',
    onScrollBeginDrag: 'onScrollBeginDrag',
    onScrollEndDrag: 'onScrollEndDrag',
}

const Events = {
    onLayout: 'onLayout',
    onContentSizeChange: 'onContentSizeChange',
    ...ScrollEvents,
    //onZoom: 'onZoom',
    //onZoomEnd: 'onZoomEnd'
}

const decelerationRate = {
    normal: 0.998,
    fast: 0.99
}

const directionalLock = {
    horizontal: 'horizontal',
    vertical: 'vertical'
}

export default class ScrollView extends Component {
    static propTypes = {
        //  zoom props
        bouncesZoom: PropTypes.bool,
        zoomScale: PropTypes.number,
        minimumZoomScale: PropTypes.number,
        maximumZoomScale: PropTypes.number,
        pinchGestureEnabled: PropTypes.bool,
        centerContent: PropTypes.bool,
        contentOffset: PropTypes.shape({ x: PropTypes.number, y: PropTypes.number }),
        contentInset: PropTypes.shape({ top: PropTypes.number, bottom: PropTypes.number, left: PropTypes.number, right: PropTypes.number, start: PropTypes.number, end: PropTypes.number }),
        decelerationRate: function (props, propName, componentName) {
            const val = props[propName];
            if ((typeof val !== 'number' && !decelerationRate[val]) || (typeof val === 'number' && (val <= 0 || val >= 1))) {
                return new Error(
                    'Invalid prop `' + propName + '` supplied to' +
                    ' `' + componentName + '`. Validation failed.' + 
                    'See documentation: https://facebook.github.io/react-native/docs/scrollview#decelerationrate'
                );
            }
        },

        //  additional ScrollView props
        scrollEnabled: PropTypes.bool,
        directionalLockEnabled: PropTypes.bool,
        contentContainerStyle: ViewPropTypes.style,
        style: ViewPropTypes.style,
        refreshControl: PropTypes.element,
        
        //stickyHeaderIndices: vertical only

        //  events
        ...Object.keys(Events)
            .reduce((combined, key) => {
                combined[key] = PropTypes.func;
                return combined;
            }, {})
    }

    static defaultProps = {
        bouncesZoom: true,
        zoomScale: 1,
        minimumZoomScale: 1,
        maximumZoomScale: 1,
        pinchGestureEnabled: true,
        centerContent: true,
        contentOffset: null,
        contentInset: { top: 0, bottom: 0, left: 0, right: 0, start: 0, end: 0 },
        decelerationRate: decelerationRate.normal,

        scrollEnabled: true,
        directionalLockEnabled: false,
        contentContainerStyle: null,
        style: null,
        refreshControl: null,

        //  events
        ...Object.keys(Events)
            .reduce((combined, key) => {
                combined[key] = () => { };
                return combined;
            }, {})
    }

    constructor(props) {
        super(props);

        const scale = new Animated.Value(props.zoomScale);
        const clampedScale = new Animated.diffClamp(scale, props.minimumZoomScale, props.maximumZoomScale);

        this._scaleValue = props.zoomScale;
        scale.addListener(({ value }) => {
            this._scaleValue = value;
            
            //props.onZoom(value);
        });

        const translate = new Animated.ValueXY({ x: 0, y: 0 });
        this._translateValue = { x: 0, y: 0 };
        translate.addListener((value) => {
            this._translateValue = value;
            this._eventSender(Events.onScroll);
            if (this._runningAnimations.length > 0) {
                const clampedTranslate = this.getClampedTranslate(this._translateValue);
                if (clampedTranslate.x !== this._translateValue.x || clampedTranslate.y !== this._translateValue.y) {                    
                    this._runningAnimations.map((anim) => anim.stop());
                    this._runningAnimations = [];
                    Animated.spring(this._animatedValues.translate, {
                        toValue: clampedTranslate,
                        useNativeDriver: true,
                        //speed: 24,
                    }).start(() => this._eventSender(Events.onMomentumScrollEnd));
                }
            }
        });

        const opacity = new Animated.Value(0);

        this._animatedValues = { scale, clampedScale, translate, opacity };

        this._onLayout = this._onLayout.bind(this);
        this._handleRelease = this._handleRelease.bind(this);
        this._loadPanResponder.call(this);

        this.style = StyleSheet.compose({
            opacity: this._animatedValues.opacity,
            transform: [
                { scale: this._animatedValues.clampedScale },
                { translateX: this._animatedValues.translate.x },
                { translateY: this._animatedValues.translate.y },
                { perspective: 1000 }
            ],
        })

        this.contentInsetStyle = this.getContentInsetStyleAttr();

        this._initialDistance = null;
        this._initialScale = null;
        this._isZooming = false;
        this._directionalLock = null;
        this._didTranslate = { x: false, y: false };
        this._runningAnimations = [];
        this._scrollToInProgress = false;

        this.state = {
            width: null,
            height: null,
        }
    }

    getDecelerationRate() {
        return typeof this.props.decelerationRate === 'number' ? this.props.decelerationRate : decelerationRate[this.props.decelerationRate];
    }

    getContentInset(contentInset = this.props.contentInset) {
        const start = {
            key: isRTL ? 'right' : 'left',
            value: contentInset.start ? contentInset.start : isRTL ? contentInset.right : contentInset.left,
        }
        const end = {
            key: isRTL ? 'left' : 'right',
            value: contentInset.end ? contentInset.end : isRTL ? contentInset.left : contentInset.right,
        }

        return {
            top: contentInset.top,
            bottom: contentInset.bottom,
            [start.key]: start.value,
            [end.key]: end.value
        }
    }

    getContentInsetStyleAttr(contentInset = this.getContentInset()) {
        const attr = 'margin';

        const _contentInset = {
            [`${attr}Top`]: contentInset.top,
            [`${attr}Bottom`]: contentInset.bottom,
            [`${attr}Left`]: contentInset.left,
            [`${attr}Right`]: contentInset.right,
            //[`${attr}Start`]: contentInset.start,
            //[`${attr}End`]: contentInset.end
        }
        
        return StyleSheet.compose(_contentInset);
    }

    calcDistance(x1, y1, x2, y2) {
        let dx = Math.abs(x1 - x2)
        let dy = Math.abs(y1 - y2)
        return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
    }

    getClampedScale(value = this._scaleValue) {
        const { minimumZoomScale, maximumZoomScale } = this.props;
        return Math.min(Math.max(value, minimumZoomScale), maximumZoomScale);
    }

    getClampedTranslate({ x, y } = this._translateValue) {
        const dimensions = Dimensions.get('window');
        const clampedScale = this.getClampedScale();
        const scaler = 0.5 * (clampedScale - 1) / clampedScale;
        const complementaryScaler = clampedScale < 1 ? clampedScale : 1 / clampedScale;
        const clampers = {
            x: scaler * this.state.width,
            y: scaler * this.state.height
        }
        const complementaryClampers = {
            x: clampers.x - this.state.width + dimensions.width * complementaryScaler, 
            y: clampers.y - this.state.height + dimensions.height * complementaryScaler
        }

        const clampCheck = {
            x: this.state.width * clampedScale - dimensions.width > 0,
            y: this.state.height * clampedScale - dimensions.height > 0
        }

        //  
        const centeredContent = {
            x: (dimensions.width - this.state.width) * scaler,
            y: (dimensions.height - this.state.height) * scaler
        }
        const alignToTop = {
            x: isRTL ? complementaryClampers.x : clampers.x,
            y: clampers.y
        }
        const centerContent = this.props.centerContent ?
            {
                x: clampCheck.x ? alignToTop.x : centeredContent.x,
                y: clampCheck.y ? alignToTop.y : centeredContent.y
            } : alignToTop;

        return {
            x: clampCheck.x ? Math.min(clampers.x, Math.max(complementaryClampers.x, x)) : centerContent.x,
            y: clampCheck.y ? Math.min(clampers.y, Math.max(complementaryClampers.y, y)) : centerContent.y
        }
    }

    _loadPanResponder() {
        this._panResponder = PanResponder.create({
            onStartShouldSetPanResponder: (evt, gestureState) => this.props.scrollEnabled || this.props.pinchGestureEnabled,
            //onStartShouldSetPanResponderCapture: (evt, gestureState) => true,
            onMoveShouldSetPanResponder: (evt, gestureState) => this.props.scrollEnabled || this.props.pinchGestureEnabled,
            //onMoveShouldSetPanResponderCapture: (evt, gestureState) => true,
            onPanResponderGrant: (evt, gestureState) => {
                this._translateStart = {
                    x: this._translateValue.x,
                    y: this._translateValue.y
                }

                this._scaleStart = this.getClampedScale();
                this._eventSender(Events.onScrollBeginDrag);
            },
            onPanResponderMove: (evt, gestureState) => {
                let touches = evt.nativeEvent.touches;
                if (touches.length == 2 && this.props.pinchGestureEnabled) {
                    let touch1 = touches[0];
                    let touch2 = touches[1];

                    const d = this.calcDistance(touches[0].pageX, touches[0].pageY, touches[1].pageX, touches[1].pageY);
                    if (!this._initialDistance) this._initialDistance = d;
                    const zoom = d / this._initialDistance;
                    if (!this._initialScale) this._initialScale = zoom - this._scaleValue;
                    //const scale = this.props.bouncesZoom ? zoom - this._initialScale : this.getClampedScale(zoom - this._initialScale);
                    const scale = this.getClampedScale(zoom - this._initialScale);
                    
                    this._isZooming = true;

                    return Animated.event([null, { scale: this._animatedValues.scale }], () => { useNativeDriver: true })(null, { scale });
                }
                else if (touches.length == 1 && !this._isZooming && this.props.scrollEnabled) {
                    const translations = {
                        x: gestureState.dx / this.getClampedScale(this._scaleValue) + this._translateStart.x,
                        y: gestureState.dy / this.getClampedScale(this._scaleValue) + this._translateStart.y
                    }
                    const m = Math.abs(gestureState.vy) / Math.abs(gestureState.vx);
                    const clampedTranslations = this.getClampedTranslate(translations);
                    if (this.props.directionalLockEnabled) {
                        if (!this._directionalLock) {
                            this._directionalLock = {
                                value: m < 0.5 ? directionalLock.horizontal : m > 2 ? directionalLock.vertical : null,
                                ...clampedTranslations
                            }
                        }
                        switch (this._directionalLock.value) {
                            case directionalLock.horizontal:
                                clampedTranslations.y = this._directionalLock.y;
                                break;
                            case directionalLock.vertical:
                                clampedTranslations.x = this._directionalLock.x;
                                break;
                        }
                    }

                    this._didTranslate = {
                        x: translations.x === clampedTranslations.x,
                        y: translations.y === clampedTranslations.y
                    }
                    console.log(translations)
                    return Animated.event([null, { x: this._animatedValues.translate.x, y: this._animatedValues.translate.y }], () => { useNativeDriver: true })(null, clampedTranslations);
                }
            },

            onPanResponderTerminationRequest: (evt, gestureState) => true,
            onPanResponderRelease: this._handleRelease,
            onPanResponderTerminate: this._handleRelease,
            onShouldBlockNativeResponder: (evt, gestureState) => true,
        });
    }

    _handleRelease(evt, gestureState) {
        const deceleration = this.getDecelerationRate();
        const clampedScale = this.getClampedScale();
        const clampedTranslate = this.getClampedTranslate();
        const didClamptranslate = this._translateValue.x !== clampedTranslate.x || this._translateValue.y !== clampedTranslate.y;
        const didShrink = clampedScale < this._scaleStart && didClamptranslate && this._isZooming;

        this._initialDistance = null;
        this._initialScale = null;
        this._isZooming = false;
        this._directionalLock = null;

        this._eventSender(Events.onScrollEndDrag);

        const animations = [];

        if ((this._didTranslate.x || didShrink) && gestureState.vx) {
            animations.push(
                Animated.decay(this._animatedValues.translate.x, {
                    velocity: gestureState.vx / clampedScale,
                    deceleration,
                    useNativeDriver: true
                })
            );
        }
        if ((this._didTranslate.y || didShrink) && gestureState.vy) {
            animations.push(
                Animated.decay(this._animatedValues.translate.y, {
                    velocity: gestureState.vy / clampedScale,
                    deceleration,
                    useNativeDriver: true
                })
            );
        }

        this._runningAnimations = animations;
        /*
        if (this.props.bouncesZoom && this._scaleValue !== clampedScale) {
            animations.push(
                Animated.spring(this._animatedValues.scale, {
                    toValue: clampedScale,
                    useNativeDriver: true
                }));
        }
        */
        if (animations.length > 0) {
            this._eventSender(Events.onMomentumScrollBegin);
            Animated.parallel(animations)
                .start(({ finished }) => {
                    this._didTranslate = { x: false, y: false };
                    finished && this._eventSender(Events.onMomentumScrollEnd);
                });
        }
        else {
            this._didTranslate = { x: false, y: false };
        }
    }

    _onLayout(evt) {
        const { width, height } = evt.nativeEvent.layout;

        if (this.state.width === null && width > 0 && height > 0) {
            this.setState({
                width: width,
                height: height
            });
        }
        this.props.onLayout(evt);
        this.props.onContentSizeChange(width, height);
    }

    componentDidUpdate(prevProps, prevState) {
        if (!prevState.width && this.state.width) {
            /*
            const dimensions = Dimensions.get('window');
            const { width, height } = this.state;
            let scale = this.props.zoomScale;
            if (width > dimensions.width) scale = Math.min(dimensions.width / width, scale);
            if (height > dimensions.height) scale = Math.min(dimensions.height / height, scale);
            */
            const contentOffset = this.props.contentOffset || { x: isRTL ? this.state.width : 0, y: 0 };
            this.scrollTo({ ...contentOffset, animated: false });
            
            Animated.timing(this._animatedValues.opacity, {
                toValue: 1,
                useNativeDriver: true
            }).start();
        }
    }

    getScrollOffset() {
        const clampedScale = this.getClampedScale();
        const scaler = 0.5 * (clampedScale - 1) / clampedScale;
        return {
            x: Math.round(this.state.width * scaler - this._translateValue.x),
            y: Math.round(this.state.height * scaler - this._translateValue.y)
        }
    }

    scrollTo({ x, y, overScroll = false, scale = null, animated = true, callback = null }) {
        const clampedScale = this.getClampedScale();
        const scaler = 0.5 * (clampedScale - 1) / clampedScale;
        const _contentOffset = {
            x: -x + this.state.width * scaler,
            y: -y + this.state.height * scaler
        }
        const contentOffset = overScroll ? _contentOffset : this.getClampedTranslate(_contentOffset);
        
        if (animated) {
            const animations = [];
            this._scrollToInProgress = true;
            if (scale) animations.push(Animated.timing(this._animatedValues.scale, { toValue: this.getClampedScale(scale), useNativeDriver: true }));
            animations.push(Animated.timing(this._animatedValues.translate, { toValue: contentOffset, useNativeDriver: true }));
            this._runningAnimations = animations;
            Animated.parallel(animations)
                .start((arg) => {
                    this._scrollToInProgress = false;
                    callback && callback(arg);
                });
        }
        else {
            if (scale) this._animatedValues.scale.setValue(this.getClampedScale(scale));
            this._animatedValues.translate.setValue(contentOffset);
            callback && callback();
        }
    }

    scrollToEnd({ animated = true, callback = null }) {
        this.scrollTo({ x: this.state.width, y: this.state.height, animated, callback });
    }

    scrollResponderZoomTo({ x, y, width, height, animated = true, callback = null }) {
        // zoomToRect
        const dimensions = this.state;
        const widthScale = dimensions.width / width;
        const heightScale = dimensions.height / height;
        const scale = this.getClampedScale(Math.min(widthScale, heightScale));
        const maxPoint = {
            x: dimensions.width * (scale - 1),
            y: dimensions.height * (scale - 1)
        }
        const point = {
            x: Math.min(maxPoint.x, x),
            y: Math.min(maxPoint.y, y)
        }

        this.scrollTo({ ...point, scale, animated, callback });
    }

    render() {
        return (
            <Animated.View
                ref={ref => this.ref = ref}
                {...this._panResponder.panHandlers}
                onLayout={this._onLayout}
                style={[this.props.style, this.style]}
            >
                {this.props.children}
                {this.props.children}
                {this.props.children}
                {this.props.children}
                {this.props.children}
                {this.props.children}

            </Animated.View>
        );
    }

    _eventSender(eventType) {
        const { width, height } = this.state;
        const scale = this.getClampedScale();
        const evt = {};

        const newNativeEvent = {
            target: findNodeHandle(this.ref),
            layoutMeasurement: { width, height },
            contentSize: { width: width * scale, height: height * scale },
            contentOffset: this.getScrollOffset(),
            contentInset: this.getContentInset(),
            zoom: scale
        }
        evt.nativeEvent = newNativeEvent;
        this.props[eventType](evt);
    }
}
