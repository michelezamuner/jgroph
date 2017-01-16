#!/usr/bin/env sh

readonly jdk_ark="/vagrant/jdk.tar.gz"
readonly jdk_dir="/usr/local/jdk"
readonly mvn_ark="/vagrant/mvn.tar.gz"
readonly mvn_dir="/usr/local/mvn"

apt-get update >/dev/null
apt-get install -y vim curl >/dev/null

if [ ! -f "${jdk_ark}" ]; then
    wget --no-check-certificate --no-cookies --header 'Cookie: oraclelicense=accept-securebackup-cookie' http://download.oracle.com/otn-pub/java/jdk/8u111-b14/jdk-8u111-linux-x64.tar.gz -O "${jdk_ark}" >/dev/null 2>&1
fi

if [ ! -f "${jdk_dir}" ]; then
    mkdir -p "${jdk_dir}"
    tar -xzf "${jdk_ark}" --strip 1 -C "${jdk_dir}" >/dev/null 2>&1
    ln -s "${jdk_dir}" /etc/alternatives/java_sdk_1.8.0
fi

if [ ! -f "${mvn_ark}" ]; then
    wget http://mirror.nohup.it/apache/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz -O "${mvn_ark}" >/dev/null 2>&1
fi

if [ ! -f "${mvn_dir}" ]; then
    mkdir -p "${mvn_dir}"
    tar -xzf "${mvn_ark}" --strip 1 -C "${mvn_dir}" >/dev/null 2>&1
    ln -s "{$mvn_dir}" /etc/alternatives/maven-3.0
fi

if [ -z "${JAVA_HOME}" ]; then
    echo '#!/bin/sh' > /etc/profile.d/custom.sh
    echo "export JAVA_HOME=${jdk_dir}" >> /etc/profile.d/custom.sh
    echo "export M2_HOME=${mvn_dir}" >> /etc/profile.d/custom.sh
    echo 'export PATH="${JAVA_HOME}/bin:${M2_HOME}/bin:${PATH}"' >> /etc/profile.d/custom.sh
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
