/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React, { Component } from 'react';
import { Platform, StyleSheet, Text, View, Image, ScrollView as RNScrollView, Animated, Button, Dimensions, I18nManager } from 'react-native';
import ViewPager from '@react-native-community/viewpager';
import { createNativeWrapper } from 'react-native-gesture-handler';
import * as _ from 'lodash';
//import { State, PanGestureHandler, PinchGestureHandler } from 'react-native-gesture-handler';

I18nManager.allowRTL(false);
I18nManager.forceRTL(false)

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
    state = { a: 150 }
    scrollRefs = [React.createRef(), React.createRef()];
    selectedPage = 0;
    componentDidMount() {
        //setTimeout(() => this.setState({ a: 200 }), 5000);
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

    renderPage(index) {
        return (
            <GHCV
                //pointerEvents="none"
                onHandlerStateChange={e => console.log(State.print(e.nativeEvent.state))}
                onGestureEvent={e => console.log(State.print(e.nativeEvent.state))}
                //enabled={false}
                style={{ backgroundColor: 'red', flex: 1,/* top: this.state.a, /*flexWrap: 'wrap', flexDirection: 'row', alignItems: 'center', justifyContent: 'center',transform: [{ scaleX:1 }, { scaleY: 1 }, {rotateX:'40deg'}] */ }}
                minimumZoomScale={0.15}
                maximumZoomScale={50}
                zoomScale={index + 1}
                key={`customview${index}`}
                ref={this.scrollRefs[index]}
                onLayout={e => console.log('!!!!', e.nativeEvent)}
                
            //centerContent
            >
                <Text style={[StyleSheet.absoluteFill, { zIndex: 1 }]}>{index + 1}</Text>
                <Image
                    style={{ width: 300, height: 300, opacity: 0.2 }}
                    source={{ uri: 'https://cdn.lynda.com/course/483230/483230-636529267515404332-16x9.jpg', width: 300, height: 300 }}
                />
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
        this.getCurrentScrollRef().scrollToEnd();
        //this.getCurrentScrollRef().scrollTo({x: 200, y: 1100});
        //this.getCurrentScrollRef().getScrollResponder().scrollResponderZoomTo({x: 50, y: 0, width, height: height - this.state.a, animated: true});
    }

    onPress1 = () => {
        const { width, height } = Dimensions.get('window');
        this.getCurrentScrollRef().scrollBy({x: 0, y: -100});
        //this.getCurrentScrollRef().getScrollResponder().scrollResponderZoomTo({x: 50, y: 0, width, height: height - this.state.a, animated: true});
    }

    render() {
        return (
            <View style={{ flex: 1 }}>
                <View style={{ width: 360, height: 50, backgroundColor: 'green' }} collapsable={false} />
                <GHViewPager
                    style={{ flex: 1, top: 150 }}
                    onPageSelected={({ nativeEvent: { position } }) => this.selectedPage = position}
                >
                    <View>{this.renderPage(0)}</View>
                    <View>{this.renderPage(1)}</View>
                </GHViewPager>
                <View style={{ width: 360, height: 50, backgroundColor: 'green' }} collapsable={false} />
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

    render1() {
        return (
            <View style={{ flex: 1 }}>
                <View style={{ width: 360, height: 250, backgroundColor: 'green' }} collapsable={false} />
                <View style={{flex:1}}>{this.renderPage(0)}</View>
                <View style={{ width: 360, height: 250, backgroundColor: 'green' }} collapsable={false} />
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
