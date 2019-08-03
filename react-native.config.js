//  https://github.com/react-native-community/cli/blob/5199d6af1aa6dc5d8dfb4a98e675987272d68998/docs/configuration.md

//const path = require('path');
//const root = __dirname;

const path = require('path');

module.exports = {
    dependency: {
        platforms: {
            android: {
                packageImportPath: "import io.autodidact.zoomage.RNZoomagePackage;",
                packageInstance: "new RNZoomagePackage()"
            },
            ios: {}
        },
    },
    dependencies: {
        zoomable: {
            root: path.resolve(__dirname, 'Zoomable')
        }
    }
};
