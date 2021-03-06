﻿from mininet.topo import Topo

class MyTopo( Topo ):
    "Simple topology example."

    def __init__( self ):

        # Initialize topology
        Topo.__init__( self )

        # Add hosts and switches
        host1 = self.addHost( 'h1')
        host2 = self.addHost( 'h2')
        #host3 = self.addHost( 'h3' )
        #host4 = self.addHost( 'h4' )

        switch1 = self.addSwitch( 's1' )
        switch2 = self.addSwitch( 's2' )
        #switch3 = self.addSwitch( 's3' )
        #switch4 = self.addSwitch( 's4' )
        #switch5 = self.addSwitch( 's5' )
        #switch6 = self.addSwitch( 's6' )
        #switch7 = self.addSwitch( 's7' )
        #switch8 = self.addSwitch( 's8' )

        # Add links
        self.addLink(switch1, switch2, bw=10, max_queue_size=10)
	#self.addLink(switches[0], hosts[0], bw=10, max_queue_size=100)
        #self.addLink(switch1, switch5)
        #self.addLink(switch2, switch3)
        #self.addLink(switch3, switch4)
        #self.addLink(switch5, switch6)
        #self.addLink(switch6, switch4)
        #self.addLink(switch5, switch6)
        #self.addLink(switch7, switch8)
        #self.addLink(switch8, switch6)
        #self.addLink(switch7, switch5)
        
        self.addLink(host1, switch1)
        self.addLink(host2, switch2)
        #self.addLink(host3, switch3)
        #self.addLink(host4, switch4)


topos = { 'mytopo': ( lambda: MyTopo() ) }
