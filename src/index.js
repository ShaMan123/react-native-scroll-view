
import {
    Platform,
    ScrollView as RNScrollView,
} from 'react-native';
import ScrollViewImplementation from './ZoomableScrollView';

const DefaultScrollView = Platform.OS === 'ios' ? RNScrollView : ScrollViewImplementation;

export default DefaultScrollView;
