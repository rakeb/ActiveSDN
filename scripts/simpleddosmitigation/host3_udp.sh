#!/bin/bash
xterm -fg yellow -bg black -fa 'Monospace' -fs 12 -geometry 60x10+1900+500 -e "echo 'H3 UDPing H10'; 
sudo python generate_udp_traffic.py 10.0.0.3 10.0.0.10 200 22223;
exit"
#sudo python ~/Downloads/traffic_generator/generate_elephant_traffic.py 10.0.0.1 10.0.0.10 100 22222;
#bash command if you want to stop xterm on a command line
