#!/usr/bin/env python
# -*- coding: utf-8 -*-

# 为生成的 apk 添加 channel 信息
# http://tech.meituan.com/mt-apk-packaging.html
import shutil
import zipfile
import os
import sys
import time
import commands

# 定义需要打哪些渠道包，名称必须是合法的文件名（不能有空格等特殊字符）
APK_OUTPUT_DIR = 'app/build/outputs/apk/'
channels = [
  "360",
  "91",
  "anzhi",
  "anzhou",
  "baidu",
]

# 获取打包的渠道。渠道可以通过命令行指定，每个参数都是一个渠道，如果不指定，则使用默认的 channels
def getChannels():
  if len(sys.argv) > 1:
    # 「--」开头的是参数
    return sys.argv[1:]
  return channels

# 获取打包生成的 apk
def getApkPath():
  apk_files = os.listdir(APK_OUTPUT_DIR)
  for file_path in apk_files:
    if file_path.endswith('-release.apk'):
      return APK_OUTPUT_DIR + file_path;
    if file_path.endswith('-beta.apk'):
      return APK_OUTPUT_DIR + file_path;
  raise Exception('xxx-release.apk not found')

# 为每个渠道拷贝一个 apk，并把渠道名作为一个文件添加进去
def addChannel():
  origin_apk_path = getApkPath()
  print ('origin_apk_path: ' + origin_apk_path)
  # 创建一个空文件，来放到 apk 里
  empty_channel_file_path = 'empty'
  open(empty_channel_file_path, 'a').close()

  for target_channel in getChannels():
    prefix_len = len('app/build/outputs/apk/keep-v_')
    postfix_index = origin_apk_path.rindex('_')
    today_date = time.strftime("%Y-%m-%d")
    build = commands.getstatusoutput('git rev-list --count HEAD')[1]
    version_name = today_date + "_" + origin_apk_path[prefix_len:postfix_index] + "." + str(build)
    target_apk_path = APK_OUTPUT_DIR + "keep-" + target_channel + "-release-v_" + version_name + ".apk"
    print ('Add channel to: ' + target_apk_path)

    shutil.copy2(origin_apk_path, target_apk_path)
    zipped = zipfile.ZipFile(target_apk_path, 'a', zipfile.ZIP_DEFLATED)
    target_channel_file_path = "META-INF/channel_{channel}".format(channel=target_channel)
    zipped.write(empty_channel_file_path, target_channel_file_path)
    zipped.close

  os.remove(empty_channel_file_path)
  os.remove(origin_apk_path)
  unaligned_file_path = origin_apk_path[:-4] + "-unaligned.apk"
  os.remove(unaligned_file_path)

addChannel()
