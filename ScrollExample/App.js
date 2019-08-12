/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React, { Component } from 'react';
import { Platform, StyleSheet, Text, View, Image, ScrollView as RNScrollView, Animated, Button, Dimensions } from 'react-native';
import ViewPager from '@react-native-community/viewpager';
import { createNativeWrapper, RectButton } from 'react-native-gesture-handler';
import * as _ from 'lodash';
//import { State, PanGestureHandler, PinchGestureHandler } from 'react-native-gesture-handler';

const instructions = Platform.select({
  ios: 'Press Cmd+R to reload,\n' + 'Cmd+D or shake for dev menu',
  android:
    'Double tap R on your keyboard to reload,\n' +
    'Shake or press menu button for dev menu',
});

import CustomView from 'react-native-scroll-view';
const GHCV = createNativeWrapper(CustomView, { shouldActivateOnStart: true, disallowInterruption: true });
const GHViewPager = createNativeWrapper(ViewPager, { shouldActivateOnStart: false, disallowInterruption: false });

type Props = {};
export default class App extends Component<Props> {
  state = { a: 150, page: 0, add: false }
  scrollRefs = [React.createRef(), React.createRef()];
  selectedPage = 0;
  componentDidMount() {
    //setTimeout(() => this.setState({ a: 200 }), 5000);
    setTimeout(() => this.setState({ add: true }), 5000);
  }
  render() {
    return (
      <View style={{ flex: 1 }}>
        <CustomView
          //pointerEvents="none"
          onHandlerStateChange={e => console.log(State.print(e.nativeEvent.state))}
          onGestureEvent={e => console.log(State.print(e.nativeEvent.state))}
          //enabled={false}
          //style={{ backgroundColor: 'blue' }}
          // minimumZoomScale={0.15}
          maximumZoomScale={5}
        //dispatchScrollEvents={false}
        >
          <Image
            style={{ width: 300, height: 300 }}
            source={{ uri: 'https://cdn.lynda.com/course/483230/483230-636529267515404332-16x9.jpg', width: 300, height: 300 }}
          />
          <Image
            style={{ width: 300, height: 300 }}
            source={{ uri: 'https://cdn.lynda.com/course/483230/483230-636529267515404332-16x9.jpg', width: 300, height: 300 }}
          />
          <Image
            style={{ width: 300, height: 300 }}
            source={{ uri: 'https://cdn.lynda.com/course/483230/483230-636529267515404332-16x9.jpg', width: 300, height: 300 }}
          />
          <Image
            style={{ width: 300, height: 300 }}
            source={{ uri: 'https://cdn.lynda.com/course/483230/483230-636529267515404332-16x9.jpg', width: 300, height: 300 }}
          />
        </CustomView>
      </View>
    );
  }

  rrrr = React.createRef();

  renderPage(index) {
    return (
      <GHCV
        //pointerEvents="none"
        onHandlerStateChange={e => console.log(State.print(e.nativeEvent.state))}
        onGestureEvent={e => console.log(State.print(e.nativeEvent.state))}
        //enabled={false}
        style={{ backgroundColor: 'red', flex: 1,/* transform: [{ translateX: 50 }, { scaleX: 0.5 }, { scaleY: 0.5 }]  /* top: this.state.a, /*flexWrap: 'wrap', flexDirection: 'row', alignItems: 'center', justifyContent: 'center',transform: [{ scaleX:1 }, { scaleY: 1 }, {rotateX:'40deg'}] */ }}
        minimumZoomScale={0.15}
        maximumZoomScale={3}
        zoomScale={index + 1}
        key={`customview${index}`}
        ref={this.scrollRefs[index]}
        onLayout={e => console.log('!!!!', e.nativeEvent)}
        onMomentumScrollBegin={e => console.log('!onMomentumBeginDrag!!!', e.nativeEvent)}
        onScroll={(e) => console.log(e.nativeEvent)}
        onScrollBeginDrag={(e) => console.log('begin', e.nativeEvent)}
        onScrollEndDrag={(e) => console.log('end', e.nativeEvent)}
        waitFor={this.rrrr}
      //centerContent
      >
        <Text style={[StyleSheet.absoluteFill, { zIndex: 1 }]}>{index + 1}</Text>
        <RectButton
          title="test me"
          onPress={() => console.log('button press inside')}
          style={{ width: 360, height: 200, backgroundColor: 'pink' }}
          enabled
          disallowInterruption
          //exclusive
          ref={this.rrrr}
          shouldActivateOnStart
          onHandlerStateChange={(e) => console.log('button press inside', e.nativeEvent)}
        >
          <Text>"test me"</Text>
        </RectButton>
        <Button
          title="test me"
          onPress={() => console.log('button press inside')}
        />
        {this.state.add && <Text>Added View</Text>}
        <Image
          style={{ width: 300, height: 300, opacity: 0.2 }}
          source={{ uri: 'https://cdn.lynda.com/course/483230/483230-636529267515404332-16x9.jpg', width: 300, height: 300 }}
        />
        <Text>"test me"</Text>
        <Image
          style={{ width: 300, height: 300 }}
          source={{ uri: 'https://cdn.lynda.com/course/483230/483230-636529267515404332-16x9.jpg', width: 300, height: 300 }}
        />

        <Image
          style={{ width: 300, height: 300 }}
          source={{ uri: 'https://www.worldatlas.com/r/w728-h425-c728x425/upload/0e/3c/e9/saharah-desert-sand-dunes.jpg', width: 300, height: 300 }}
        />
        <Image
          style={{ width: 300, height: 300 }}
          source={{ uri: 'https://cdn.lynda.com/course/483230/483230-636529267515404332-16x9.jpg', width: 300, height: 300 }}
        />
      </GHCV>

    );
  }

