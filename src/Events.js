import { NativeModules } from 'react-native';
const { UIManager = {} } = NativeModules;

const customEventsConfig = {
    onScrollBeginDrag: { registrationName: 'onScrollBeginDrag' },
    onScroll: { registrationName: 'onScroll' },
    onScrollEndDrag: { registrationName: 'onScrollEndDrag' },
};

// Add gesture specific events to genericDirectEventTypes object exported from UIManager
// native module.
// Once new event types are registered with react it is possible to dispatch these
// events to all kind of native views.
UIManager.genericDirectEventTypes = {
    ...UIManager.genericDirectEventTypes,
    ...customEventsConfig,
};

/*
// Wrap JS responder calls and notify gesture handler manager
const {
    setJSResponder: oldSetJSResponder = () => { },
    clearJSResponder: oldClearJSResponder = () => { },
    getConstants: oldGetConstants = () => ({}),
} = UIManager;
UIManager.setJSResponder = (tag, blockNativeResponder) => {
    RNGestureHandlerModule.handleSetJSResponder(tag, blockNativeResponder);
    oldSetJSResponder(tag, blockNativeResponder);
};
UIManager.clearJSResponder = () => {
    RNGestureHandlerModule.handleClearJSResponder();
    oldClearJSResponder();
};
*/


// We also add GH specific events to the constants object returned by
// UIManager.getConstants to make it work with the newest version of RN
UIManager.getConstants = () => {
    const constants = oldGetConstants();
    return {
        ...constants,
        genericDirectEventTypes: {
            ...constants.genericDirectEventTypes,
            ...customEventsConfig,
        },
    };
};