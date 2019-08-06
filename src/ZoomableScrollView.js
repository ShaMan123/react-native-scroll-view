import React, { Component } from 'react';
import PropTypes from 'prop-types';
import {
    View,
    PanResponder,
    Animated,
    StyleSheet,
    Dimensions,
    ViewPropTypes,
    Platform,
    PixelRatio,
    I18nManager,
    findNodeHandle,
    requireNativeComponent,
    NativeModules,
    ScrollView as RNScrollView,
    UIManager
} from 'react-native';

import './Events';
import ScrollResponder from './ScrollResponder';

const nativeViewName = 'RNZoomableScrollView';
const nativeViewManagerName = 'RNZoomableScrollViewManager';
const nativeViewModulerName = 'RNZoomableScrollViewModule';
const NativeView = requireNativeComponent(nativeViewManagerName, ScrollView, {
    nativeOnly: {
        nativeID: true
    }
});
const ViewManager = NativeModules[nativeViewManagerName] || NativeModules[nativeViewModulerName];
export const { Constants } = UIManager.getViewManagerConfig ? UIManager.getViewManagerConfig(nativeViewManagerName) : UIManager[nativeViewManagerName];



const screenScale = Platform.OS === 'ios' ? 1 : PixelRatio.get();
const isRTL = I18nManager.isRTL;

const ScrollEvents = {
    onMomentumScrollBegin: 'onMomentumScrollBegin',
    onMomentumScrollEnd: 'onMomentumScrollEnd',
    onScroll: 'onScroll',
    onScrollBeginDrag: 'onScrollBeginDrag',
    onScrollEndDrag: 'onScrollEndDrag'
};

const Events = {
    onLayout: 'onLayout',
    onContentSizeChange: 'onContentSizeChange',
    onScrollAnimationEnd: 'onScrollAnimationEnd',
    ...ScrollEvents
    //onZoom: 'onZoom',
    //onZoomEnd: 'onZoomEnd'
};

const shouldSetResponder = {
    onStartShouldSetResponderCapture: 'onStartShouldSetResponderCapture',
    onMoveShouldSetResponderCapture: 'onMoveShouldSetResponderCapture',
    onStartShouldSetResponder: 'onStartShouldSetResponder',
    onMoveShouldSetResponder: 'onMoveShouldSetResponder'
};

const decelerationRate = {
    normal: 0.998,
    fast: 0.99
};

const directionalLock = {
    horizontal: 'horizontal',
    vertical: 'vertical'
};

const styles = StyleSheet.create({
    container: {
        flex: 1
    },
    indicator: {
        position: 'absolute',
        backgroundColor: 'white',
        borderRadius: 25
    },
    horizontalScrollIndicator: {
        bottom: 0,
        right: 0,
        width: 100,
        height: 10
    },
    verticalScrollIndicator: {
        top: 0,
        width: 10,
        height: 100
    }
});

export default class ScrollView extends Component {
    static propTypes = {
        //  zoom props
        bounces: PropTypes.bool,
        bouncesZoom: PropTypes.bool,
        zoomScale: PropTypes.number,
        minimumZoomScale: PropTypes.number,
        maximumZoomScale: PropTypes.number,
        pinchGestureEnabled: PropTypes.bool,
        centerContent: PropTypes.bool,
        contentOffset: PropTypes.shape({ x: PropTypes.number, y: PropTypes.number }),
        contentInset: PropTypes.shape({ top: PropTypes.number, bottom: PropTypes.number, left: PropTypes.number, right: PropTypes.number, start: PropTypes.number, end: PropTypes.number }),
        //decelerationRate: Prop
        overScroll: PropTypes.shape({ x: PropTypes.number, y: PropTypes.number }),
        scrollEnabled: PropTypes.bool,
        directionalLockEnabled: PropTypes.bool,
        showsHorizontalScrollIndicator: PropTypes.bool,
        showsVerticalScrollIndicator: PropTypes.bool,

        //  additional ScrollView props
        contentContainerStyle: ViewPropTypes.style,
        style: ViewPropTypes.style,
        indicatorStyle: ViewPropTypes.style,
        refreshControl: PropTypes.element,
        ...ViewPropTypes,

        //  gesture responder 
        onStartShouldSetResponderCapture: PropTypes.func,
        onMoveShouldSetResponderCapture: PropTypes.func,
        onStartShouldSetResponder: PropTypes.func,
        onMoveShouldSetResponder: PropTypes.func,

        //stickyHeaderIndices: vertical only

        //  events
        ...Object.keys(Events)
            .reduce((combined, key) => {
                combined[key] = PropTypes.func;
                return combined;
            }, {})
    }

    static defaultProps = {
        bounces: true,
        bouncesZoom: true,
        zoomScale: 1,
        minimumZoomScale: 0.75,
        maximumZoomScale: 3,
        pinchGestureEnabled: true,
        centerContent: false,
        contentOffset: null,
        contentInset: { top: 0, bottom: 0, left: 0, right: 0, start: 0, end: 0 },
        decelerationRate: decelerationRate.normal,
        overScroll: { x: 0, y: 0 },
        scrollEnabled: true,
        directionalLockEnabled: false,
        showsHorizontalScrollIndicator: true,
        showsVerticalScrollIndicator: true,

        contentContainerStyle: null,
        style: styles.container,
        indicatorStyle: styles.indicator,
        horizontalIndicatorStyle: styles.horizontalScrollIndicator,
        verticalIndicatorStyle: styles.verticalScrollIndicator,
        refreshControl: null,

        onStartShouldSetResponder: () => false,
        onMoveShouldSetResponder: () => false,

        //  events
        ...Object.keys(Events)
            .reduce((combined, key) => {
                combined[key] = () => { };
                return combined;
            }, {})
    }

    _scrollResponder = new ScrollResponder(this);
    _scrollNode;

    _setRef = (ref) => {
        this._scrollRef = ref;
        this._scrollNode = findNodeHandle(this._scrollRef);
    }

    getScrollResponder() {
        return this._scrollResponder;
    }

    getScrollRef() {
        return this._scrollRef;
    }

    getScrollableNode() {
        return this._scrollNode;
    }

    getDecelerationRate() {
        return typeof this.props.decelerationRate === 'number' ? this.props.decelerationRate : decelerationRate[this.props.decelerationRate];
    }
    
    setMaxOverScroll({ x, y }) {
        
    }

    scrollTo(options) {
        this._scrollResponder.scrollResponderScrollTo(options);
    }

    scrollBy(options) {
        this._scrollResponder.scrollResponderScrollBy(options);
    }

    scrollToEnd(options) {
        this._scrollResponder.scrollResponderScrollToEnd(options);
    }

    flashScrollIndicators() {
        this._scrollResponder.flashScrollIndicators(options);
    }

    render() {
        return (
            <NativeView
                {...this.props}
                ref={this._setRef}
                onScroll={(e) => console.log(e.nativeEvent)}
                onScrollBeginDrag={(e) => console.log('begin', e.nativeEvent)}
                onScrollEndDrag={(e) => console.log('end', e.nativeEvent)}
            >
                <View
                    collapsable={false}
                >
                    {this.props.children}
                </View>
            </NativeView>
        );
    }
}

const style = StyleSheet.create({
    base: {
        flex: 1
    }
});
