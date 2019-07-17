/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React, { Component } from 'react';
import { Platform, StyleSheet, Text, View, Image, ScrollView as RNScrollView } from 'react-native';
import { createNativeWrapper, State } from 'react-native-gesture-handler';

const instructions = Platform.select({
    ios: 'Press Cmd+R to reload,\n' + 'Cmd+D or shake for dev menu',
    android:
        'Double tap R on your keyboard to reload,\n' +
        'Shake or press menu button for dev menu',
});

import CustomView from 'react-native-scroll-view';

const GHCV = createNativeWrapper(CustomView, {shouldActivateOnStart: true, enabled: false});

type Props = {};
export default class App extends Component<Props> {
    render1() {
        return (
            <CustomView
                style={{ flex: 1 }}
                //contentContainerStyle={styles.container}
                onScroll={e => console.log(e.nativeEvent)}
                scrollEnabled
                minimumZoomScale={1}
                maximumZoomScale={5}
            >
                <View>
                    <Text style={styles.welcome}>Welcome to React Native!</Text>
                    <Text style={styles.instructions}>To get started, edit App.js</Text>
                    <Text style={styles.instructions}>{instructions}</Text>
                    <Image
                        style={{ width: 300, height: 300 }}
                        source={{ uri: 'https://cdn.lynda.com/course/483230/483230-636529267515404332-16x9.jpg' }}
                    />
                    <Image
                        style={{ width: 300, height: 300 }}
                        source={{ uri: 'https://cdn.lynda.com/course/483230/483230-636529267515404332-16x9.jpg' }}
                    />
                </View>
            </CustomView>
        );
    }

    render() {
        return (
            <GHCV
                //pointerEvents="none"
                onHandlerStateChange={e => console.log(State.print(e.nativeEvent.state))}
                onGestureEvent={e => console.log(State.print(e.nativeEvent.state))}
                //enabled={false}
            />
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
