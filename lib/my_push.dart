
import 'dart:io';

import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';
import 'package:platform/platform.dart';

import 'my_push_platform_interface.dart';

class MyPush {
  static const String flutter_log = "| MyPush | Flutter | ";
  factory MyPush() => _instance;
  final MethodChannel _channel;
  final Platform _platform;

  @visibleForTesting
  MyPush.private(MethodChannel channel, Platform platform)
      : _channel = channel,
        _platform = platform;

  static final MyPush _instance = MyPush.private(const MethodChannel('jpush'), const LocalPlatform());

  void initPush({
    String alias = '',
    String oppoAppId = '',
    String oppoAppSecret = '',
    String vivoAppId = '',
    String vivoAppKey = '',
    String xiaomiAppId = '',
    String xiaomiAppKey = '',
  }) {
    print("${flutter_log}setup:");
    _channel.invokeMethod('initPush', {
      'alias': alias,
      'oppoAppId': oppoAppId,
      'oppoAppSecret': oppoAppSecret,
      'vivoAppId': vivoAppId,
      'vivoAppKey': vivoAppKey,
      'xiaomiAppId': xiaomiAppId,
      'xiaomiAppKey': xiaomiAppKey,
    });
  }

}
