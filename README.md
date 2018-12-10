# react-native-scroll-view
A react-native component for android that mimics the react-native iOS `ScrollView`.

- android: **ZOOM and SCROLL** content easily *(isn't supported by the react-native `ScrollView`)*.
- iOS: exports the react-native `ScrollView` *(does nothing!)*

Refer to the react-native `ScrollView` [documentation](https://facebook.github.io/react-native/docs/scrollview#props).

## Installation
```
npm install --save github:ShaMan123/react-native-scroll-view
```
OR
```
yarn add github:ShaMan123/react-native-scroll-view
```

## Usage

```
import ScrollView from 'react-native-scroll-view';

<ScrollView
  minimumZoomScale={0.75}
  maximumZoomScale={3}
  zoomScale={1.5}
>
  {...contentToRender}
</ScrollView>

```

## Props
- [ ] alwaysBounceVertical
- [ ] contentContainerStyle
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
- [ ] showsHorizontalScrollIndicator
- [ ] showsVerticalScrollIndicator
- [ ] stickyHeaderIndices
- [ ] endFillColor
- [ ] overScrollMode
- [ ] scrollPerfTag
- [ ] DEPRECATED_sendUpdatedChildFrames
- [ ] alwaysBounceHorizontal
- [ ] horizontal
- [ ] automaticallyAdjustContentInsets
- [ ] bounces
- [ ] bouncesZoom
- [ ] canCancelContentTouches
- [x] centerContent
- [ ] contentInset
- [ ] contentInsetAdjustmentBehavior
- [x] contentOffset
- [x] decelerationRate
- [x] directionalLockEnabled
- [ ] indicatorStyle
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
