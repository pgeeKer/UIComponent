#!/usr/bin/env bash

storeFile="/etc/keep.jks" #Jenkins上Apk签名的地址
task_name="fortest" #默认打fortest包
channel="" #channel1,channel2 打特定channel的包 渠道之间用","分隔，不能有空格
apk_name=""
apk_version=""
bundleIdentifier="com.gotokeep.keep"

function show_usage() {
  echo ""
  echo "******************************使用帮助******************************"
  echo "-r file_path, 指定签名文件地址, 可以不填, 默认是/etc/keep.jks(Jenkins上的地址)"
  echo "-t task_name, 指定任务类型, fortest/beta/release/yunying"
  echo "-c channel_name1,channel_name2, 指定渠道名字"
  echo "-n apk_name, 指定最后打包的apk的名字的识别字段,给fortest使用"
  echo "-v version_name, 指定apk的version_name"
  echo "--switchOutdoorBeta, 开启跑步内测包的特定功能"
  echo "-h, show help"
  echo "*******************************************************************"
  echo ""
}

readCommand(){
    TEMP=`getopt -o r:t:c:n:v:: --long switchOutdoorBeta -- "$@"`

    if [ $? != 0 ] ; then echo "Terminating..." >&2 ; show_usage ; exit 1 ; fi
    `set -- "$TEMP"`
    echo $TEMP
    while true ; do
        [ -z "$1" ] && break;
        case "$1" in
            -r) storeFile=$2
                if [ -z ${storeFile} ]; then
                  echo "-r parameter is NULL"
                  exit 1
                fi
                shift 2;;
            -t) task_name=$2
                if [ -z ${task_name} ]; then
                  echo "-t parameter is NULL"
                  exit 1
                fi
                shift 2;;
            -c) channel=$2;
                if [ -z ${channel} ]; then
                      echo "-c parameter is NULL"
                      exit 1
                    fi
                shift 2;;
            -n) apk_name=$2;
                if [ -z ${apk_name} ]; then
                      echo "-n parameter is NULL"
                      exit 1
                    fi
                shift 2;;
            -v) apk_version=$2;
                if [ -z ${apk_version} ]; then
                      echo "-v parameter is NULL"
                      exit 1
                fi
                shift 2;;
            -h) show_usage;
                exit 0;;
            --switchOutdoorBeta) switchOutdoorBeta="switchOutdoorBeta";
                shift;;
            --);;
            *) echo "Internal error!" ; show_usage ; exit 1;;
        esac
    done
}

get_build_task(){
    task_prefix="app:assemble"
    if [ ${task_name} = "fortest" ]; then
        task="${task_prefix}Fortest"
    elif [ ${task_name} = "beta" ]; then
        task="${task_prefix}Beta"
    elif [ ${task_name} = "release" ]; then
        task="${task_prefix}Release"
    else
        task="${task_prefix}Fortest"
    fi
}

for_test(){
    dst_dir="$HOME/built-apks/apks"
    mkdir -p ${dst_dir}
    sha=`git rev-parse --short=6 HEAD`
    build_debug_apk="./app/build/outputs/apk/*fortest.apk"
    version=`ls ${build_debug_apk} | cut -d _ -f 2`
    build=`git rev-list --count HEAD`
    true_file="${dst_dir}/keep-${version}v${build}-${sha}.apk"
    if [ -n "${apk_name}" ]; then
        #如果指定了apk的名字
        true_file="${dst_dir}/keep-${apk_name}-${version}v${build}-${sha}.apk"
    fi
    cp ${build_debug_apk} "${true_file}"

    ## Update change log
    #last_build_sha=`cat /Users/keeppro/built-apks/apks/__last_build__`
    #if [[ $last_build_sha == "" ]];then
    #    msg=`git log --pretty=format:"%s" -10`
    #elif [[ $last_build_sha == $sha ]];then
    #    exit 0
    #else
    #    msg=`git log --pretty=format:"%s" ${last_build_sha}...HEAD`
    #fi
    #echo $sha > "${dst_dir}/__last_build__"
    #echo "$msg" | sed 's/^/  * /' > ${true_file}.txt
    ## 注释掉上传 mapping 到 bugly
    ## Upload proguard mapping file onto bugly.3
    ## curl --header "Content-Type: text/plain" --data-binary "@${CWD}/app/build/outputs/mapping/release/mapping.txt" "http://bugly.qq.com/upload/map?pid=1&app=900003414&key=xnfvZWN8PY8zY4im&bid=${bundleIdentifier}&ver=v${version}-build${build}&n=mapping.txt" --verbose
    exit 0
}

