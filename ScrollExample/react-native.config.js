//  https://github.com/react-native-community/cli/blob/5199d6af1aa6dc5d8dfb4a98e675987272d68998/docs/configuration.md

const path = require('path');
const root = path.resolve(__dirname, '..');

module.exports = {
    dependencies: {
        'react-native-scroll-view': {
            root,
        },
        'react-native-gesture-handler': {
            root: path.resolve(root, './node_modules/react-native-gesture-handler'),
            platforms: {
                android: {
                    packageImportPath: "import com.swmansion.gesturehandler.react.RNGestureHandlerPackage;",
                    packageInstance: "new RNGestureHandlerPackage()"
                },
                ios: null
            },
        },
    },
};