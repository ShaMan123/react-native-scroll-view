import * as React from 'react';
import { ScrollView as RNScrollView, ViewStyle, ScrollViewProps as RNScrollViewProps, ScrollViewPropsIOS, Platform, ViewProps, Touchable, Animated } from 'react-native';

type Point = {
    x: number,
    y: number
}

export interface ScrollViewProps extends ScrollViewPropsIOS {
    /**
     * - Android only: pass a style
     */
    indicatorStyle: ViewStyle;
}

export type UScrollViewProps = (ScrollViewProps | ScrollViewPropsIOS) & ViewProps & Touchable;

type Constructor<T> = new (...args: any[]) => T;

declare class ScrollViewComponent extends React.Component<UScrollViewProps> { }
declare const ScrollViewBase: Constructor<RNScrollView & ScrollViewComponent> & typeof RNScrollView & typeof ScrollViewComponent;

export default class ScrollView extends ScrollViewBase {

    /*
     * @ShaMan123
     *  added options: 
     *  - scale: defaults to 1
     *  - overScroll: controls whether the ScrollView overscrolls to the given coords, pass a point to act as an upper bound for over-scrolling
     *  - callback: pass a callback to run after the animation finishes, fires `onScrollAnimationEnd` regardlessly
     * */
    scrollTo(options: {
        x: number,
        y: number,
        animated?: boolean,
        scale?: number,
        overScroll?: boolean | Point
        callback?: Animated.EndCallback
    }): void;

    /*
     * @ShaMan123
     *  added options: 
     *  - callback: pass a callback to run after the animation finishes, fires `onScrollAnimationEnd` regardlessly
     * */
    scrollToEnd(options?: {
        animated?: boolean | undefined,
        callback?: Animated.EndCallback
    }): void;

    /*
     * @ShaMan123
     *  added options: 
     *  - callback: pass a callback to run after the animation finishes, fires `onScrollAnimationEnd` regardlessly
     * */
    scrollResponderZoomTo(options: {
        x: number,
        y: number,
        width: number,
        height: number,
        animated?: boolean,
        callback?: Animated.EndCallback
    }): void;

    scrollResponderScrollNativeHandleToKeyboard(reactNode: number, extraHeight: number, preventNegativeScrollOffset?: boolean): void;

    getNode(): number;

    /*
     * @ShaMan123
     * - This is a stub pointing back to the ScrollView
     * 
     * */
    getScrollResponder(): JSX.Element;

    /*
     * @ShaMan123
     * - This is a stub pointing back to the ScrollView
     * */
    getScrollRef(): ScrollView;

}
