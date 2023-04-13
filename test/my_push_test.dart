import 'package:flutter_test/flutter_test.dart';
import 'package:my_push/my_push.dart';
import 'package:my_push/my_push_platform_interface.dart';
import 'package:my_push/my_push_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockMyPushPlatform
    with MockPlatformInterfaceMixin
    implements MyPushPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final MyPushPlatform initialPlatform = MyPushPlatform.instance;

  test('$MethodChannelMyPush is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelMyPush>());
  });

  test('getPlatformVersion', () async {
    MyPush myPushPlugin = MyPush();
    MockMyPushPlatform fakePlatform = MockMyPushPlatform();
    MyPushPlatform.instance = fakePlatform;

    expect(await myPushPlugin.getPlatformVersion(), '42');
  });
}
