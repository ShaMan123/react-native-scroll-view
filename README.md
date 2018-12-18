# react-native-scroll-view
A react-native component for android that mimics the react-native iOS `ScrollView`.

- android: **ZOOM and SCROLL** content easily *(isn't supported by the react-native `ScrollView`)*.
- iOS: exports the react-native `ScrollView` *(does nothing!)*

Refer to the react-native `ScrollView` [documentation](https://facebook.github.io/react-native/docs/scrollview#props).

## Installation
```
npm install --save @ShaMan123/react-native-scroll-view
```
OR
```
yarn add @ShaMan123/react-native-scroll-view
```

## Usage

```
import ScrollView from 'react-native-scroll-view';

<ScrollView
  ref={ref => this.ref = ref}
  minimumZoomScale={0.75}
  maximumZoomScale={3}
  zoomScale={1.5}
>
  {...contentToRender}
</ScrollView>

zoomToRect() {
  this.ref.getScrollResponder().scrollResponderZoomTo({ x: 0, y: 0, width: 100, height: 100});
}

setOverScroll() {
  this.ref.getScrollResponder()
    .scrollResponderScrollNativeHandleToKeyboard(React.findNodeHandle(this.ref), 100);
}
```

## Props
- [ ] alwaysBounceVertical
- [x] contentContainerStyle
- [ ] keyboardDismissMode
- [ ] keyboardShouldPersistTaps
- [x] onContentSizeChange
- [x] onMomentumScrollBegin
- [x] onMomentumScrollEnd
- [x] onScroll
- [x] onScrollBeginDrag
- [x] onScrollEndDrag
- [ ] pagingEnabled
- [ ] refreshControl
- [ ] removeClippedSubviews
- [x] scrollEnabled
- [x] showsHorizontalScrollIndicator
- [x] showsVerticalScrollIndicator
- [ ] stickyHeaderIndices
- [ ] endFillColor
- [ ] overScrollMode
- [ ] scrollPerfTag
- [ ] DEPRECATED_sendUpdatedChildFrames
- [ ] alwaysBounceHorizontal
- [ ] horizontal
- [ ] automaticallyAdjustContentInsets
- [x] bounces
- [ ] bouncesZoom
- [ ] canCancelContentTouches
- [x] centerContent
- [x] contentInset
- [ ] contentInsetAdjustmentBehavior
- [x] contentOffset
- [x] decelerationRate
- [x] directionalLockEnabled
- [x] indicatorStyle: differs from the *iOS* prop => accepts a style object
- [x] maximumZoomScale
- [x] minimumZoomScale
- [x] pinchGestureEnabled
- [ ] scrollEventThrottle
- [ ] scrollIndicatorInsets
- [ ] scrollsToTop
- [ ] snapToAlignment
- [ ] snapToInterval
- [x] zoomScale
- [ ] nestedScrollEnabled

## Methods
| Method  | Description |
| :------------ |:---------------| 
| `scrollTo({ x, y, animated?, scale?, overScroll?, callback?})` | see `ScrollView`'s [scrollTo](https://facebook.github.io/react-native/docs/scrollview#scrollto). Added optional arguments: `scale`, `overScroll`, `callback` |
| `scrollToEnd({ animated?, callback?})` | see `ScrollView`'s [scrollTo](https://facebook.github.io/react-native/docs/scrollview#scrolltoend). Added optional arguments: `callback` |
| `scrollResponderZoomTo({ x, y, width, height, animated?, callback?})` |  see [issue](https://github.com/facebook/react-native/issues/9830) |
| `flashScrollIndicators()` |  see `ScrollView`'s [flashScrollIndicators](https://facebook.github.io/react-native/docs/scrollview#flashscrollindicators) |
| `scrollResponderScrollNativeHandleToKeyboard(reactNode?, extraHeight, preventNegativeScrollOffset?)` |  see [issue](https://github.com/facebook/react-native/issues/3195)  |
| `getNode()` |  the same as `findNodeHandle(componentRef)`  |
| `getScrollResponder()` |  a dummy method pointing back to the component, used for chaining, enables cross platform compatibility  |
| `getScrollRef()` |  a dummy method pointing back to the component, used for chaining, enables cross platform compatibility  |
