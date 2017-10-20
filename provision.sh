#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail
shopt -s dotglob
IFS=$'\n\t'

readonly jdk_ark='/vagrant/jdk.tar.gz'
readonly jdk_dir='/usr/local/jdk'
readonly mvn_version='3.3.9'
readonly mvn_ark='/vagrant/maven.tar.gz'
readonly mvn_dir='/usr/local/maven'

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

if [ -d "${mvn_dir}" ] && [ "$(cat ${mvn_dir}/VERSION)" == "${mvn_version}" ]; then
    echo "Maven already installed"
else
    echo "Installing Maven ${mvn_version}"
    if [ -d "${mvn_dir}" ]; then
        rm -rf "${mvn_dir}"
    fi

    curl -o "${mvn_ark}" "http://mirror.nohup.it/apache/maven/maven-3/${mvn_version}/binaries/apache-maven-${mvn_version}-bin.tar.gz" >/dev/null 2>&1

    mkdir "${mvn_dir}"
    tar -xzf "${mvn_ark}" -C "${mvn_dir}" --strip 1
    rm "${mvn_ark}"
    echo "${mvn_version}" > "${mvn_dir}/VERSION"
fi

if [ -z "${JAVA_HOME+x}" ]; then
    readonly mem_b="$(grep MemTotal /proc/meminfo | awk '{print $2}')"
    readonly mem_m="$((mem_b / 1024))"
    readonly mem_q="$((mem_m / 4))"

    echo 'Configuring Java environment...'
    echo '#!/bin/sh' > /etc/profile.d/custom.sh
    echo "export JAVA_HOME=${jdk_dir}" >> /etc/profile.d/custom.sh
    echo "export M2_HOME=${mvn_dir}" >> /etc/profile.d/custom.sh
    echo 'export M2=${M2_HOME}/bin' >> /etc/profile.d/custom.sh
    echo "export MAVEN_OPTS=-Xms$((mem_q * 2))m -Xmx$((mem_q * 3))m" >> /etc/profile.d/custom.sh
    echo 'export PATH="${JAVA_HOME}/bin:${M2}:${PATH}"' >> /etc/profile.d/custom.sh
    echo "export JAVA_MS=$((mem_q * 2))m" >> /etc/profile.d/custom.sh
    echo "export JAVA_MX=$((mem_q * 3))m" >> /etc/profile.d/custom.sh
    chmod +x /etc/profile.d/custom.sh
fi
