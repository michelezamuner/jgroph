#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail
shopt -s dotglob
IFS=$'\n\t'

readonly jdk_ark='/vagrant/jdk.tar.gz'
readonly jdk_dir='/usr/local/jdk'

apt-get update >/dev/null
apt-get install -y vim curl sqlite3 >/dev/null

if [ ! -f "${jdk_ark}" ]; then
    echo 'Downloading local JDK archive...'
    wget \
        --no-check-certificate \
        --no-cookies \
        --header 'Cookie: oraclelicense=accept-securebackup-cookie' \
        'http://download.oracle.com/otn-pub/java/jdk/8u144-b01/090f390dda5b47b9b721c7dfaa008135/jdk-8u144-linux-x64.tar.gz' \
            -O "${jdk_ark}" >/dev/null 2>&1
fi

if [ ! -d "${jdk_dir}" ]; then
    echo 'Installing JDK...'
    mkdir -p "${jdk_dir}"
    tar -xzf "${jdk_ark}" --strip 1 -C "${jdk_dir}" >/dev/null 2>&1
    ln -s "${jdk_dir}" /etc/alternatives/java_sdk_1.8.0
fi

if [ -z "${JAVA_HOME+x}" ]; then
    echo 'Configuring Java environment...'
    echo '#!/bin/sh' > /etc/profile.d/custom.sh
    echo "export JAVA_HOME=${jdk_dir}" >> /etc/profile.d/custom.sh
    echo 'export PATH="${JAVA_HOME}/bin:/home/vagrant/maven/bin:${PATH}"' >> /etc/profile.d/custom.sh
    echo 'export OPENSHIFT_DATA_DIR=/home/vagrant/' >> /etc/profile.d/custom.sh
    echo 'export OPENSHIFT_REPO_DIR=/vagrant/' >> /etc/profile.d/custom.sh
    echo 'export OPENSHIFT_DIY_IP=0.0.0.0' >> /etc/profile.d/custom.sh
    echo 'export OPENSHIFT_DIY_PORT=8080' >> /etc/profile.d/custom.sh
    echo 'export OPENSHIFT_LOG_DIR=/home/vagrant/' >> /etc/profile.d/custom.sh

    readonly mem_b="$(grep MemTotal /proc/meminfo | awk '{print $2}')"
    readonly mem_m="$((mem_b / 1024))"
    readonly mem_q="$((mem_m / 4))"
    echo "export JAVA_MS=$((mem_q * 2))m" >> /etc/profile.d/custom.sh
    echo "export JAVA_MX=$((mem_q * 3))m" >> /etc/profile.d/custom.sh
    chmod +x /etc/profile.d/custom.sh
fi
