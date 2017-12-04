#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail
shopt -s dotglob
IFS=$'\n\t'

readonly command="${1:-}"
readonly jar_path="${2:-}"
readonly pid_file="pid.file"
readonly server_log="server.log"

if [ "${command}" == "start" ]; then
    if [ ! -f "${jar_path}" ]; then
        echo "JAR ${jar_path} not found."
        exit 1
    fi

    if [ -f "${pid_file}" ]; then
        echo "Running server found";
        exit 1
    fi

    echo "Starting server with command 'java -jar ${jar_path} 0.0.0.0 8000 >>${server_log} 2>>&1 &"
    nohup java -jar "${jar_path}" 0.0.0.0 8000 >>"${server_log}" 2>&1 &
    [ $? == 0 ] && echo $! >"${pid_file}"
elif [ "${command}" == "stop" ]; then
    if [ ! -f "${pid_file}" ]; then
        echo "No running server found";
        exit 1
    fi

    echo "Stopping server with command kill -9 $(cat ${pid_file})"
    kill -9 $(cat "${pid_file}")
    rm "${pid_file}"
else
    echo "Invalid command ${command}";
    exit 1
fi
