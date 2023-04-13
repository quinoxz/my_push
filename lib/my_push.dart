
import 'my_push_platform_interface.dart';

class MyPush {
  Future<String?> getPlatformVersion() {
    return MyPushPlatform.instance.getPlatformVersion();
  }
}
