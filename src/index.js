
import {
    Platform,
    ScrollView as RNScrollView,
} from 'react-native';
import ScrollViewImplementation from './ZoomageView';

const DefaultScrollView = Platform.OS === 'ios' ? RNScrollView : ScrollViewImplementation;

export default DefaultScrollView;
