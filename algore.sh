#!/bin/sh
## Main launcher for Al Gore Rhythms
## Backend must've been built beforehand. `./build.sh`
## Assumes you have `xdg-open` or `open` installed

# Set up OS specific
os=`uname`

open=''
cmd=''
if [[ "$os" == 'Linux' ]]; then
    open='xdg-open'
    cmd='cmd'
elif [[ "$os" == 'Darwin' ]]; then
    open='open'
    cmd='command'
fi

echo "[*] Setting up for the environment"
echo "[*]     os = ${os}"
echo "[*]     open command = ${open}"
echo "[*]     ps option = ${cmd}"


echo "[*] Starting Al Gore Rhythms"
cd ./backend
java -jar ./build/libs/backend-all.jar 2>/dev/null &
sleep 1
pid=`ps -o pid,${cmd} | grep backend-all | head -n 1 | awk '{print $1}'`

echo "[*] Started backend on localhost:5000"
echo "[*]    pid = ${pid}"

echo "[*] Launching browser GUI"
${open} http://localhost:5000

# Set up signal trap so we can exit cleanly
trap 'kill ${pid}; echo "[*] Exited Al Gore Rhythms"' EXIT

wait
