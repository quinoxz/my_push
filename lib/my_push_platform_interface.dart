import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'my_push_method_channel.dart';

abstract class MyPushPlatform extends PlatformInterface {
  /// Constructs a MyPushPlatform.
  MyPushPlatform() : super(token: _token);

  static final Object _token = Object();

  static MyPushPlatform _instance = MethodChannelMyPush();

  /// The default instance of [MyPushPlatform] to use.
  ///
  /// Defaults to [MethodChannelMyPush].
  static MyPushPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [MyPushPlatform] when
  /// they register themselves.
  static set instance(MyPushPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
