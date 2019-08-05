import { UIManager, Dimensions, NativeModules, findNodeHandle } from 'react-native';

const nativeViewName = 'RNZoomableScrollView';
const nativeViewManagerName = 'RNZoomableScrollViewManager';
const nativeViewModulerName = 'RNZoomableScrollViewModule';
const ScrollViewManager = NativeModules[nativeViewManagerName] || NativeModules[nativeViewModulerName];

export default class ScrollResponder {
    constructor(ref) {
        this.ref = ref;
    }
    
    scrollResponderGetScrollableNode() {
        return this.ref.getScrollableNode();
    }

    /**
     * A helper function to scroll to a specific point in the ScrollView.
     * This is currently used to help focus child TextViews, but can also
     * be used to quickly scroll to any element we want to focus. Syntax:
     *
     * `scrollResponderScrollTo(options: {x: number = 0; y: number = 0; animated: boolean = true})`
     *
     * Note: The weird argument signature is due to the fact that, for historical reasons,
     * the function also accepts separate arguments as as alternative to the options object.
     * This is deprecated due to ambiguity (y before x), and SHOULD NOT BE USED.
     */
    scrollResponderScrollTo({ x, y, animated }) {
        UIManager.dispatchViewManagerCommand(
            this.scrollResponderGetScrollableNode(),
            Commands.scrollTo,
            [x || 0, y || 0, animated !== false],
        );
    }

    /**
     * Scrolls to the end of the ScrollView, either immediately or with a smooth
     * animation.
     *
     * Example:
     *
     * `scrollResponderScrollToEnd({animated: true})`
     */
    scrollResponderScrollToEnd({ animated } = { animated: true }) {
        UIManager.dispatchViewManagerCommand(
            this.scrollResponderGetScrollableNode(),
            Commands.scrollToEnd,
            [animated],
        );
    }

    /**
     * A helper function to zoom to a specific rect in the scrollview. The argument has the shape
     * {x: number; y: number; width: number; height: number; animated: boolean = true}
     *
     * @platform ios
     */
    scrollResponderZoomTo(rect) {
        if ('animated' in rect) {
            animated = rect.animated;
            delete rect.animated;
        } else if (typeof animated !== 'undefined') {
            console.warn(
                '`scrollResponderZoomTo` `animated` argument is deprecated. Use `options.animated` instead',
            );
        }
        
        ScrollViewManager.zoomToRect(
            this.scrollResponderGetScrollableNode(),
            rect,
            animated !== false,
        );
        
        /*
        UIManager.dispatchViewManagerCommand(
            this.scrollResponderGetScrollableNode(),
            Commands.zoomToRect,
            [rect, animated !== false]
        );
        */
    }

    /**
     * Displays the scroll indicators momentarily.
     */
    scrollResponderFlashScrollIndicators() {
        UIManager.dispatchViewManagerCommand(
            this.scrollResponderGetScrollableNode(),
            Commands
                .flashScrollIndicators,
            [],
        );
    }

    /**
     * This method should be used as the callback to onFocus in a TextInputs'
     * parent view. Note that any module using this mixin needs to return
     * the parent view's ref in getScrollViewRef() in order to use this method.
     * @param {number} nodeHandle The TextInput node handle
     * @param {number} additionalOffset The scroll view's bottom "contentInset".
     *        Default is 0.
     * @param {bool} preventNegativeScrollOffset Whether to allow pulling the content
     *        down to make it meet the keyboard's top. Default is false.
     */
    scrollResponderScrollNativeHandleToKeyboard(
        nodeHandle,
        additionalOffset,
        preventNegativeScrollOffset,
    ) {
        this.additionalScrollOffset = additionalOffset || 0;
        this.preventNegativeScrollOffset = !!preventNegativeScrollOffset;
        UIManager.measureLayout(
            nodeHandle,
            ReactNative.findNodeHandle(this.getInnerViewNode()),
            this.scrollResponderTextInputFocusError,
            this.scrollResponderInputMeasureAndScrollToKeyboard,
        );
    }

    /**
     * The calculations performed here assume the scroll view takes up the entire
     * screen - even if has some content inset. We then measure the offsets of the
     * keyboard, and compensate both for the scroll view's "contentInset".
     *
     * @param {number} left Position of input w.r.t. table view.
     * @param {number} top Position of input w.r.t. table view.
     * @param {number} width Width of the text input.
     * @param {number} height Height of the text input.
     */
    scrollResponderInputMeasureAndScrollToKeyboard(
        left,
        top,
        width,
        height,
    ) {
        let keyboardScreenY = Dimensions.get('window').height;
        if (this.keyboardWillOpenTo) {
            keyboardScreenY = this.keyboardWillOpenTo.endCoordinates.screenY;
        }
        let scrollOffsetY =
            top - keyboardScreenY + height + this.additionalScrollOffset;

        // By default, this can scroll with negative offset, pulling the content
        // down so that the target component's bottom meets the keyboard's top.
        // If requested otherwise, cap the offset at 0 minimum to avoid content
        // shifting down.
        if (this.preventNegativeScrollOffset) {
            scrollOffsetY = Math.max(0, scrollOffsetY);
        }
        this.scrollResponderScrollTo({ x: 0, y: scrollOffsetY, animated: true });

        this.additionalOffset = 0;
        this.preventNegativeScrollOffset = false;
    }

    scrollResponderTextInputFocusError(msg) {
        console.error('Error measuring text field: ', msg);
    }
}
