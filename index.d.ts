import { ScrollView as RNScrollView, ViewStyle, ScrollViewProps as RNScrollViewProps,ScrollViewPropsIOS, Platform } from 'react-native';

type Point = {
    x: number,
    y: number
}

//export type Extract<U, T> = U extends T ? U : never;

export interface IScrollViewProps extends ScrollViewPropsIOS {
    indicatorStyle: ViewStyle;
}

export type UScrollViewProps = IScrollViewProps | ScrollViewPropsIOS;

export default class ScrollView<UScrollViewProps> extends RNScrollView {
    props: UScrollViewProps;

    scrollTo(options: {
        x: number,
        y: number,
        animated?: boolean,
        scale?: number,
        overScroll?: boolean | Point
        callback?: Function
    }): void;

    scrollToEnd(options: { animated?: boolean } | undefined): void;
    scrollToEnd(options: {
        animated?: boolean | undefined,
        callback?:Function
    }): void;

    scrollResponderZoomTo(options: {
        x: number,
        y: number,
        width: number,
        height: number,
        animated?: boolean,
        callback?: Function
    }): void;

    scrollResponderScrollNativeHandleToKeyboard(reactNode: number, extraHeight: number, preventNegativeScrollOffset?: boolean): void;

    getNode(): number;

    getScrollResponder(): JSX.Element;
    getScrollResponder(): ScrollView<UScrollViewProps>;

    getScrollRef(): ScrollView<UScrollViewProps>;

}
