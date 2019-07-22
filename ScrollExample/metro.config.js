/**
 * Metro configuration for React Native
 * https://github.com/facebook/react-native
 *
 * @format
 */

const path = require('path');
const blacklist = require('metro-config/src/defaults/blacklist');

module.exports = {
  transformer: {
    getTransformOptions: async () => ({
      transform: {
        experimentalImportSupport: false,
        inlineRequires: false,
      },
    }),
    },
    resolver: {
        /*
        providesModuleNodeModules: Object.keys(pkg.dependencies),
        extraNodeModules: {
            'react-native-scroll-view': path.resolve(__dirname, '..')
        },
        */
        extraNodeModules: {
            '@babel/runtime': path.resolve(__dirname, 'node_modules/@babel/runtime'),
            'react': path.resolve(__dirname, 'node_modules/react'),
            'lodash': path.resolve(__dirname, 'node_modules/lodash'),
            'prop-types': path.resolve(__dirname, 'node_modules/prop-types'),
            'events': path.resolve(__dirname, 'node_modules/events'),
            //'@react-native-community/async-storage': path.resolve(__dirname, '../node_modules/@react-native-community/async-storage'),
            //'react-native-webview': path.resolve(__dirname, '../node_modules/react-native-webview'),
        },
        blacklistRE: blacklist([
            //path.resolve(__dirname, '..', 'node_modules'),
            path.resolve(__dirname, '..', 'ScrollExample'),
        ]),
    },
    watchFolders: [path.resolve(__dirname, '..')],
};
