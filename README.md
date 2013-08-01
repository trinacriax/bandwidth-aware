bandwidth-aware
===============
The bandwidth aware protocol is a module for PeerSim simulator.
This module was developed by Alessandro Russo (DISI - University  of Trento),
under the European project Napa-Wine project (www.napa-wine.eu)

This module is useful to simulate real networks with bandwidth knowledge;
it implements a priority sharing bandwidth allocation which servers the
transmissions with FIFO policies, reserving as much resources as possibile
for each connection. The amount of data to transmit is given in bits,
and when a transmission is feasible it returns the time needed to perform
the transfer in milliseconds, otherwise it returns a code reflecting the
lack of either upload or download bandwidth. These error codes are in
the BandwidthMessage class, and the module that uses this protocol has to
know these codes.

For further information please send an e-mail to the author.

**** HOW TO COMPILE ***
Before start....

!!! This modules REQUIRES JAVA 1.6 or later !!!
check 
$ java -version
$ javac -version

PLEASE use the latest version of PeerSim available at
http://peersim.sourceforge.net/

...let's go!

1) Place the parent directory (bandwidth-aware) in the same parent direnctory of PeerSim simulator:

.
..
peersim
bandwidth-aware
myFirstProtocol
myFavoriteOne

2) copy you version of peersim.jar, djep and jep in the bandwidth-aware direcotry

3) Use the Makefile

    a) To compile
    $ make

    b) To test
    $ make run
    OR
    $ java -cp "peersim.jar:djep-1.0.0.jar:jep-2.3.0.jar:bandwidth-aware.jar" peersim.Simulator config-bandwidth.txt > output.log

    and look at output.log

    c) For docs
    $ make doc

4) Config file have to define the following blocks:

    a) state the protocol

    protocol.bwp BandwidthAwareProtocol

    b) Initilizer for connection and eventually inialize the bandwidth of the source which is the last peer in the network

    ## You can set here the source multiplier, adding the base_uplink and the bms,
    ## or the source will be initialized as the other nodes.
    init.bwi bandwidth.core.BandwidthAwareInitializer
    {
        ## Protocol to initialize
        protocol bwp
        ## Verbosity level
        debug DEBUG
        ## Base uplink value (e.g., it could be 1Mbit/s -> 1000000)
        base_uplink 1000000
        ## Source bandwidth, as uplink multiplicator.
        ## Note that the source is the last peer in the neighbor.
        bms 2.1
        ## Active connections are those started by node.
        ## Involving upload bandwidth
        active_upload ALPHA_up
        ## Involving download bandwidth
        active_download ALPHA_down
        ## Passive connections are those received by other nodes.
        ## Involving upload bandwidth
        passive_upload PI_up
        ## Involving download bandwidth
        passive_download PI_down
    }

    c)  CDF initilizer for bandwidth distribution.

    init.bandUP bandwidth.core.CDFDistribution
    {
        ## Protocol identifier (to set)
        protocol bwp
        ## Base value (e.g., for the bandwidth could be 1Mbit/s = 1000000)
        base_value 1000000
        ## Base value multipliers, separated by comma.
        value_multiplier 3.1,3.3,3.6,4
        ## CDF distribution corresponding to multipliers.
        cdf_distribution 0.5,0.6,0.9,1.0
        ## Method invoked to set the value
        setter initUpload
    }
