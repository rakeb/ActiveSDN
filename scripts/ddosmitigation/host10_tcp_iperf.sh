#!/bin/bash
xterm -fg orange -bg black -fa 'Monospace' -fs 12 -geometry 60x30+1200+10 -e "echo 'getting ready to receive traffic';
iperf -s"
#sudo python ~/Downloads/traffic_generator/generate_elephant_traffic.py 10.0.0.1 10.0.0.10 100 22222;
#bash command if you want to stop xterm on a command line
