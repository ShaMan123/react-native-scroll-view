
import {
    Platform,
    ScrollView as RNScrollView,
} from 'react-native';
import ScrollViewImplementation from './ScrollView';

const DefaultScrollView = Platform.OS === 'ios' ? RNScrollView : ScrollViewImplementation;

export default DefaultScrollView;
