#!/bin/bash
xterm -fg red -bg black -fa 'Monospace' -fs 12 -geometry 60x30+600+10 -e "echo 'H4 pinging H10'; 
ping -c3 10.0.0.10;
iperf -c 10.0.0.10 -i 1 -t 100;
exit"
#sudo python ~/Downloads/traffic_generator/generate_elephant_traffic.py 10.0.0.1 10.0.0.10 100 22222;
#bash command if you want to stop xterm on a command line
#read -p 'press any key to start sending elephant flows';
#sudo python generate_elephant_traffic.py 10.0.0.4 10.0.0.10 100 22222;
#read -p 'press any key to exit';

