/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React, { Component } from 'react';
import { Platform, StyleSheet, Text, View, Image, ScrollView as RNScrollView } from 'react-native';
import ViewPager from '@react-native-community/viewpager';
import { createNativeWrapper } from 'react-native-gesture-handler';
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
const GHViewPager = createNativeWrapper(ViewPager, { shouldActivateOnStart: true, disallowInterruption: true });

type Props = {};
export default class App extends Component<Props> {
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
                style={{ backgroundColor: 'red', flex: 1 }}
                minimumZoomScale={0.15}
                maximumZoomScale={5}
                key={`customview${index}`}
                enabled={false}
            >
                <Text>{index + 1}</Text>
                <Image
                    source={{ uri: 'https://cdn.lynda.com/course/483230/483230-636529267515404332-16x9.jpg', width: 300, height: 300 }}
                />
            </GHCV>
        );
    }

    render() {
        return (
            <GHViewPager
                style={{flex:1}}
            >
                <View>{this.renderPage(0)}</View>
                <View>{this.renderPage(1)}</View>
            </GHViewPager>
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
