import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'my_push_platform_interface.dart';

/// An implementation of [MyPushPlatform] that uses method channels.
class MethodChannelMyPush extends MyPushPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('my_push');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
