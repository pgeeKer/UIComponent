#!/bin/bash

# 打 Debug 包

# Save current working directory.
CWD="$(pwd)"

# 包名
bundleIdentifier="com.gotokeep.keep"
echo "bundleIdentifier: $bundleIdentifier"

# Build
rm -rf ./app/build/outputs/apk/*
echo "gradle"
cp $1 ./app/keep.jks
./gradlew clean app:assembleFortest -x lint --no-daemon
[[ $? -gt 0 ]] && exit 1

# Copy apk
dst_dir="$HOME/built-apks/apks"
mkdir -p $dst_dir
echo "Get last commit sha"
sha=`git rev-parse --short=6 HEAD`
echo ${sha}
build_debug_apk="./app/build/outputs/apk/*fortest.apk"
version=`ls ${build_debug_apk} | cut -d _ -f 2`
build=`git rev-list --count HEAD`
true_file="${dst_dir}/keep-${version}v${build}-${sha}.apk"
if [ $2 == "--run" ]; then
    true_file="${dst_dir}/keep-run-${version}v${build}-${sha}.apk"
elif [ $2 == "--cycling" ]; then
    true_file="${dst_dir}/keep-cycling-${version}v${build}-${sha}.apk"
fi
cp ${build_debug_apk} "${true_file}"

# Update change log
last_build_sha=`cat /Users/keeppro/built-apks/apks/__last_build__`

if [[ $last_build_sha == "" ]];then
    msg=`git log --pretty=format:"%s" -10`
elif [[ $last_build_sha == $sha ]];then
    exit 0
else
    msg=`git log --pretty=format:"%s" ${last_build_sha}...HEAD`
fi

echo $sha > "${dst_dir}/__last_build__"
echo "$msg" | sed 's/^/  * /' > ${true_file}.txt

# 注释掉上传 mapping 到 bugly
# Upload proguard mapping file onto bugly.
# curl --header "Content-Type: text/plain" --data-binary "@${CWD}/app/build/outputs/mapping/release/mapping.txt"  "http://bugly.qq.com/upload/map?pid=1&app=900003414&key=xnfvZWN8PY8zY4im&bid=${bundleIdentifier}&ver=v${version}-build${build}&n=mapping.txt" --verbose
exit 0
