#!/bin/bash
xterm -fg yellow -bg black -fa 'Monospace' -fs 10 -geometry 45x20+10+10 -e "ping 10.0.0.10;
exit"
#sudo python ~/Downloads/traffic_generator/generate_elephant_traffic.py 10.0.0.1 10.0.0.10 100 22222;
#bash command if you want to stop xterm on a command line
#sudo python host1_sending_ping.py;