build(){
    #打包
    rm -rf ./app/build/outputs/apk/*
    cp $storeFile ./app/keep.jks
    get_build_task
    gradle_command="./gradlew clean ${task} -x lint --no-daemon"
    if [ -n "${switchOutdoorBeta}" ]; then
        gradle_command="${gradle_command} -PswitchOutdoorBeta=true "
    fi
    if [ -n "${apk_version}" ]; then
        gradle_command="${gradle_command} -PcustomVersionName=${apk_version} "
    fi
    echo ${gradle_command}
    ${gradle_command}
    if [ $? != 0 ]; then exit 1; fi

    #gradle 3.0之后把apk子目录中的apk文件移动到上一级
    mv ./app/build/outputs/apk/*/* ./app/build/outputs/apk/

    if [ $task_name = fortest ]; then
    #fortest特殊处理
        for_test
        if [ $? != 0 ]; then
            exit 1;
        else
            exit 0;
        fi
    fi

    #处理渠道
    channel_split=${channel//,/ }
    echo ${channel_split}
    python_command="python channel.py"
    for element in ${channel_split}
    do
        python_command="${python_command} ${element}"
    done
    echo ${python_command}
    ${python_command}

    if [ ${task_name} = beta ]; then
        cd ./app/build/outputs/apk
        if [ -n "${switchOutdoorBeta}" ]; then
            dst_dir="$HOME/built-apks/androidRunningBeta"
        else
            dst_dir="$HOME/built-apks/androidBeta"
        fi
        version=`ls *.apk | head -1 | awk -F'_' '{print $NF}' | sed 's/.apk//'`
        v_dir="${dst_dir}/${version}"
    elif [ ${task_name} = release ]; then
        cd ./app/build/outputs/apk
        dst_dir="$HOME/built-apks/androidRelease"
        version=`ls *.apk | head -1 | awk -F'_' '{print $NF}' | sed 's/.apk//' | sed s'/.[0-9]*$//'`
        v_dir="${dst_dir}/${version}"
        zip Keep-Release_${version}.zip *.apk
        if [[ -d ${v_dir} ]];then
            rm -f ${v_dir}/*
        fi
    #elif [ ${task_name} = yunying ]; then
    #    cd ./app/build/outputs/apk
    #    dst_dir="$HOME/built-apks/androidRelease"
    #    version=`ls *.apk | head -1 | awk -F'_' '{print $NF}' | sed 's/.apk//' | sed s'/.[0-9]*$//'`
    #    v_dir="${dst_dir}/${version}/yunying"
    fi
    mkdir -p ${dst_dir}
    echo ${v_dir}
    mkdir -p ${v_dir}
    mv *.apk ${v_dir}
    mv *.zip ${v_dir}
    build=`git rev-list --count HEAD`
    # 注释掉上传 mapping 到 bugly
    # Upload proguard mapping file onto bugly.3
    # curl --header "Content-Type: text/plain" --data-binary "@${CWD}/app/build/outputs/mapping/release/mapping.txt"  "http://bugly.qq.com/upload/map?pid=1&app=900003414&key=xnfvZWN8PY8zY4im&bid=${bundleIdentifier}&ver=v${version}-build${build}&n=mapping.txt" --verbose
}


main(){
   readCommand $@
   build
}

main $@