  getCurrentScrollRef() {
    return this.scrollRefs[this.selectedPage].current;
  }

  onPress = () => {
    const { width, height } = Dimensions.get('window');
    //this.getCurrentScrollRef().scrollToEnd();
    //this.getCurrentScrollRef().scrollTo({x: 200, y: 50});
    this.getCurrentScrollRef().getScrollResponder().scrollResponderZoomTo({ x: 50, y: 200, width: 100, height: 100, animated: true });
  }

  onPress1 = () => {
    const { width, height } = Dimensions.get('window');
    this.getCurrentScrollRef().scrollBy({ x: 0, y: 100, animated: true });
    //this.getCurrentScrollRef().getScrollResponder().scrollResponderZoomTo({x: 50, y: 0, width, height: height - this.state.a, animated: true});
  }

  renderPager() {
    return (
      <View style={{ flex: 1 }}>
        <GHViewPager
          style={{ flex: 1 }}
          onPageSelected={({ nativeEvent: { position } }) => this.selectedPage = position}
          waitFor={this.scrollRefs}
        >
          <View>{this.renderPage(0)}</View>
          <View>{this.renderPage(1)}</View>
        </GHViewPager>
      </View>
    );
  }

  renderFreakShow() {
    return (
      <View style={{ flex: 1, transform: [{ translateX: 50 }, { scaleX: 0.5 }, { scaleY: 0.5 }] }}>
        <Button
          onPress={this.onPress}
          title='scrollTo'
        />
        <Button
          onPress={this.onPress1}
          title='scrollTo'
          hitSlop={{ bottom: 0 }}
        />
        <View style={{ width: 360, height: 50, backgroundColor: 'green' }} collapsable={false} />
        <View style={{ flex: 1 }}>{this.renderPage(0)}</View>
        <View style={{ width: 360, height: 250, backgroundColor: 'green' }} collapsable={false} />
      </View>
    );
  }

  renderStandard() {
    return (
      <View style={{ flex: 1 }}>
        <View style={{ width: 360, height: 50, backgroundColor: 'green' }} collapsable={false} />
        <Button
          onPress={this.onPress}
          title='scrollTo'
        />
        <View style={{ flex: 1 }}>{this.renderPage(0)}</View>
        <Button
          onPress={this.onPress}
          title='scrollTo'
        />
        <Button
          onPress={this.onPress1}
          title='scrollTo'
        />
      </View>
    );
  }

  render__() {
    return (
      <PanGestureHandler>
        <Animated.View
          collapsable={false}
        >
          <PinchGestureHandler>
            <Image
              source={{ uri: 'https://cdn.lynda.com/course/483230/483230-636529267515404332-16x9.jpg', width: 300, height: 300 }}
            />
          </PinchGestureHandler>
        </Animated.View>
      </PanGestureHandler>
    );
  }

  go = (page) => {
    this.setState({ page });
  }

  renderPlain() {
    return (
      <View style={{ flex: 1,/* transform: [{ translateX: 50 }, { scaleX: 0.5 }, { scaleY: 0.5 }]*/ }}>
        <View style={{ width: 360, height: 50, backgroundColor: 'green' }} collapsable={false} onTouchMove={(e) => console.log('ffdfdfdf', e.nativeEvent)} />
        <Button
          onPress={() => console.log('button press scrollTo')}
          title='scrollTo'
        />
        <RectButton
          onPress={() => console.log('button press inside')}
          style={{ width: 360, height: 200, backgroundColor: 'pink' }}
          enabled
          disallowInterruption
          exclusive
          ref={this.rrrr}
          shouldActivateOnStart
          onHandlerStateChange={(e) => console.log('button press inside', e.nativeEvent)}
          title="nonw"
        >
          <Text>"test me"</Text>
        </RectButton>
      </View>
    );
  }

  renderMenu() {
    return (
      <View style={{ flex: 1, alignItems: 'stretch', justifyContent: 'space-around' }}>
        <Button
          title="Android ViewPager"
          onPress={() => this.go(1)}
        />
        <Button
          title="Freaky Layout"
          onPress={() => this.go(2)}
        />
        <Button
          title="Standard Use Case"
          onPress={() => this.go(3)}
        />
      </View>
    );
  }

  getPage() {
    switch (this.state.page) {
      default:
      case 0: return this.renderMenu();
      case 1: return this.renderPager();
      case 2: return this.renderFreakShow();
      case 3: return this.renderStandard();
      case 4: return this.renderPlain()
    }
  }


  render() {
    return (
      <>
        {this.getPage()}
        {
          this.state.page !== 0 &&
          <Button
            style={{ zIndex: 1 }}
            title="Back"
            onPress={() => this.go(0)}
          />
        }
      </>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});
