
import {
    Platform,
    ScrollView as RNScrollView
} from 'react-native';
import { ScrollView } from 'react-native-gesture-handler';
import ScrollViewImplementation from './ScrollView';

const DefaultScrollView = Platform.OS === 'ios' ? ScrollView : ScrollViewImplementation;

//import ScrollView from '@shaman123/react-native-scroll-view';

export default DefaultScrollView;
