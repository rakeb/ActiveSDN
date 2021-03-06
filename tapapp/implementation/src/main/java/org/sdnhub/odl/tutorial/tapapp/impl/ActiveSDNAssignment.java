package org.sdnhub.odl.tutorial.tapapp.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.sal.binding.api.NotificationProviderService;
import org.opendaylight.controller.sal.binding.api.RpcProviderRegistry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeId;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.ActivesdnListener;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.ActivesdnService;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.ConstructTopology;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.EventSpecs.EventAction;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.EventTriggered;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.EventTriggered.TriggeredEventType;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.FlowIsRemoved;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.FlowStatisticReceived;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.GetAllFlowRulesFromASwitchInputBuilder;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.GetAllFlowRulesFromASwitchOutput;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.InstallFlowRuleInputBuilder;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.InstallNetworkPathInputBuilder;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.IpMutateInputBuilder;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.Ipv4PacketHeaderFields;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.IsLinkFlooded;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.MigrateNetworkPathInputBuilder;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.NewHostFound;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.ReRouteInputBuilder;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.RedirectInputBuilder;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.SendPacketOutInputBuilder;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.SubscribeEventInputBuilder;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.SubscribeForLinkFloodingCheckInputBuilder;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.SubscribeForStatsFromSwitchInputBuilder;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.event.triggered.packet.type.IcmpPacketType;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.event.triggered.packet.type.Ipv4PacketType;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.event.triggered.packet.type.TcpPacketType;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.flow.statistic.received.SwitchStatistics;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.get.all.flow.rules.from.a._switch.output.FlowRules;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.grp.flow.statistic.FlowStatistic;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.is.link.flooded.FloodedLinks;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.tap.rev150601.GetNetworkTopologyOutput;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.tap.rev150601.TapService;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.tap.rev150601.TrafficType;
import org.opendaylight.yangtools.concepts.Registration;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

enum TrafficProtocolType {
	TCP, UDP, ICMP, IP
};

//@SuppressWarnings("unused")
public class ActiveSDNAssignment implements ActivesdnListener{
	
	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
//	private final static String FLOOD = "FLOOD";
    private final static String DROP = "DROP";
    private final static String CONTROLLER = "CONTROLLER";
    private final static String TABLE = "TABLE";
//    private final static long TABLE_PORT_NUMBER = 0xfffffff9L;
//    private DataBroker dataBroker;
    private ActivesdnService activeSDNService;
    private TapService tapService;
    
//    private final AtomicLong eventID = new AtomicLong();
    private HashMap<String, ConnectedHostInfo> hostTable = new HashMap<String, ConnectedHostInfo>();
    private HashMap<Integer, List<FlowRules>> networkConfiguration = new HashMap<Integer, List<FlowRules>>();
    private HashMap<String, List<String>> installedPaths = new HashMap<String, List<String>>();
    private HashMap<String, List<String>> installedInspectionPaths = new HashMap<String, List<String>>();
    
    public HashMap<String, Integer> criticalLinks = new HashMap<String, Integer>();
    /**
     * List of Switch Statistics Snapshot against per Switch ID
     */
    public HashMap<Integer, List<SwitchStatsSnapshot>> networkStatistics = new HashMap<Integer, List<SwitchStatsSnapshot>>();
    
    /**
     * Number of snapshots(array list size) taken against the sourceIp(string)
     */
    public HashMap<Integer, SwitchStatsSnapshot> currentSwitchStats = new HashMap<Integer, SwitchStatsSnapshot>();
    public HashMap<String, ArrayList<Boolean>> sourceIpConfidence = new HashMap<String, ArrayList<Boolean>>();
    public List<String> blockedIPs = Lists.newArrayList();
    public List<String> throttledIPs = Lists.newArrayList();
    public List<String> listOfNewComers = Lists.newArrayList();
    List<String>whiteListedSources = Lists.newArrayList();
    public List<String> serverIPs = Lists.newArrayList();
    List<Integer> edgeSwitches = Lists.newArrayList();
    private String criticalLink = null;
    private LinkInfo deletedLink = null;
//    private boolean trigger = false;
//    private String floodEventId;
//    private String notifyEventId;
    private float newComersRatio = (float) 0.2;
//    private boolean firstTime = true;
    private NetworkGraph topology;
   
    private ProxyTable proxyTable = new ProxyTable();
    private final static String INSPECT = "INSPECT";
    
    //Property
    public static Properties properties;
    private List<String> pathForDropBoxLogin;
//    private List<String> randomHostIPs = Lists.newArrayList();
    private List<String> vIpList = Lists.newArrayList();
    private int vIpIndex;
//    private List<String> alreadyMutated = Lists.newArrayList();
    
    boolean ipMutationPathFirstTime = true;
    private long ipMutationDuration;
    private static final int IPMMUTATIONTRIGGER = 20;
    private static final String RIP_DST = "10.0.0.8/32";
    private long ipMutationTimeDifference;
    
    boolean callingSchedularFirstTime = true;
    private long duration;
    private static final long DELAY = 60;
    public String inspector_IP = "10.0.0.8/32";
    long prevTime;
//    private long prev;
//    private long next;
    
//    private long mutationDuration = 0;
//    private int mutationTrigger = 25; //Represents number of seconds before trigger IP mutation (RHM)
//    private boolean mutationPathFirstTime = true;
    int mutationIndex = 0;
    boolean rhmExperiment = false;
    public boolean isSpecialMutationStarted = false;
    public boolean isPathMutationStarted = false;
	private List<String> inspectionPath;
    
    public static final int FLOW_PRIORITY = 300;
    /////////////////////////////////////////////////////////////////
    
	@SuppressWarnings("deprecation")
	public ActiveSDNAssignment(DataBroker dataBroker, NotificationProviderService notificationService, 
			RpcProviderRegistry rpcProviderRegistry, NetworkGraph topology) {
		//Store the data broker for reading/writing from inventory store
//        this.dataBroker = dataBroker;
        //Object used for flow programming through RPC calls
        this.activeSDNService = rpcProviderRegistry.getRpcService(ActivesdnService.class);
        
        this.tapService = rpcProviderRegistry.getRpcService(TapService.class);
        notificationService.registerNotificationListener(this);
        //this.topology = new NetworkGraph();
        this.topology = topology;

		// Property - starts
		initProperties();
		//set target ips
//		setTargetIPs();
		
		//setting globals
		String propertyNewComersRatio = properties.getProperty("newComersRatio");
		newComersRatio = (float) ((Integer.parseInt(propertyNewComersRatio)*1.0)/(100.0));
        
//        System.out.println(newComersRatio);
        
		
	}
	/**
	 * Sets the target/serverIPs so that they will not get blocked
	 */
	private void setTargetIPs() {
        String sIPs = properties.getProperty("serverIPs");
        String[] serverIps = sIPs.split(",");

        for (String serverIp :
                serverIps) {
            serverIPs.add(serverIp);
        }

        System.out.println(serverIPs);	
		
	}

	/**
	 * Initialize the configuration file
	 */
	private void initProperties() {
		properties = new Properties();
        
		final String dir = System.getProperty("user.dir");
//        System.out.println("current dir = " + dir);

        String key = "distribution";
        String[] parts = dir.split(key);
        String resources = "tapapp/implementation/src/main/resources/ddos-config.properties";
        String resourcesPath = parts[0] + resources;

//        System.out.println("Final path: " + resourcesPath);
        try {
			properties.load(new FileInputStream(resourcesPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onNewHostFound(NewHostFound notification) {
		ConnectedHostInfo connectedHost = new ConnectedHostInfo(
				notification.getHostMacAddress(), notification.getHostIpAddress(), 
				notification.getSwitchConnectedTo(), notification.getPortConnectedTo());
		hostTable.put(notification.getHostIpAddress(), connectedHost);
		edgeSwitches.add(notification.getSwitchConnectedTo());
	}

	@Override
	public void onConstructTopology(ConstructTopology notification) {
		try {
			Future<RpcResult<GetNetworkTopologyOutput>> topologyFutureOutput = tapService.getNetworkTopology();
			if (topologyFutureOutput != null){
				GetNetworkTopologyOutput topologyOutput = topologyFutureOutput.get().getResult();
				topology = new NetworkGraph(topologyOutput);
			}
		} catch (Exception e){
			LOG.debug("Exception reached." , e);
		}
	}
	
	@SuppressWarnings("unused")
	private void getSwitchFlowTable (int switchId) {
		GetAllFlowRulesFromASwitchInputBuilder getFlowsInputBuilder = new GetAllFlowRulesFromASwitchInputBuilder();
		getFlowsInputBuilder.setSwitchId(switchId);
	
		try {
			Future<RpcResult<GetAllFlowRulesFromASwitchOutput>> flowsOutputFuture = 
					this.activeSDNService.getAllFlowRulesFromASwitch(getFlowsInputBuilder.build());
			if (flowsOutputFuture != null){
				GetAllFlowRulesFromASwitchOutput flowsOutput = flowsOutputFuture.get().getResult();
				if (networkConfiguration.containsKey(switchId)){
					networkConfiguration.remove(switchId);
					networkConfiguration.put(switchId, flowsOutput.getFlowRules());
				} else {
					networkConfiguration.put(switchId,  flowsOutput.getFlowRules());
				}
			}
		} catch (Exception e){
			LOG.error("Exception reached in getting switch flow table {} ", e);
		}
	}
	
	private void updatePaths(String targetLink, List<String>whiteListedSources){
		String leftSwitch = targetLink.split(":")[0];
		String rightSwitch = targetLink.split(":")[1];
		
		NodeId sourceNode = new NodeId("openflow:" + leftSwitch);
		NodeId dstNode = new NodeId("openflow:" + rightSwitch);
		
		updateInstalledPaths(sourceNode, dstNode, whiteListedSources);
//		migratePath(sourceNode, dstNode, whiteListedSources);
		
	}

	public void updateInstalledPaths (NodeId sourceNode, NodeId dstNode, List<String> whiteListSources){
		NodeId sourceNodeId = sourceNode; //new NodeId(linkDown.getSource().getSourceNode().getValue());
        NodeId dstNodeId = dstNode; //new NodeId(linkDown.getDestination().getDestNode().getValue());
        
        String leftSwitch = sourceNodeId.getValue().split(":")[1];
		String rightSwitch = dstNodeId.getValue().split(":")[1];
		String forwardMatch = leftSwitch + ", " + rightSwitch;
		String reverseMatch = rightSwitch + ", " + leftSwitch;
		
		for (String pair : installedPaths.keySet()){
			if (installedPaths.get(pair).toString().contains(forwardMatch) || 
					installedPaths.get(pair).toString().contains(reverseMatch)){
				
				String sourceIp = pair.split(":")[0];
				String dstIp = pair.split(":")[1];
					
				if (whiteListSources.isEmpty() == false) {
					if (whiteListSources.contains(sourceIp) == false) {continue;}
				}
				else {
					return;
				}
				
				LOG.debug("==============---------------=================----------------------");
	        	LOG.debug("     Found Pair {} with flooded link along the path.", pair);
	        	LOG.debug("==============---------------=================----------------------");
	        	
				ReRouteInputBuilder reRouteInputBuilder = new ReRouteInputBuilder();
				reRouteInputBuilder.setSrcIpAddress(sourceIp);
				reRouteInputBuilder.setDstIpAddress(dstIp);
				reRouteInputBuilder.setFlowPriority(FLOW_PRIORITY);
				
				List<Integer> oldPathNodes = Lists.newArrayList(); //List of switches along the old path
				for (String node : installedPaths.get(pair)){
					oldPathNodes.add(Integer.parseInt(node));
				}
				reRouteInputBuilder.setSwitchesInOldPath(oldPathNodes); //list of switches along the new path
				
				List<Integer> newPathNodes = Lists.newArrayList();
				List<String> path = topology.findShortestPath(
						Integer.parseInt(installedPaths.get(pair).get(0)), 
						Integer.parseInt(installedPaths.get(pair).get(installedPaths.get(pair).size() - 1)));
				
				if (path != null) {
					for (String node : path){
						newPathNodes.add(Integer.parseInt(node));
					}
					reRouteInputBuilder.setSwitchesInNewPath(newPathNodes);
					LOG.debug("     ==================================================================     ");
					LOG.debug("   Path of Pair {} is changed from {}  to  {} ", pair, 
							installedPaths.get(pair).toString(), path.toString());
					LOG.debug("      ==================================================================     ");
					installedPaths.put(pair, path);
					this.activeSDNService.reRoute(reRouteInputBuilder.build());
				}
				else {
					LOG.debug("==================================================================     ");
					LOG.debug("   No new Path is found for Pair {}  ", pair);
					LOG.debug("==================================================================     ");
				}
			}
		}
	}
	
	public void migratePath (NodeId sourceNode, NodeId dstNode, List<String> whiteListSources){
		NodeId sourceNodeId = sourceNode; //new NodeId(linkDown.getSource().getSourceNode().getValue());
        NodeId dstNodeId = dstNode; //new NodeId(linkDown.getDestination().getDestNode().getValue());
        
        String leftSwitch = sourceNodeId.getValue().split(":")[1];
		String rightSwitch = dstNodeId.getValue().split(":")[1];
		String forwardMatch = leftSwitch + ", " + rightSwitch;
		String reverseMatch = rightSwitch + ", " + leftSwitch;
		
		for (String pair : installedPaths.keySet()){
			if (installedPaths.get(pair).toString().contains(forwardMatch) || 
					installedPaths.get(pair).toString().contains(reverseMatch)){
				
				String sourceIp = pair.split(":")[0];
				String dstIp = pair.split(":")[1];
				String newDstIp = "10.0.0.13/32";
					
				if (whiteListSources.isEmpty() == false) {
					if (whiteListSources.contains(sourceIp) == false) {continue;}
				}
				else {
					return;
				}
				
				LOG.debug("==============---------------=================----------------------");
	        	LOG.debug("Found Pair {} with critical link along the path.", pair);
	        	LOG.debug("==============---------------=================----------------------");
	        	
//				ReRouteInputBuilder reRouteInputBuilder = new ReRouteInputBuilder();
	        	MigrateNetworkPathInputBuilder migrateNetworkPathInputBuilder = new MigrateNetworkPathInputBuilder();
	        	migrateNetworkPathInputBuilder.setOldSrcIpAddress(sourceIp);
	        	migrateNetworkPathInputBuilder.setOldDstIpAddress(dstIp);
	        	migrateNetworkPathInputBuilder.setNewDstIpAddress(newDstIp);
	        	migrateNetworkPathInputBuilder.setFlowPriority(FLOW_PRIORITY);
				
				List<Integer> oldPathNodes = Lists.newArrayList(); //List of switches along the old path
				for (String node : installedPaths.get(pair)){
					oldPathNodes.add(Integer.parseInt(node));
				}
				migrateNetworkPathInputBuilder.setSwitchesInOldPath(oldPathNodes); //list of switches along the new path
				
				List<Integer> newPathNodes = Lists.newArrayList();
				int srcSwitchNumber = hostTable.get(sourceIp).getSwitchConnectedTo();
				int dstSwitchNumber = hostTable.get(newDstIp).getSwitchConnectedTo();
				
				List<String> path = topology.findShortestPath(srcSwitchNumber, dstSwitchNumber);
				
				if (path != null) {
					for (String node : path){
						newPathNodes.add(Integer.parseInt(node));
					}
					migrateNetworkPathInputBuilder.setSwitchesInNewPath(newPathNodes);
					LOG.debug("     ==================================================================     ");
					LOG.debug("   Path of Pair {} is changed from {}  to  {} ", pair, 
							installedPaths.get(pair).toString(), path.toString());
					LOG.debug("      ==================================================================     ");
					installedPaths.put(pair, path);
					this.activeSDNService.migrateNetworkPath(migrateNetworkPathInputBuilder.build());
				}
				else {
					LOG.debug("     ==================================================================     ");
					LOG.debug("   No new Path is found for Pair {}  ", pair);
					LOG.debug("      ==================================================================     ");
				}
			}
		}
	}
	
	public long timeSchedular() {
        long timeMillis = System.currentTimeMillis();
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeMillis);

        long timeDifference = 0;
        if (callingSchedularFirstTime) {
            duration = seconds;
            callingSchedularFirstTime = false;
        } else {
            timeDifference = seconds - duration;
        }

        long index = (timeDifference/ DELAY);
        return index;
    }
	
	
	@SuppressWarnings("unused")
	private void pathMutate(List<String> src, List<String> dst, PathProfile p, int trigger) {
		LOG.debug("		==============---------------=================----------------------");
    	LOG.debug("		Starting Path Mutation ...");
    	LOG.debug("		==============---------------=================----------------------");
    	
    	String srcIp = src.get(0);
    	String dstIp = dst.get(0);

		ConnectedHostInfo srcHost = hostTable.get(srcIp);
		ConnectedHostInfo dstHost = hostTable.get(dstIp);
		String forwardPathKey = srcHost.getHostIP() + ":" + dstHost.getHostIP();
		String reversePathKey = dstHost.getHostIP() + ":" + srcHost.getHostIP();
		String key = null;
		
		List<String> oldPath = null;
		
		if (installedPaths.containsKey(forwardPathKey)){
			oldPath = installedPaths.get(forwardPathKey);
			key = forwardPathKey;
		} 
		
		if(installedPaths.containsKey(reversePathKey)) {
			oldPath = installedPaths.get(reversePathKey);
			key = reversePathKey;
		}
		
		if (oldPath == null) {
			LOG.debug("     ==================================================================     ");
			LOG.debug("   Abroting path mutation as because there is no old path found in src {} <--> dst {}", srcIp, dstIp);
			LOG.debug("     ==================================================================     ");
			return;
		}
		
		LOG.debug("     ==================================================================     ");
		LOG.debug("   	In path mutation oldPath {}", oldPath.toString());
		LOG.debug("     ==================================================================     ");
			
		ReRouteInputBuilder reRouteInputBuilder = new ReRouteInputBuilder();
		reRouteInputBuilder.setSrcIpAddress(srcIp);
		reRouteInputBuilder.setDstIpAddress(dstIp);
		reRouteInputBuilder.setFlowPriority(FLOW_PRIORITY);
		reRouteInputBuilder.setHardTimeout(trigger);
		reRouteInputBuilder.setRemoveOldPath(true);
		
		List<Integer> oldPathNodes = Lists.newArrayList(); //List of switches along the old path
		for (String node : oldPath){
			oldPathNodes.add(Integer.parseInt(node));
		}
		reRouteInputBuilder.setSwitchesInOldPath(oldPathNodes); //list of switches along the new path
		
		int srcSwitchNumber = srcHost.getSwitchConnectedTo();
		int dstSwitchNumber = dstHost.getSwitchConnectedTo();
		
		reRouteInputBuilder.setSwitchesInOldPath(oldPathNodes); //list of switches along the new path
		
		List<Integer> newPathNodes = Lists.newArrayList();
		List<String> path = Utility.getDifferntPath(topology.findAllPaths(srcSwitchNumber, dstSwitchNumber), oldPath);
		
		if (path != null) {
			LOG.debug("     ==================================================================     ");
			LOG.debug("   	In path mutation new Path {}", path.toString());
			LOG.debug("     ==================================================================     ");
			
			for (String node : path){
				newPathNodes.add(Integer.parseInt(node));
			}
			reRouteInputBuilder.setSwitchesInNewPath(newPathNodes);
			LOG.debug("     ==================================================================     ");
			LOG.debug("   	Mutating path form oldPath {} newPath {}", oldPath, path);
			LOG.debug("      ==================================================================     ");
			installedPaths.put(forwardPathKey, path);
			installedPaths.put(reversePathKey, path);
			this.activeSDNService.reRoute(reRouteInputBuilder.build());
		}
		else {
			LOG.debug("     ==================================================================     ");
			LOG.debug("   	Abroting path mutation as because there is no new path found in src {} <--> dst {}", srcIp, dstIp);
			LOG.debug("     ==================================================================     ");
			return;
		}
	}
	
	
	@SuppressWarnings("unused")
	private String findCriticalLink(){
		criticalLinks.clear();
		
		for (List<String> path: installedPaths.values()) {
			for (int index = 1; index < path.size() - 2; index++){
				//Currently I have made this to ensure that I find Critical link in the middle of the network 
				//and not with  hte edge routers
				String link = path.get(index) + ":" + path.get(index + 1);
				String revLink = path.get(index + 1) + ":" + path.get(index);
				if (criticalLinks.containsKey(link)){
					criticalLinks.put(link, criticalLinks.get(link) + 1);
				}
				else if (criticalLinks.containsKey(revLink)){
					criticalLinks.put(revLink, criticalLinks.get(revLink) + 1);
				}
				else {
					criticalLinks.put(link, 1);
				}
			}
		}
		
		int maxValue = (Collections.max(criticalLinks.values()));  // This will return max value in the Hashmap
		
        for (Entry<String, Integer> entry : criticalLinks.entrySet()) {  // Iterate through hashmap
        	//if (entry.getKey().contains("11") || entry.getKey().contains("5")) {continue;}
            if (entry.getValue() == maxValue) {
            	LOG.debug("     ^^^^^^^^^^^^^^==============================^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^     ");
				LOG.debug("                    Critical Link = {} ", entry.getKey());
				LOG.debug("     ^^^^^^^^^^^^^^==============================^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^     ");
                return entry.getKey();  
            }
        }
        return null;
	}

	public void clearConfiguration(){
		installedPaths.clear();
		//hostTable.clear();
		networkConfiguration.clear();
		criticalLinks.clear();
		networkStatistics.clear();
		currentSwitchStats.clear();
		sourceIpConfidence.clear();
		blockedIPs.clear();
		throttledIPs.clear();
		listOfNewComers.clear();
		criticalLink = null;
		deletedLink = null;
//		trigger = false;
//		firstTime = true;
//		floodEventId = "";
//		notifyEventId = "";
	}
	
	public void inspectByController(EventTriggered notification) {

		LOG.debug("     ==================================================================     ");
		LOG.debug("     Inspection Starts " );
		LOG.debug("     ==================================================================     ");
		
		String payload = notification.getStringPayload();
		if (payload != ""){
			LOG.debug("     ==================================================================     ");
			LOG.debug("		Payload available in the packet: {}", payload);
			LOG.debug("     ==================================================================     ");
			
			if (payload.contains("malicious")){
				LOG.debug("		We have found malicious string");
			}
			else {
				LOG.debug("		We Couldn't find malicious string");
			}
		}
		else {
			LOG.debug("		No data available in the payload");
		}
		
		SendPacketOutInputBuilder packetOutBuilder = new SendPacketOutInputBuilder();
		packetOutBuilder.setSwitchId(Integer.parseInt(inspectionPath.get(1))); // 1 because the first switch generates packet in and second switch is the next hop
		packetOutBuilder.setInPortNumber(-1);
		packetOutBuilder.setPayload(notification.getPayload()); // This sets the payload as received during PacketIn
		packetOutBuilder.setOutputPort(TABLE);
		this.activeSDNService.sendPacketOut(packetOutBuilder.build());
		
		LOG.debug("     ==================================================================     ");
		LOG.debug("     Inspection Ends " );
		LOG.debug("     ==================================================================     ");
		
	}

	
	public boolean inspectByProxy(EventTriggered notification) {

		LOG.debug("     ==================================================================     ");
		LOG.debug("     DropBox Login: Inspection Starts " );
		LOG.debug("     ==================================================================     ");
		
		TcpPacketType tcpPacketType = (TcpPacketType) notification.getPacketType();
		
		String source = tcpPacketType.getSourceAddress();
		String destination = tcpPacketType.getDestinationAddress();
		
		ConnectedHostInfo srcHost = hostTable.get(source);
		ConnectedHostInfo dstHost = hostTable.get(destination);

		String forwardTo;
		
		pathForDropBoxLogin = topology.findShortestPath(srcHost.getSwitchConnectedTo(), dstHost.getSwitchConnectedTo());


//		pathForDropBoxLogin.remove(Integer.toString(srcHost.getSwitchConnectedTo()));
//		populate proxy table
		forwardTo = pathForDropBoxLogin.get(1); // 1 because the first switch will be negated and second switch is the next hop
		proxyTable.getSource().add(source);
		proxyTable.getAction().put(source, INSPECT);
		proxyTable.getForwardTo().put(source, forwardTo);
		
		String payload = notification.getStringPayload();
		if (payload != ""){
			LOG.debug("Payload available in the packet: {}", payload);
			if (payload.contains("malicious")){
				LOG.debug("We have found malicious string");
				LOG.debug("     ==================================================================     ");
				LOG.debug("     DropBox Login: Inspection Ends " );
				LOG.debug("     ==================================================================     ");
				return true;
			}
			else {
				LOG.debug("We Couldn't find malicious string");
				
				LOG.debug("     ==================================================================     ");
				LOG.debug("     DropBox Login: Inspection Ends " );
				LOG.debug("     ==================================================================     ");
				return false;
			}
		}
		else {
			LOG.debug("No data available in the payload");
			
			LOG.debug("     ==================================================================     ");
			LOG.debug("     DropBox Login: Inspection Ends " );
			LOG.debug("     ==================================================================     ");
			return true;
		}
		
	}

	//in CLIPS it is reRoute
	public void reDirect(EventTriggered notification) {
		LOG.debug("     ==================================================================     ");
		LOG.debug("     DropBox Login: Redirect to controller Starts " );
		LOG.debug("     ==================================================================     ");
		
		TcpPacketType tcpPacketType = (TcpPacketType) notification.getPacketType();
		
		String source = tcpPacketType.getSourceAddress();
		String destination = tcpPacketType.getDestinationAddress();
		
		String srcDstKey = source + ":" + destination;
		
//		ConnectedHostInfo srcHost = hostTable.get(source);
//		ConnectedHostInfo dstHost = hostTable.get(destination);

		if (installedInspectionPaths.containsKey(srcDstKey)){
			//SendPacketOutInputBuilder packetOutBuilder = new SendPacketOutInputBuilder();
			SendPacketOutInputBuilder packetOutBuilder = new SendPacketOutInputBuilder();
			packetOutBuilder.setSwitchId(Integer.parseInt(installedInspectionPaths.get(srcDstKey).get(1)));
			packetOutBuilder.setInPortNumber(-1);
			packetOutBuilder.setPayload(notification.getPayload()); // This sets the payload as received during PacketIn
			packetOutBuilder.setOutputPort(TABLE);
			this.activeSDNService.sendPacketOut(packetOutBuilder.build());
			LOG.debug("     ==================================================================     ");
			LOG.debug("     DropBox Login: Redirect to controller Ends " );
			LOG.debug("     ==================================================================     ");
			return;
		}
		
		//Install the path to dropbox
		RedirectInputBuilder redirectInputBuilder = new RedirectInputBuilder();
		
		redirectInputBuilder.setSrcIpAddress(tcpPacketType.getSourceAddress());
		redirectInputBuilder.setDstIpAddress(tcpPacketType.getDestinationAddress());
		redirectInputBuilder.setFlowPriority(FLOW_PRIORITY);
		
		List<Integer> pathNodes = Lists.newArrayList();
		if (pathForDropBoxLogin != null) {
			for (String node : pathForDropBoxLogin){
				pathNodes.add(Integer.parseInt(node));
			}
			redirectInputBuilder.setSwitchesInPath(pathNodes);
			LOG.debug("     ==================================================================     ");
			LOG.debug("     Path found for pathForDropBoxLogin is {}", pathForDropBoxLogin.toString());
			LOG.debug("     ==================================================================     ");
		}
		redirectInputBuilder.setTypeOfTraffic(TrafficType.TCP);
		redirectInputBuilder.setInspectionSwitchId(pathNodes.get(0));
		redirectInputBuilder.setInspectionSwitchPortId(CONTROLLER);
		//If you don't want to send the controller port as redirection instead another port then simply write that port number
		//redirectInputBuilder.setInspectionSwitchPortId("1"); //e.g., if you want to output through port of the switch
		this.activeSDNService.redirect(redirectInputBuilder.build());

		if (installedInspectionPaths.containsKey(srcDstKey) == false){
			installedInspectionPaths.put(srcDstKey, pathForDropBoxLogin);
		}
		
		//Forward the packet to next hop
//		LinkInfo linkInfo = topology.findLink(srcHost.getSwitchConnectedTo(), Integer.parseInt(proxyTable.getForwardTo().get(source)));

		//SendPacketOutInputBuilder packetOutBuilder = new SendPacketOutInputBuilder();
		SendPacketOutInputBuilder packetOutBuilder = new SendPacketOutInputBuilder();
		packetOutBuilder.setSwitchId(pathNodes.get(1));
		packetOutBuilder.setInPortNumber(-1);
		packetOutBuilder.setPayload(notification.getPayload()); // This sets the payload as received during PacketIn
		packetOutBuilder.setOutputPort(TABLE);
		this.activeSDNService.sendPacketOut(packetOutBuilder.build());		 
		
		LOG.debug("     ==================================================================     ");
		LOG.debug("     DropBox Login: Redirect to controller Ends " );
		LOG.debug("     ==================================================================     ");
	}
	
	private int changeVip() {
		long timeMillis = System.currentTimeMillis();
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeMillis);

        if (ipMutationPathFirstTime) {
            ipMutationDuration = seconds;
            ipMutationPathFirstTime = false;
//            return 1;
        } else {
            ipMutationTimeDifference = seconds - ipMutationDuration;
        }
        long index = (ipMutationTimeDifference/IPMMUTATIONTRIGGER) % 6;
        
        return (int) index;
	}
	
	
	
	private void sendingPacketOut(EventTriggered notification) {
		SendPacketOutInputBuilder packetOutBuilder = new SendPacketOutInputBuilder();
		packetOutBuilder.setSwitchId(notification.getSwitchId());
		packetOutBuilder.setInPortNumber(notification
				.getInPortNumber());
		packetOutBuilder.setPayload(notification.getPayload()); 
		packetOutBuilder.setOutputPort(TABLE);
		
//		LOG.debug("=================================================================================");
//		LOG.debug(" Sending Packet Out...");
//		LOG.debug("=================================================================================");
		
		this.activeSDNService.sendPacketOut(packetOutBuilder.build());
	}
	
	/**
	 * This is a temporary method only for subscribing sensors to mitigate DDoS attack
	 */
	void subscribeSensors() {
		List<Integer> switchIDs = Lists.newArrayList();

		String[] ids = properties.getProperty("switchIDs").split(",");
		
		for (String id : ids) {
			if (!switchIDs.contains(id)) {
				switchIDs.add(Integer.parseInt(id));
			}
		}
		
//		int leftSwitch = 1; // SwitchID is fixed as the way we design our
//							// topology for this simple DDoS Mitigation example
//		int rightSwitch = 9; // SwitchID is fixed as the way we design our
//								// topology for this simple DDoS Mitigation
//								// example
//		this.activeSDNService
//				.subscribeForStatsFromSwitch(new SubscribeForStatsFromSwitchInputBuilder()
//						.setSwitchId(leftSwitch).build());
		this.activeSDNService
				.subscribeForStatsFromSwitch(new SubscribeForStatsFromSwitchInputBuilder()
						.setSwitchIds(switchIDs).build());
		
		String propertyLinks = properties.getProperty("links");
		int dropThreshold = Integer.parseInt(properties.getProperty("dropThreshold"));
		
        String[] links = propertyLinks.split(",");

		for (String link : links) {
			
			String[] switches = link.split("-");
			int leftSwitch = Integer.parseInt(switches[0]);
			int rightSwitch = Integer.parseInt(switches[1]);

			LinkInfo linkInfo = topology.findLink(leftSwitch, rightSwitch);

			this.activeSDNService
					.subscribeForLinkFloodingCheck(new SubscribeForLinkFloodingCheckInputBuilder()
							.setSwitchId(leftSwitch)
							.setConnectorId(linkInfo.getLeftSwitchPortNumber())
							.setDropThreshold(dropThreshold).build());
		}
	}
	
	/**
	 * Checks whether path is already installed or not. If not then install a path for TCP, UDP and ICMP protocol
	 * @param packetHeaderFields
	 * @return Return <code>false</code> if install nothing, <code>true</code> if installs a path.
	 */
	private boolean installPath(Ipv4PacketHeaderFields packetHeaderFields) {
		ConnectedHostInfo srcHost = hostTable.get(packetHeaderFields.getSourceAddress());
		ConnectedHostInfo dstHost = hostTable.get(packetHeaderFields.getDestinationAddress());
		String forwardPathKey = srcHost.getHostIP() + ":" + dstHost.getHostIP();
		String reversePathKey = dstHost.getHostIP() + ":" + srcHost.getHostIP();
		
		if (installedPaths.containsKey(forwardPathKey) || installedPaths.containsKey(reversePathKey)){
			LOG.debug("=========================================================================================");
			LOG.debug("Path is already installed between nodes     " + srcHost.getHostIP() + " and " + dstHost.getHostIP());
			LOG.debug("=========================================================================================");
			
			return false;
		} else {
			LOG.debug("     ==================================================================     ");
			LOG.debug("Installing a path using forwardPathKey {}, and installed pathSize {}", forwardPathKey, installedPaths.size());
			LOG.debug("     ==================================================================     ");
			
			InstallNetworkPathInputBuilder pathInputBuilder = new InstallNetworkPathInputBuilder();
			
			pathInputBuilder.setSrcIpAddress(packetHeaderFields.getSourceAddress());
			pathInputBuilder.setDstIpAddress(packetHeaderFields.getDestinationAddress());
			pathInputBuilder.setFlowPriority(FLOW_PRIORITY);
			
			List<Integer> pathNodes = Lists.newArrayList();
			List<String> path = topology.findShortestPath(
					srcHost.getSwitchConnectedTo(), dstHost.getSwitchConnectedTo());
			if (path != null) {
				LOG.debug("==================================================================     ");
				LOG.debug("Path found for installing is {}", path.toString());
				LOG.debug("==================================================================     ");
				
				for (String node : path){
					pathNodes.add(Integer.parseInt(node));
				}
				pathInputBuilder.setSwitchesInPath(pathNodes);
				installedPaths.put(forwardPathKey, path);
			}
			pathInputBuilder.setTypeOfTraffic(TrafficType.ICMP);
			this.activeSDNService.installNetworkPath(pathInputBuilder.build());
			
//			pathInputBuilder.setTypeOfTraffic(TrafficType.TCP);
//			this.activeSDNService.installNetworkPath(pathInputBuilder.build());
//			
//			pathInputBuilder.setTypeOfTraffic(TrafficType.UDP);
//			this.activeSDNService.installNetworkPath(pathInputBuilder.build());
			
			return true;
		}
	}

	@Override
	public void onEventTriggered(EventTriggered notification) {
		boolean isPathAlreadyExist = false;
//		LOG.debug("     ==================================================================     ");
//		LOG.debug("                    Event Triggered is called.");
//		LOG.debug("     ==================================================================     ");
		
		if (notification.getPacketType() instanceof Ipv4PacketType) {
			Ipv4PacketType ipv4Packet = (Ipv4PacketType) notification.getPacketType();
			if (!(hostTable.containsKey(ipv4Packet.getSourceAddress()) && 
					hostTable.containsKey(ipv4Packet.getDestinationAddress()))){
				LOG.debug("=====================================================================================");
				LOG.debug("	For this IP Packet, either source or destination host is not available in the Host Table");
				LOG.debug("=====================================================================================");
				return;
			}
		} else if (notification.getPacketType() instanceof IcmpPacketType) {
			IcmpPacketType icmpPacket = (IcmpPacketType) notification.getPacketType();
			if (!(hostTable.containsKey(icmpPacket.getSourceAddress()) && 
					hostTable.containsKey(icmpPacket.getDestinationAddress()))){
				LOG.debug("=================================================================================");
				LOG.debug("	For this ICMP Packet, either source or destination host is not available in the Host Table");
				LOG.debug("=================================================================================");
				return;
			}
		} else if (notification.getPacketType() instanceof TcpPacketType) {
			TcpPacketType tcpPacketType = (TcpPacketType) notification.getPacketType();
			if (!(hostTable.containsKey(tcpPacketType.getSourceAddress()) && hostTable
					.containsKey(tcpPacketType.getDestinationAddress()))) {
				LOG.debug("=================================================================================");
				LOG.debug("	For this TCP Packet, either source or destination host is not available in the Host Table");
				LOG.debug("=================================================================================");
				return;
			}
		}
		else if (notification.getPacketType() instanceof TcpPacketType) {
			TcpPacketType tcpPacketType = (TcpPacketType) notification.getPacketType();
			if (!(hostTable.containsKey(tcpPacketType.getSourceAddress()) && 
					hostTable.containsKey(tcpPacketType.getDestinationAddress()))){
				LOG.debug("=================================================================================");
				LOG.debug("    Some host information is missing so skipping rest of the event trigger.");
				
				LOG.debug("    SrcIp: {}, dstIp: {}",tcpPacketType.getSourceAddress(), tcpPacketType.getDestinationAddress());
				
				LOG.debug("=================================================================================");
				
				return;
			}
		}
		//============================================================================================
		if (notification.getTriggeredEventType() == TriggeredEventType.NoFlowRuleEvent) {
			//If conditions checks if the Event is triggered because the switch couldn't find any flow rule
			if (notification.getPacketType() instanceof Ipv4PacketType) {
				//This condition checks if the the Packet received in the notification is of an IP Version 4 Packet
				//you can get IP packet fields by using .get methods, e.g., ipv4Packet.getDestinationAddress()
				@SuppressWarnings("unused")
				Ipv4PacketType ipv4Packet = (Ipv4PacketType) notification.getPacketType();
			}
			else if (notification.getPacketType() instanceof IcmpPacketType) {
				//This condition checks if the the Packet received in the notification is of an ICMP Packet
				//you can get ICMP packet fields by using .get methods, e.g., icmpPacket.getCrc()
				@SuppressWarnings("unused")
				IcmpPacketType icmpPacket = (IcmpPacketType) notification.getPacketType();
			}
		} 
		else if (notification.getTriggeredEventType() == TriggeredEventType.ControllerFlowRuleEvent) {
			//If conditions checks if the Event is triggered because of a flow rule that 
			//explicitly forwards the packet to the controller. You can get the flow rule id from notification.getEventId()
			
			///We need the handle the special case where if the switch receives a packet who it was forwarding before but as 
			// the path is migrated away due to link failure or attack. Now, there will now path exists for this packet but that 
			// path doesn't contain current ingress node. So, the packet may stuck in an infinite loop between the controller and switch
			
			////------------------------------------------------------------------------------------------------------
			/////////////                   Handling IPv4 Traffic        /////////////////////////////////////////////  
			////------------------------------------------------------------------------------------------------------
			if (notification.getPacketType() instanceof Ipv4PacketType) {
				LOG.debug("IPV4 packet found in On Event Triggered");
				Ipv4PacketType ipv4Packet = (Ipv4PacketType) notification.getPacketType();
				
				isPathAlreadyExist = !installPath(ipv4Packet);
				if (isSpecialMutationStarted && isPathAlreadyExist ) {
					return;
				}
				sendingPacketOut(notification);
				
				if (isPathAlreadyExist) {
					return;
				}
			}
			////-----------------------------------------------------------------------------------------------------
			/////////////                   Handling TCP Traffic        /////////////////////////////////////////////  
			////-----------------------------------------------------------------------------------------------------
			else if (notification.getPacketType() instanceof TcpPacketType) {
				LOG.debug("TCP packet found in On Event Triggered");
				TcpPacketType tcpPacketType = (TcpPacketType) notification.getPacketType();
				
//				isPathAlreadyExist = !installPath((Ipv4PacketHeaderFields)tcpPacketType);
//				if (isSpecialMutationStarted && isPathAlreadyExist ) {
//					return;
//				}
//				sendingPacketOut(notification);
//				
//				if (isPathAlreadyExist) {
//					return;
//				}
				inspectByController(notification);
			}
			
			////------------------------------------------------------------------------------------------------------
			/////////////                   Handling ICMP Traffic        /////////////////////////////////////////////  
			////------------------------------------------------------------------------------------------------------
			else if (notification.getPacketType() instanceof IcmpPacketType) {
				LOG.debug("ICMP packet found in On Event Triggered");
				IcmpPacketType icmpPacket = (IcmpPacketType) notification.getPacketType();
				
				// example of path mutate through RPC
//				isPathAlreadyExist = !installPath(icmpPacket);
//				sendingPacketOut(notification);
//				
//				if (isPathAlreadyExist) {
//					return;
//				}
				// example of simple path mutate
//				simplestPathMutation(icmpPacket);
				
				// example of redirect
				// trigger "10.0.0.1/32" --> "10.0.0.11/32"
//				String tsrc = "10.0.0.1/32";
//				String tdst = "10.0.0.12/32";
//				IcmpPacketType icmpPacketType = (IcmpPacketType) notification.getPacketType();
//				String source = icmpPacketType.getSourceAddress();
//				String destination = icmpPacketType.getDestinationAddress();
//				
//				if ((source.equals(tsrc) && destination.equals(tdst)) || (source.equals(tdst) && destination.equals(tsrc))){
//					simpleRedirect(notification);
//				}
//				installPath(icmpPacket);
//				sendingPacketOut(notification);
				
				
				// example of simple subscribe event
				IcmpPacketType icmpPacketType = (IcmpPacketType) notification.getPacketType();
				installPath(icmpPacket);
				sendingPacketOut(notification);
				
				simpleSubscribeEvent(icmpPacketType);
				
				
			} /// End of ICMP Packet
		} /// End of ControllerEVentIF
		
		else if (notification.getTriggeredEventType() == TriggeredEventType.SubscribedEvent) {
			LOG.debug("Subscribed Event called!");
			//If conditions checks if the Event is triggered because of a subscribed event is triggered 
			//you can find the event id from notification.getEventId()
			if (notification.getPacketType() instanceof Ipv4PacketType) {
				@SuppressWarnings("unused")
				Ipv4PacketType ipv4Packet = (Ipv4PacketType) notification.getPacketType();
			}
			else if (notification.getPacketType() instanceof IcmpPacketType) {
				LOG.debug("Inside SubscribedEvent packet type: ICMP");
				IcmpPacketType icmpPacket = (IcmpPacketType) notification.getPacketType();
				ConnectedHostInfo srcHost = hostTable.get(icmpPacket.getSourceAddress());
				ConnectedHostInfo dstHost = hostTable.get(icmpPacket.getDestinationAddress());
				String forwardPathKey = srcHost.getHostIP() + ":" + dstHost.getHostIP();
				String reversePathKey = dstHost.getHostIP() + ":" + srcHost.getHostIP();
				
				LOG.debug("     ==================================================================     ");
				LOG.debug("      SubscribedEvent called for src {} to dst {}", srcHost.getHostIP(), dstHost.getHostIP());
				LOG.debug("     ==================================================================     ");
				
				LOG.debug("     ==================================================================     ");
				LOG.debug("      We will simply block the flow");
				LOG.debug("     ==================================================================     ");
				
				
				List<String> path = null;
				if (installedPaths.containsKey(forwardPathKey)){
					path = installedPaths.get(forwardPathKey);
					
				} else if (installedPaths.containsKey(reversePathKey)) {
					path = installedPaths.get(reversePathKey);
				} else {
					LOG.debug("     ==================================================================     ");
					LOG.debug("		No oldPath found. returning without blocking...");
					LOG.debug("     ==================================================================     ");
					return;
				}
				
				blockIP(srcHost.getHostIP(), dstHost.getHostIP(), TrafficType.ICMP, Integer.parseInt(path.get(0)), 0);

//				IcmpPacketType icmpPacket = (IcmpPacketType) notification.getPacketType();
//				
//				int leftSwitch = Integer.parseInt(criticalLink.split(":")[0]);
//				int rightSwitch = Integer.parseInt(criticalLink.split(":")[1]);
//				
//				deletedLink = topology.findLink(leftSwitch, rightSwitch);
//				topology.removelinkInfo(leftSwitch, rightSwitch);
//				
//				updatePaths(criticalLink, null);
//				
//				ConnectedHostInfo dstHost = hostTable.get(icmpPacket.getDestinationAddress());
//				SendPacketOutInputBuilder packetOutBuilder = new SendPacketOutInputBuilder();
//				packetOutBuilder.setSwitchId(dstHost.getSwitchConnectedTo());
//				packetOutBuilder.setInPortNumber(-1);
//				packetOutBuilder.setPayload(notification.getPayload()); //This sets the payload as received during PacketIn
//				packetOutBuilder.setOutputPort(Integer.toString(dstHost.getPortConnectedTo())); 
//			
//				this.activeSDNService.sendPacketOut(packetOutBuilder.build());
			}
		}

		/*
		if (notification.getPacketType() instanceof ArpPacketType){
			//This condition checks if the the Packet received in the notification is of an ARP Packet
			//you can get ARP packet fields by using .get methods, e.g., arpPacket.getEthernetSrcMacAddress()
			ArpPacketType arpPacket = (ArpPacketType) notification.getPacketType();
		*/	
		
	}

	private void simpleRedirect(EventTriggered notification) {
		LOG.debug("     ==================================================================     ");
		LOG.debug("     Redirect called " );
		LOG.debug("     ==================================================================     ");
		
		IcmpPacketType icmpPacketType = (IcmpPacketType) notification.getPacketType();
		
		String source = icmpPacketType.getSourceAddress();
		String destination = icmpPacketType.getDestinationAddress();
		
		ConnectedHostInfo srcHost = hostTable.get(source);
		ConnectedHostInfo dstHost = hostTable.get(destination);

		RedirectInputBuilder redirectInputBuilder = new RedirectInputBuilder();
		
		redirectInputBuilder.setSrcIpAddress(source);
		redirectInputBuilder.setDstIpAddress(destination);
		redirectInputBuilder.setFlowPriority(FLOW_PRIORITY);
		
		List<Integer> pathNodes = Lists.newArrayList();
		
		inspectionPath = topology.findShortestPath(srcHost.getSwitchConnectedTo(), dstHost.getSwitchConnectedTo());
		
		if (inspectionPath != null) {
			LOG.debug("     ==================================================================     ");
			LOG.debug("     Redirection path: {}, from src: {} to inspector: {}", inspectionPath.toString(), srcHost.getHostIP(), dstHost.getHostIP());
			LOG.debug("     ==================================================================     ");
			for (String node : inspectionPath){
				pathNodes.add(Integer.parseInt(node));
			}
			redirectInputBuilder.setSwitchesInPath(pathNodes);
		}
		redirectInputBuilder.setTypeOfTraffic(TrafficType.TCP);
		redirectInputBuilder.setInspectionSwitchId(pathNodes.get(0));
		redirectInputBuilder.setInspectionSwitchPortId(CONTROLLER);
		//If you don't want to send the controller port as redirection instead another port then simply write that port number
		//redirectInputBuilder.setInspectionSwitchPortId("1"); //e.g., if you want to output through port of the switch
		this.activeSDNService.redirect(redirectInputBuilder.build());
		
		LOG.debug("     ==================================================================     ");
		LOG.debug("     Redirect call ends " );
		LOG.debug("     ==================================================================     ");
			
	}
	
	private void simplestPathMutation(Ipv4PacketHeaderFields packetHeaderFields) {
		ConnectedHostInfo srcHost = hostTable.get(packetHeaderFields.getSourceAddress());
		ConnectedHostInfo dstHost = hostTable.get(packetHeaderFields.getDestinationAddress());
		String forwardPathKey = srcHost.getHostIP() + ":" + dstHost.getHostIP();
		String reversePathKey = dstHost.getHostIP() + ":" + srcHost.getHostIP();
		String key = null;
		
		List<String> path = null;
		List<String> oldPath = null;
		
		int srcSwitchNumber = srcHost.getSwitchConnectedTo();
		int dstSwitchNumber = dstHost.getSwitchConnectedTo();
		
		if (installedPaths.containsKey(forwardPathKey)){
			LOG.debug("=========================================================================================");
			LOG.debug("Oldpath exist between " + srcHost.getHostIP() + " and " + dstHost.getHostIP());
			LOG.debug("=========================================================================================");
			key = forwardPathKey;
			oldPath = installedPaths.get(key);
			path = Utility.getDifferntPath(topology.findAllPaths(srcSwitchNumber, dstSwitchNumber), oldPath);
			
		} else if (installedPaths.containsKey(reversePathKey)) {
			LOG.debug("=========================================================================================");
			LOG.debug("Oldpath exist between " + srcHost.getHostIP() + " and " + dstHost.getHostIP());
			LOG.debug("=========================================================================================");
			key = forwardPathKey;
			oldPath = installedPaths.get(key);
			path = Utility.getDifferntPath(topology.findAllPaths(srcSwitchNumber, dstSwitchNumber), oldPath);
		} else {
			LOG.debug("     ==================================================================     ");
			LOG.debug("		No oldPath found. Generating a new path using forwardPathKey {}, and installed pathSize {}", forwardPathKey, installedPaths.size());
			LOG.debug("     ==================================================================     ");
			
			path = topology.findShortestPath(srcHost.getSwitchConnectedTo(), dstHost.getSwitchConnectedTo());
		}
			
		InstallNetworkPathInputBuilder pathInputBuilder = new InstallNetworkPathInputBuilder();
		
		pathInputBuilder.setSrcIpAddress(packetHeaderFields.getSourceAddress());
		pathInputBuilder.setDstIpAddress(packetHeaderFields.getDestinationAddress());
		pathInputBuilder.setFlowPriority(FLOW_PRIORITY);
		pathInputBuilder.setHardTimeout(10);
		
		List<Integer> pathNodes = Lists.newArrayList();
		if (path != null) {
			LOG.debug("     ==================================================================     ");
			LOG.debug("   	Mutating path form oldPath {} to newPath {}", oldPath, path);
			LOG.debug("      ==================================================================     ");
			
			for (String node : path){
				pathNodes.add(Integer.parseInt(node));
			}
			pathInputBuilder.setSwitchesInPath(pathNodes);
			installedPaths.put(forwardPathKey, path);
		}
		pathInputBuilder.setTypeOfTraffic(TrafficType.ICMP);
		this.activeSDNService.installNetworkPath(pathInputBuilder.build());
			
	}
	
	public boolean simpleSubscribeEvent(IcmpPacketType icmpPacket) {
		ConnectedHostInfo srcHost = hostTable.get(icmpPacket.getSourceAddress());
		ConnectedHostInfo dstHost = hostTable.get(icmpPacket.getDestinationAddress());
		String forwardPathKey = srcHost.getHostIP() + ":" + dstHost.getHostIP();
		String reversePathKey = dstHost.getHostIP() + ":" + srcHost.getHostIP();
		
		List<String> path = null;
		if (installedPaths.containsKey(forwardPathKey)){
			path = installedPaths.get(forwardPathKey);
			
		} else if (installedPaths.containsKey(reversePathKey)) {
			path = installedPaths.get(reversePathKey);
		} else {
			LOG.debug("     ==================================================================     ");
			LOG.debug("		No oldPath found");
			LOG.debug("     ==================================================================     ");
			return false;
		}
		
		LOG.debug("     ==================================================================     ");
		LOG.debug("      Event subscription started for Switch Number: {}", path.get(0));
		LOG.debug("     ==================================================================     ");
		
		
		SubscribeEventInputBuilder eventInputBuilder = new SubscribeEventInputBuilder();
		eventInputBuilder.setCount((long)5);
		eventInputBuilder.setSrcIpAddress(icmpPacket.getSourceAddress());
		eventInputBuilder.setDstIpAddress(icmpPacket.getDestinationAddress());
		eventInputBuilder.setDuration((long)10);
		eventInputBuilder.setSwitchId(Integer.parseInt(path.get(0)));
		eventInputBuilder.setTrafficProtocol(TrafficType.ICMP);
		eventInputBuilder.setEventAction(EventAction.NOTIFY);
//		eventInputBuilder.setHoldNotification(5);
		
		this.activeSDNService.subscribeEvent(eventInputBuilder.build());
		return true;
	}

	/**
	 * Install a flow rule to Drops packets depending on the parameter 
	 * @param srcIp
	 * @param dstIp
	 * @param trafficType
	 * @param switchId
	 */
	private void blockIP(String srcIp, String dstIp, TrafficType trafficType, int switchId, int hardTimeOut) {
		
		LOG.debug("     ==================================================================     ");
		LOG.debug("     Blocking IP Source {} to Destination {} starts ", srcIp, dstIp);
		LOG.debug("     ==================================================================     ");
		
		InstallFlowRuleInputBuilder flowRuleInputBuilder = new InstallFlowRuleInputBuilder();
		
		flowRuleInputBuilder.setInPortId((long)0);
		flowRuleInputBuilder.setSwitchId(switchId);
		
		if (srcIp != null) {
			flowRuleInputBuilder.setSrcIpAddress(srcIp);
		}
			
		if(dstIp != null) {
			flowRuleInputBuilder.setDstIpAddress(dstIp);
		}
			
		if(trafficType != null) {
			flowRuleInputBuilder.setTypeOfTraffic(trafficType);
		}
			
		
		flowRuleInputBuilder.setFlowPriority(FLOW_PRIORITY + 30000);
		flowRuleInputBuilder.setIdleTimeout(0);
		flowRuleInputBuilder.setHardTimeout(0);
		flowRuleInputBuilder.setHardTimeout(0);
		
		flowRuleInputBuilder.setActionOutputPort(DROP);
		this.activeSDNService.installFlowRule(flowRuleInputBuilder.build());
		
		LOG.debug("     ==================================================================     ");
		LOG.debug("     Blocking IP Source {} to Destination {} ends ", srcIp, dstIp);
		LOG.debug("     ==================================================================     ");
		
	}

	@Override
	public void onFlowIsRemoved(FlowIsRemoved notification) {
		// Notification will provide you the FlowId which is removed and the ID of the switch 
		// that removed the flow rule. Now you have to decide if you want to reinsert that rule or not
		//notification.getFlowId() // returns String
		//notification.getSwitchId() // returns integer
		
		// You can write your code here that will handle the flow removal from the switch
		
		LOG.debug("     ==================================================================     ");
		LOG.debug("            Flow is Removed                  .");
		LOG.debug(notification.getFlowId() +  "            "   + notification.getSwitchId());
		LOG.debug("     ==================================================================     ");
		
	}
	
	@Override
	public void onFlowStatisticReceived(FlowStatisticReceived notification) {
		//LOG.debug(" ");
		//LOG.debug("=============onFlowStatisticReceived Notification is called =================");
		//----------------------Add one more snapshot to know source IP addresses
		//---In future we need to improve this as we can't keep up with thousands of IP addresses and each has 
		//--an ArrayList of of millions of length as we calculate statistics after 60 second
		for (String srcIp : sourceIpConfidence.keySet()){
			sourceIpConfidence.get(srcIp).add(false);
		}
		//----------------------
		for (Iterator<SwitchStatistics> statItr = notification.getSwitchStatistics().iterator(); statItr.hasNext(); ){
			SwitchStatistics switchStats = statItr.next();
			if (switchStats.getFlowStatistic() != null){
				SwitchStatsSnapshot dbstatsSnapshot = new SwitchStatsSnapshot();
				dbstatsSnapshot.snapshotTime = notification.getStatsTime();
				//SwitchStatsSnapshot dbprevSnapshot;
				if (networkStatistics.containsKey(switchStats.getSwitchId())){
					//snapshotId should be snapshotCount
					dbstatsSnapshot.snapshotId = networkStatistics.get(switchStats.getSwitchId()).size() + 1;
					//dbprevSnapshot = networkStatistics.get(switchStats.getSwitchId()).get(dbstatsSnapshot.snapshotId - 1);
				}
				else {
					dbstatsSnapshot.snapshotId = 1;
				}
				
				for (Iterator<FlowStatistic> flowItr = switchStats.getFlowStatistic().iterator(); flowItr.hasNext();){
					FlowStatistic flowStats = flowItr.next();
					
					FlowStatsTuple dbflowTuple = new FlowStatsTuple();
					dbflowTuple.flowId = flowStats.getFlowId();
					dbflowTuple.byteCount = flowStats.getByteCount().longValue();
					dbflowTuple.packetCount = flowStats.getPacketCount().longValue();
					dbflowTuple.duration = flowStats.getDuration();
					dbflowTuple.srcIPAddress = (flowStats.getSrcIpAddress() != null ?  flowStats.getSrcIpAddress() : "");
					dbflowTuple.dstIPAddress = (flowStats.getDstIpAddress() != null ?  flowStats.getDstIpAddress() : "");
					dbflowTuple.srcPort = (flowStats.getSrcPort() != null ? flowStats.getSrcPort() : -1);
					dbflowTuple.dstPort = (flowStats.getDstPort() != null ? flowStats.getDstPort() : -1);
					if (flowStats.getTypeOfTraffic() != null){
						if (flowStats.getTypeOfTraffic() == TrafficType.ICMP){
							dbflowTuple.traffic = TrafficProtocolType.ICMP;
						}
						else if (flowStats.getTypeOfTraffic() == TrafficType.TCP){
							dbflowTuple.traffic = TrafficProtocolType.TCP;
						}
						else if (flowStats.getTypeOfTraffic() == TrafficType.UDP){
							//LOG.debug("UDP flow rule found ");
							dbflowTuple.traffic = TrafficProtocolType.UDP;						
						}
					}
					dbstatsSnapshot.listOfFlows.put(dbflowTuple.flowId, dbflowTuple);
				}
				if (networkStatistics.containsKey(switchStats.getSwitchId())){
					networkStatistics.get(switchStats.getSwitchId()).add(dbstatsSnapshot);
					calculateRunningStatistics(switchStats.getSwitchId());
				}
				else {
					List<SwitchStatsSnapshot> dbStatList = Lists.newArrayList();
					dbStatList.add(dbstatsSnapshot);
					networkStatistics.put(switchStats.getSwitchId(), dbStatList);
					calculateRunningStatistics(switchStats.getSwitchId());
				}
			}
		}
	}

	public void calculateRunningStatistics(int switchId){
		if (networkStatistics.containsKey(switchId)){
			int flowTableSize = networkStatistics.get(switchId).size(); //number of snapshots available
			SwitchStatsSnapshot dbSwitchRunningStats = new SwitchStatsSnapshot();
			if (flowTableSize > 1){
				SwitchStatsSnapshot dbLatestSnapshot = networkStatistics.get(switchId).get(flowTableSize - 1);
				SwitchStatsSnapshot dbPrevSnapshot = networkStatistics.get(switchId).get(flowTableSize - 2);	
				dbSwitchRunningStats.snapshotTime = dbLatestSnapshot.snapshotTime;
				for (String flowId : dbLatestSnapshot.listOfFlows.keySet()){
					if (dbPrevSnapshot.listOfFlows.containsKey(flowId)){
						FlowStatsTuple flowStats = dbLatestSnapshot.listOfFlows.get(flowId);
						
						FlowStatsTuple dbflowTuple = new FlowStatsTuple();
						dbflowTuple.flowId = flowStats.flowId;
						dbflowTuple.byteCount = flowStats.byteCount - dbPrevSnapshot.listOfFlows.get(flowId).byteCount;
						dbflowTuple.packetCount = flowStats.packetCount - dbPrevSnapshot.listOfFlows.get(flowId).packetCount;
						dbflowTuple.duration = flowStats.duration;
						dbflowTuple.srcIPAddress = (flowStats.srcIPAddress != null ?  flowStats.srcIPAddress : "");
						dbflowTuple.dstIPAddress = (flowStats.dstIPAddress != null ?  flowStats.dstIPAddress : "");
						dbflowTuple.srcPort = flowStats.srcPort;;
						dbflowTuple.dstPort = flowStats.dstPort;
						if (flowStats.traffic != null){
							dbflowTuple.traffic = flowStats.traffic;
						}
						dbSwitchRunningStats.listOfFlows.put(flowId, dbflowTuple);
						//----------
						if (dbflowTuple.byteCount > 0) { //this SourceIP has sent some new bytes in the current window
							if (dbflowTuple.srcIPAddress != "") {
								if (sourceIpConfidence.containsKey(dbflowTuple.srcIPAddress)){
									//This source is already known and has been sending traffic before the current snapshot
									//So we have to put True in the current snapshot as well
									sourceIpConfidence.get(dbflowTuple.srcIPAddress).add(
											sourceIpConfidence.get(dbflowTuple.srcIPAddress).size() - 1, true);
								}
								else {
									ArrayList<Boolean> snapshotValues = new ArrayList<Boolean>();
									snapshotValues.add(true);
									sourceIpConfidence.put(dbflowTuple.srcIPAddress, snapshotValues);
								}
							}
						}

					}
					else { //This means that it is a new flow so simply add its current stats as running stats
						FlowStatsTuple flowStats = dbLatestSnapshot.listOfFlows.get(flowId);
						
						FlowStatsTuple dbflowTuple = new FlowStatsTuple();
						dbflowTuple.flowId =  flowStats.flowId;
						dbflowTuple.byteCount = flowStats.byteCount;
						dbflowTuple.packetCount = flowStats.packetCount;
						dbflowTuple.duration = flowStats.duration;
						dbflowTuple.srcIPAddress = (flowStats.srcIPAddress != null ?  flowStats.srcIPAddress : "");
						dbflowTuple.dstIPAddress = (flowStats.dstIPAddress != null ?  flowStats.dstIPAddress : "");
						dbflowTuple.srcPort = flowStats.srcPort;;
						dbflowTuple.dstPort = flowStats.dstPort;
						if (flowStats.traffic != null){
							dbflowTuple.traffic = flowStats.traffic;
						}
						dbSwitchRunningStats.listOfFlows.put(flowId, dbflowTuple);
					}
				}
			}
			else {
				SwitchStatsSnapshot dbLatestSnapshot = networkStatistics.get(switchId).get(flowTableSize - 1);
				dbSwitchRunningStats.snapshotTime = dbLatestSnapshot.snapshotTime;
				for (String flowId : dbLatestSnapshot.listOfFlows.keySet()){
					FlowStatsTuple flowStats = dbLatestSnapshot.listOfFlows.get(flowId);
					
					FlowStatsTuple dbflowTuple = new FlowStatsTuple();
					dbflowTuple.flowId =  flowStats.flowId;
					dbflowTuple.byteCount = flowStats.byteCount;
					dbflowTuple.packetCount = flowStats.packetCount;
					dbflowTuple.duration = flowStats.duration;
					dbflowTuple.srcIPAddress = (flowStats.srcIPAddress != null ?  flowStats.srcIPAddress : "");
					dbflowTuple.dstIPAddress = (flowStats.dstIPAddress != null ?  flowStats.dstIPAddress : "");
					dbflowTuple.srcPort = flowStats.srcPort;;
					dbflowTuple.dstPort = flowStats.dstPort;
					if (flowStats.traffic != null){
						dbflowTuple.traffic = flowStats.traffic;
					}
					dbSwitchRunningStats.listOfFlows.put(flowId, dbflowTuple);
				}
			}
			//-----
			currentSwitchStats.put(switchId, dbSwitchRunningStats);
		}
	}
	
	public List<String> findElephantFlows(int switchId, int anomalousThreshold){
		
		List<String> elephantFlows = Lists.newArrayList(); 
		
		LOG.debug(" .... Not found ...");
		LOG.debug("Checking Elephant Flows ......................");
		ArrayList<Long> flowBytes = new ArrayList<Long>();
		for (String flowId : currentSwitchStats.get(switchId).listOfFlows.keySet()){ 
			if (currentSwitchStats.get(switchId).listOfFlows.get(flowId).traffic == TrafficProtocolType.UDP){continue;}
			if (blockedIPs.contains(currentSwitchStats.get(switchId).listOfFlows.get(flowId).srcIPAddress)){continue;}
			if (throttledIPs.contains(currentSwitchStats.get(switchId).listOfFlows.get(flowId).srcIPAddress)){continue;}
			if (currentSwitchStats.get(switchId).listOfFlows.get(flowId).srcIPAddress == "") {continue;}
			if (serverIPs.contains(currentSwitchStats.get(switchId).listOfFlows.get(flowId).srcIPAddress)) {continue;}
			if (whiteListedSources.contains(currentSwitchStats.get(switchId).listOfFlows.get(flowId).srcIPAddress)){continue;}
			if (currentSwitchStats.get(switchId).listOfFlows.get(flowId).byteCount > 0){
				flowBytes.add(currentSwitchStats.get(switchId).listOfFlows.get(flowId).byteCount);
			}
		}
		
		if (flowBytes.size() < 1) {
			return elephantFlows;
		}
		
		Collections.sort(flowBytes);
		int median = flowBytes.size() / 2;
		long baseLineValue = flowBytes.get(median);
		
		for (String flowId : currentSwitchStats.get(switchId).listOfFlows.keySet()){
			if (currentSwitchStats.get(switchId).listOfFlows.get(flowId).traffic == TrafficProtocolType.UDP){continue;}
			if (blockedIPs.contains(currentSwitchStats.get(switchId).listOfFlows.get(flowId).srcIPAddress)){continue;}
			if (throttledIPs.contains(currentSwitchStats.get(switchId).listOfFlows.get(flowId).srcIPAddress)){continue;}
			if (currentSwitchStats.get(switchId).listOfFlows.get(flowId).srcIPAddress == "") {continue;}
			if (serverIPs.contains(currentSwitchStats.get(switchId).listOfFlows.get(flowId).srcIPAddress)) {continue;}
			if (whiteListedSources.contains(currentSwitchStats.get(switchId).listOfFlows.get(flowId).srcIPAddress)){continue;}
			if (currentSwitchStats.get(switchId).listOfFlows.get(flowId).byteCount > (baseLineValue * anomalousThreshold) ){
				//String entry = currentSwitchStats.get(switchId).listOfFlows.get(flowId).flowId 
				//		+ "," + currentSwitchStats.get(switchId).listOfFlows.get(flowId).srcIPAddress;
				String entry = currentSwitchStats.get(switchId).listOfFlows.get(flowId).flowId;
				elephantFlows.add(entry);
			}
		}
		return elephantFlows;
	}
	
	/**
	 * Calculate the UDP rates against the given threshold
	 * @param switchId
	 * @param anomalousRate
	 * @return
	 */
	public List<String> checkRateOfSuspiciousTraffic(int switchId, int anomalousRate){
		LOG.debug("");
		LOG.debug("Checking high rate UDP/ICMP flows .....");
		long udpBytes = 0;
		
		@SuppressWarnings("unused")
		long icmpBytes = 0;
		
		long totalBytes = 0;
		List<String> flowIDs = Lists.newArrayList();
		for (String flowId : currentSwitchStats.get(switchId).listOfFlows.keySet()){
			FlowStatsTuple flowTuple = currentSwitchStats.get(switchId).listOfFlows.get(flowId);
			if (blockedIPs.contains(flowTuple.srcIPAddress)){continue;}
			if (throttledIPs.contains(flowTuple.srcIPAddress)){continue;}
			if (serverIPs.contains(flowTuple.srcIPAddress)){continue;}
			if (whiteListedSources.contains(flowTuple.srcIPAddress)){continue;}
			totalBytes += flowTuple.byteCount;
			if (flowTuple.traffic == TrafficProtocolType.ICMP) {
				icmpBytes += flowTuple.byteCount;
			}
			else if (flowTuple.traffic == TrafficProtocolType.UDP) {
				if (flowTuple.byteCount == 0){continue;}
				udpBytes += flowTuple.byteCount;
				flowIDs.add(flowId);
			}
		}
		LOG.debug("UDP Bytes {}, Total bytes {} and calculated rate {}, Anomalous Rate {}", udpBytes, totalBytes,
				(((double)udpBytes / (double)totalBytes) * 100), anomalousRate);
		if (((double)udpBytes / (double)totalBytes) * 100 >= anomalousRate){
			return flowIDs;
		}
		else {
			return null;
		}
	}
	
	public float calculateNewComersRatio(int slidingWindowSize, int newComerThreshold){
		LOG.debug(" .... Not found ...");
		LOG.debug("Checking for ratio of new comers ....");
		
//		List<String> whiteListedIPs = Lists.newArrayList();
		
		int currentNumberofFlows = 0; //This is to check how many total flows we have seen in current
		/// windows so that we can calculate the ratio of new comers
		listOfNewComers.clear();
		for (String srcIp : sourceIpConfidence.keySet()){
			if (throttledIPs.contains(srcIp)){continue;} //if some IP has already been throttled then we'll not consider it
			if (blockedIPs.contains(srcIp)){continue;}
			if (whiteListedSources.contains(srcIp)){continue;}
			//LOG.debug("---------------Current Source IP {} ", srcIp);
			int noOfSnapshots = sourceIpConfidence.get(srcIp).size();
			if (sourceIpConfidence.get(srcIp).get(noOfSnapshots - 1) == true){
				currentNumberofFlows += 1;
				//LOG.debug("CurrentNumberofFlows {}", currentNumberofFlows);
			}
			int srcIPFrequency = 0;
			if ( noOfSnapshots > slidingWindowSize) {
				srcIPFrequency = Collections.frequency(sourceIpConfidence.get(srcIp).
						subList(noOfSnapshots - slidingWindowSize, noOfSnapshots - 1), true);
				//LOG.debug("srcIPFrequency {}", srcIPFrequency);
			}
			else {
				srcIPFrequency = Collections.frequency(sourceIpConfidence.get(srcIp), true);
				//LOG.debug("srcIPFrequency {}", srcIPFrequency);
			}
			//if (((float) srcIPFrequency / (float) slidingWindowSize) * 100 < newComerThreshold){
			if (srcIPFrequency < newComerThreshold){
				//whiteListedIPs.add(srcIp);
				listOfNewComers.add(srcIp);
				//LOG.debug("srcIP Added {}", srcIp);
			}
		}
		return (((float) listOfNewComers.size()) / ((float) currentNumberofFlows)) ;
	}
	
	public int calculateWhiteList(int slidingWindowSize) {
		return 0;
	}

//	@Override
//	public void onIsLinkFlooded(IsLinkFlooded notification) {
//		LOG.debug("");
//		LOG.debug("--------- IsLinkFloodedNotification is called -----");
//		List<FloodedLinks> floodedLinkList = notification.getFloodedLinks();
//		//For elephant flows using median based filtering
//		int anomalousThreshold = Integer.parseInt(properties.getProperty("anomalousThreshold")); 
//		//To decide if there exist too much UDP traffic
//		int anomalousUDPRate = Integer.parseInt(properties.getProperty("anomalousUDPRate"));  
//		//how many previous windows do we have to consider to decide new comer
//		int historySize = Integer.parseInt(properties.getProperty("historySize")); 
//		//If there exists even a single entry in the previous windows then it is not a new comer
//		int newComerThreshold = Integer.parseInt(properties.getProperty("newComerThreshold"));
//		for (FloodedLinks floodedLink : floodedLinkList){
//			String linkId = floodedLink.getLinkId();
//			int switchId = Integer.parseInt(linkId.split(":")[1]);
//			int floodedLinkId = Integer.parseInt(linkId.split(":")[2]);
//			LOG.debug("*************************************************************");
//			LOG.debug("      Port {} of Switch {} is flooded", floodedLinkId, switchId);
//			LOG.debug("*************************************************************");
//			ddosMitigation(switchId, floodedLinkId, anomalousThreshold, 
//					anomalousUDPRate, historySize, newComerThreshold);	
//		}		
//	}
	
	@Override
	public void onIsLinkFlooded(IsLinkFlooded notification) {
		LOG.debug("--------- IsLinkFloodedNotification is called -----");
		List<FloodedLinks> floodedLinkList = notification.getFloodedLinks();
		//For elephant flows using median based filtering
		int anomalousThreshold = Integer.parseInt(properties.getProperty("anomalousThreshold")); 
		//To decide if there exist too much UDP traffic
		int anomalousUDPRate = Integer.parseInt(properties.getProperty("anomalousUDPRate"));  
		//how many previous windows do we have to consider to decide new comer
		int historySize = Integer.parseInt(properties.getProperty("historySize")); 
		//If there exists even a single entry in the previous windows then it is not a new comer
		int newComerThreshold = Integer.parseInt(properties.getProperty("newComerThreshold"));
		
		StringBuilder sb = new StringBuilder();
		for (FloodedLinks floodedLink : floodedLinkList){
			String linkId = floodedLink.getLinkId();
			sb.append(linkId);
		    sb.append("\t");
		}
		RequestToMiddleware requestToMiddleware = new RequestToMiddleware();
//		requestToMiddleware.setProperties(properties);
		requestToMiddleware.sendRequest(sb.toString());
		
		for (FloodedLinks floodedLink : floodedLinkList){
			String linkId = floodedLink.getLinkId();
			int switchId = Integer.parseInt(linkId.split(":")[1]);
			int floodedLinkId = Integer.parseInt(linkId.split(":")[2]);
			LOG.debug("*************************************************************");
			LOG.debug("      Port {} of Switch {} is flooded", floodedLinkId, switchId);
			LOG.debug("*************************************************************");
			ddosMitigation(switchId, floodedLinkId, anomalousThreshold, 
					anomalousUDPRate, historySize, newComerThreshold);	
		}		
	}
	
	public boolean blockFlow(int switchId, String flowId, String type){
		//LOG.debug("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		//LOG.debug("");
		//LOG.debug("Block flow is called to block the flows");
		int currentSnapshotIndex = networkStatistics.get(switchId).size() - 1;
		SwitchStatsSnapshot snapshot = networkStatistics.get(switchId).get(currentSnapshotIndex);
		if (snapshot.listOfFlows.containsKey(flowId)){
			FlowStatsTuple flowTuple = snapshot.listOfFlows.get(flowId);
			if (serverIPs.contains(flowTuple.srcIPAddress)){
				return false;
			}
			InstallFlowRuleInputBuilder flowRuleInputBuilder = new InstallFlowRuleInputBuilder();
			//LOG.debug("switchid {} ", switchId);
			//LOG.debug("Source IP {} ", flowTuple.srcIPAddress);
			//LOG.debug("Destination IP {} ", flowTuple.dstIPAddress);
			
			flowRuleInputBuilder.setInPortId((long)0);
			flowRuleInputBuilder.setSwitchId(switchId);
			flowRuleInputBuilder.setSrcIpAddress(flowTuple.srcIPAddress);
			flowRuleInputBuilder.setDstIpAddress(flowTuple.dstIPAddress);
			flowRuleInputBuilder.setFlowPriority(FLOW_PRIORITY);
			flowRuleInputBuilder.setIdleTimeout(0);
			flowRuleInputBuilder.setHardTimeout(0);
			//flowRuleInputBuilder.setTypeOfTraffic(
			//		flowTuple.traffic == TrafficProtocolType.UDP ? 
			//				TrafficType.UDP : TrafficType.TCP); //this is basically the traffic protocol
			//flowRuleInputBuilder.setSrcPort(10000); //Source Port
			//flowRuleInputBuilder.setDstPort(10000); //Destination Port
			//This will set the output port to Drop so that packet belongs to this flow will be immediately dropped.
			flowRuleInputBuilder.setActionOutputPort(DROP);
			if (type == "UDP"){
				flowRuleInputBuilder.setTypeOfTraffic(TrafficType.UDP);
				this.activeSDNService.installFlowRule(flowRuleInputBuilder.build());
			}
			else if (type == "Elephant"){
				flowRuleInputBuilder.setTypeOfTraffic(TrafficType.TCP);
				this.activeSDNService.installFlowRule(flowRuleInputBuilder.build());
			}
			else if (type == "NewComers"){
				if (flowTuple.traffic != null){
					if (flowTuple.traffic == TrafficProtocolType.ICMP){
						flowRuleInputBuilder.setTypeOfTraffic(TrafficType.ICMP);
					}
					else if (flowTuple.traffic == TrafficProtocolType.TCP){
						flowRuleInputBuilder.setTypeOfTraffic(TrafficType.TCP);
					}
					else if (flowTuple.traffic == TrafficProtocolType.UDP){
						//LOG.debug("UDP flow rule found ");
						flowRuleInputBuilder.setTypeOfTraffic(TrafficType.UDP);						
					}
				}	
				this.activeSDNService.installFlowRule(flowRuleInputBuilder.build());
			}
		
			if (blockedIPs.contains(flowTuple.srcIPAddress) == false){
				LOG.debug("");
				LOG.debug("&&&&&&&&&&  Source IP {} is blocked. &&&&&&&&&&", flowTuple.srcIPAddress);
				LOG.debug("");
				blockedIPs.add(flowTuple.srcIPAddress);
			}
			return true;
		}
		return false;
	}
	
	public boolean limitFlowRate(int switchId, int floodedLink, String srcIP){
		//LOG.debug("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		//LOG.debug("");
		//LOG.debug("Block flow is called to block the flows");
		int currentSnapshotIndex = networkStatistics.get(switchId).size() - 1;
		SwitchStatsSnapshot snapshot = networkStatistics.get(switchId).get(currentSnapshotIndex);
		for (String flowId : snapshot.listOfFlows.keySet()){
			if (snapshot.listOfFlows.get(flowId).srcIPAddress == srcIP){
				FlowStatsTuple flowTuple = snapshot.listOfFlows.get(flowId);
				InstallFlowRuleInputBuilder flowRuleInputBuilder = new InstallFlowRuleInputBuilder();
				//LOG.debug("switchid {} ", switchId);
				//LOG.debug("Source IP {} ", flowTuple.srcIPAddress);
				//LOG.debug("Destination IP {} ", flowTuple.dstIPAddress);
				
				flowRuleInputBuilder.setInPortId((long)0);
				flowRuleInputBuilder.setSwitchId(switchId);
				flowRuleInputBuilder.setSrcIpAddress(flowTuple.srcIPAddress);
				flowRuleInputBuilder.setDstIpAddress(flowTuple.dstIPAddress);
				flowRuleInputBuilder.setFlowPriority(FLOW_PRIORITY);
				flowRuleInputBuilder.setIdleTimeout(0);
				flowRuleInputBuilder.setHardTimeout(0);
				flowRuleInputBuilder.setActionOutputPort(Integer.toString(floodedLink));
				//As we have only two queues available with Queue IDs 0 & 1. 0 default port with full speed, however
				//1 is used for throttling the rate. In future, if we use more queues then it should be made more 
				//dynamic
				flowRuleInputBuilder.setActionSetPortQueue(1);
				//if (flowTuple.traffic != null){
					//if (flowTuple.traffic == TrafficProtocolType.ICMP){
						flowRuleInputBuilder.setTypeOfTraffic(TrafficType.ICMP);
						this.activeSDNService.installFlowRule(flowRuleInputBuilder.build());
					//}
					//else if (flowTuple.traffic == TrafficProtocolType.TCP){
						flowRuleInputBuilder.setTypeOfTraffic(TrafficType.TCP);
						this.activeSDNService.installFlowRule(flowRuleInputBuilder.build());
					//}
					//else if (flowTuple.traffic == TrafficProtocolType.UDP){
						//LOG.debug("UDP flow rule found ");
						flowRuleInputBuilder.setTypeOfTraffic(TrafficType.UDP);				
						this.activeSDNService.installFlowRule(flowRuleInputBuilder.build());
					//}
				//}	
				this.activeSDNService.installFlowRule(flowRuleInputBuilder.build());
			}
		}
				
		if (throttledIPs.contains(srcIP) == false){
			LOG.debug("");
			LOG.debug("Source IP {} is Throttled. ", srcIP);
			throttledIPs.add(srcIP);
		}
		return true;
	}

	public boolean migrateFlow(int switchId, int troubledLink, List<String>whiteListedSources){
		deletedLink = topology.findLinkUsingOneSide(switchId, troubledLink);
		topology.removelinkInfo(deletedLink.getLeftSwitch(), deletedLink.getRightSwitch());
		String floodedLink = properties.getProperty("floodedLink");
		updatePaths(floodedLink, whiteListedSources);
		topology.addLinkInfo(deletedLink.getLeftSwitch(), deletedLink.getRightSwitch(), 
				deletedLink.getLeftSwitchPortNumber(), deletedLink.getRightSwitchPortNumber());
		deletedLink = null;
		return true;
	}
		
	public void ddosMitigation(int switchId, int floodedLink, 
			int anomalousThreshold, int anomalousUDPRate, int historySize, int newComerThreshold){
		LOG.debug("----DDoSMitigation function is called ------");
		List<String>flowIds;
		
		if ((flowIds = checkRateOfSuspiciousTraffic(switchId, anomalousUDPRate)) != null ){
			//Block all UDP traffic as they represents majority of the traffic
			LOG.debug("High rate UDP/ICMP flow found ....");
			for (String flowId : flowIds){
				@SuppressWarnings("unused")
				boolean result = blockFlow(switchId, flowId, "UDP");
				//boolean result = limitFlowRate(switchId, floodedLink, flowId, "UDP");
			}
		}
		else if ((flowIds = findElephantFlows(switchId, anomalousThreshold)).isEmpty() == false){ 
			//there exists maliciously aggressive flows and we have block them
			LOG.debug("Elephant flows are found ....");
			for (String flowId : flowIds){
				LOG.debug("Elephant Flow ID {}", flowId);
				@SuppressWarnings("unused")
				boolean result = blockFlow(switchId, flowId, "Elephant");
				//boolean result = limitFlowRate(switchId, floodedLink, flowId, "Elephant"); 
			}
		}
		else if ((calculateNewComersRatio(historySize, newComerThreshold)) > newComersRatio){
			//There exists significant number of new comers and now we have to throttle them
			//all new comers source IP addresses are saved if listOfNewComers
			LOG.debug("Found high ratio of new comers ....");
			for (String srcIP : listOfNewComers){
				LOG.debug("New Comer IP", srcIP);
				@SuppressWarnings("unused")
				boolean result = limitFlowRate(switchId, floodedLink, srcIP);
			}
			String whitelist = properties.getProperty("whitelist");
			if (whiteListedSources.contains(whitelist) == false){
				whiteListedSources.add(whitelist);
			}
		}
		else if (whiteListedSources.isEmpty() == false){ //We have a static list of pre-authenticated white list sources
			//This function will see if any white listed sources is currently sending data and if yes then it will migrate 
			//its traffic to a new path
			LOG.debug("We have one white listed source IP 10.0.0.1/32. Now migrating that source");
			//whiteListedSources.add("10.0.0.1/32");
			migrateFlow(switchId, floodedLink, whiteListedSources);
		}
	}

	
	/*
	 /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	 ////------------------------------subscribeEvent() Function example  ----------------------------------------////
	 /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	 SubscribeEventInputBuilder eventInputBuilder = new SubscribeEventInputBuilder();
		eventInputBuilder.setSwitchId(1);
		
		eventInputBuilder.setDstMacAddress("00:00:00:00:00:02");
		eventInputBuilder.setSrcMacAddress("00:00:00:00:00:01");
		eventInputBuilder.setDstIpAddress("10.0.0.2/32");
		eventInputBuilder.setSrcIpAddress("10.0.0.1/32");
		eventInputBuilder.setTrafficProtocol(TrafficType.ICMP);
		//eventInputBuilder.setTrafficProtocol(TrafficType.HTTP);
		//eventInputBuilder.setTrafficProtocol(TrafficType.DNS);
		eventInputBuilder.setInPortId((long)1);
		
		eventInputBuilder.setCount((long)10);
		eventInputBuilder.setDuration((long)20);

		//eventInputBuilder.setEventAction(EventAction.DROP); //OR
		eventInputBuilder.setEventAction(EventAction.NOTIFY); //OR
		eventInputBuilder.setEventAction(EventAction.DROPANDNOTIFY);
		eventInputBuilder.setHoldNotification(5); //number of instances e.g., 5 pings
		//This value is used only for Drop & Notify case where if you want to 
		 * hold notification immediately after drop. You could use this to create a malicious behavior where sowe 
		 * switch started dropping packets and then you either migratePath or create a tunnel to escape
		 * Please check the example for clarification.
		
		this.activeSDNService.subscribeEvent(eventInputBuilder.build());
	 
	 /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	 ////------------------------------installFlowRule() Function example  ---------------------------------------////
	 /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	 InstallFlowRuleInputBuilder flowRuleInputBuilder = new InstallFlowRuleInputBuilder();
		
		flowRuleInputBuilder.setSwitchId(1);
		flowRuleInputBuilder.setInPortId((long)1);
		flowRuleInputBuilder.setSrcIpAddress("10.0.0.1/32");
		flowRuleInputBuilder.setDstIpAddress("10.0.0.2/32");
		flowRuleInputBuilder.setSrcMacAddress("00:00:00:00:00:01");
		flowRuleInputBuilder.setDstMacAddress("00:00:00:00:00:02");
		flowRuleInputBuilder.setTypeOfTraffic(TrafficType.ICMP); //this is basically the traffic protocol
		flowRuleInputBuilder.setTypeOfTraffic(TrafficType.HTTP); //and more types
		flowRuleInputBuilder.setSrcPort(10000); //Source Port
		flowRuleInputBuilder.setDstPort(10000); //Destination Port
		
		flowRuleInputBuilder.setFlowPriority(100);
		flowRuleInputBuilder.setIdleTimeout(300);
		flowRuleInputBuilder.setHardTimeout(1000);
		
		//You can perform multiple actions
		//This will change the destination IP address field of the packet to new IP
		flowRuleInputBuilder.setActionSetDstIpv4Address("10.0.0.20/32");
		//This will change the source IP address field of the packet to new IP
		flowRuleInputBuilder.setActionSetSourceIpv4Address("10.0.0.10/32");
		//This will change the TTL value of the IP header to new value
		flowRuleInputBuilder.setActionSetIpv4Ttl((short)1);
		//This will change the Destination port value of the TCP header to new value
		flowRuleInputBuilder.setActionSetTcpDstPort(8000);
		//This will change the Source port value of the TCP header to new value
		flowRuleInputBuilder.setActionSetTcpSrcPort(8000);
		//This will change the TOS field value of the IP header. Please read online about the function of this field
		flowRuleInputBuilder.setActionSetIpv4Tos(4);
		//This will set the output port value for the flow rule
		flowRuleInputBuilder.setActionOutputPort("2");
		//This will set the output port to Flood so that packet belongs to this flow will be flooded.
		flowRuleInputBuilder.setActionOutputPort(FLOOD);
		//This will set the output port to Controller so that packet belongs to this flow will be sent to controller.
		flowRuleInputBuilder.setActionOutputPort(CONTROLLER);
		//This will set the output port to Drop so that packet belongs to this flow will be immediately dropped.
		flowRuleInputBuilder.setActionOutputPort(DROP);
		
		this.activeSDNService.installFlowRule(flowRuleInputBuilder.build());
		
	 /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	 ////------------------------------sendPacketOut() Function example  -----------------------------------------////
	 /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	 SendPacketOutInputBuilder packetOutBuilder = new SendPacketOutInputBuilder();
		packetOutBuilder.setSwitchId(1);
		packetOutBuilder.setInPortNumber(1);
		packetOutBuilder.setPayload(notification.getPayload()); //This sets the payload as received during PacketIn
		packetOutBuilder.setOutputPort("2"); // OR 
		packetOutBuilder.setOutputPort(FLOOD); // OR
		packetOutBuilder.setOutputPort(DROP); // OR
		packetOutBuilder.setOutputPort(CONTROLLER);

		this.activeSDNService.sendPacketOut(packetOutBuilder.build());
					
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////------------------------------installNetworkPath() Function example  ------------------------------------////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	InstallNetworkPathInputBuilder pathInputBuilder = new InstallNetworkPathInputBuilder();
		pathInputBuilder.setSrcIpAddress("10.0.0.1/32");
		pathInputBuilder.setDstIpAddress("10.0.0.8/32");
		pathInputBuilder.setFlowPriority(300);
		pathInputBuilder.setIdleTimeout(60);
		pathInputBuilder.setHardTimeout(400); //values in seconds
		List<Integer> pathNodes = Lists.newArrayList(); //list of switches along the path
		pathNodes.add(1);
		pathNodes.add(2);
		pathNodes.add(3);
		pathNodes.add(4);
		pathNodes.add(5);
		pathNodes.add(6);
		pathNodes.add(7);
		pathNodes.add(8);
		pathInputBuilder.setSwitchesInPath(pathNodes);
		
		this.activeSDNService.installNetworkPath(pathInputBuilder.build());
		
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////------------------------------migrateNetworkPath() Function example  ------------------------------------////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		ReRouteInputBuilder reRouteInputBuilder = new ReRouteInputBuilder();
		reRouteInputBuilder.setSrcIpAddress("10.0.0.1/32");
		reRouteInputBuilder.setDstIpAddress("10.0.0.8/32");
		reRouteInputBuilder.setFlowPriority(300);
		reRouteInputBuilder.setIdleTimeout(90);
		reRouteInputBuilder.setHardTimeout(0);
		
		List<Integer> oldPathNodes = Lists.newArrayList(); //List of switches along the old path
		oldPathNodes.add(3);
		oldPathNodes.add(2);
		oldPathNodes.add(1);
		oldPathNodes.add(5);
		oldPathNodes.add(7);
		reRouteInputBuilder.setSwitchesInOldPath(oldPathNodes); //list of switches along the new path
		
		List<Integer> newPathNodes = Lists.newArrayList();
		newPathNodes.add(3);
		newPathNodes.add(2);
		newPathNodes.add(5);
		newPathNodes.add(7);
		reRouteInputBuilder.setSwitchesInNewPath(oldPathNodes);
		this.activeSDNService.reRoute(reRouteInputBuilder.build());
						
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////------------------------------createSrcDstTunnel() Function example  ------------------------------------////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	CreateSrcDstTunnelInputBuilder srcDstTunnelBuilder = new CreateSrcDstTunnelInputBuilder();
		srcDstTunnelBuilder.setCurrentSrcIpAddress("10.0.0.1/32");
		srcDstTunnelBuilder.setNewSrcIpAddress("10.0.0.10/32");
		srcDstTunnelBuilder.setCurrentDstIpAddress("10.0.0.8/32");
		srcDstTunnelBuilder.setNewDstIpAddress("10.0.0.80/32");
		srcDstTunnelBuilder.setFlowPriority(400);
		srcDstTunnelBuilder.setIdleTimeout(0);
		srcDstTunnelBuilder.setHardTimeout(0); //if you want the flow to remain forever in the switch
		
		List<Integer> pathNodes = Lists.newArrayList();
		pathNodes.add(1);
		pathNodes.add(2);
		pathNodes.add(3);
		pathNodes.add(4);
		pathNodes.add(5);
		pathNodes.add(6);
		pathNodes.add(7);
		pathNodes.add(8);
		srcDstTunnelBuilder.setSwitchesInPath(pathNodes);
		
		this.activeSDNService.createSrcDstTunnel(srcDstTunnelBuilder.build());
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////------------------------------createSrcOnlyTunnel() Function example  -----------------------------------////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	CreateSrcOnlyTunnelInputBuilder srcOnlyTunnelBuilder = new CreateSrcOnlyTunnelInputBuilder();
		srcOnlyTunnelBuilder.setCurrentSrcIpAddress("10.0.0.1/32");
		srcOnlyTunnelBuilder.setNewSrcIpAddress("10.0.0.10/32");
		srcOnlyTunnelBuilder.setDstIpAddress("10.0.0.8/32");
		srcOnlyTunnelBuilder.setFlowPriority(400);
		srcOnlyTunnelBuilder.setIdleTimeout(0);
		srcOnlyTunnelBuilder.setHardTimeout(0); //if you want the flow to remain forever in the switch
		
		List<Integer> pathNodes = Lists.newArrayList();
		pathNodes.add(1);
		pathNodes.add(2);
		pathNodes.add(3);
		pathNodes.add(4);
		pathNodes.add(5);
		pathNodes.add(6);
		pathNodes.add(7);
		pathNodes.add(8);
		srcOnlyTunnelBuilder.setSwitchesInPath(pathNodes);
		
		this.activeSDNService.createSrcOnlyTunnel(srcOnlyTunnelBuilder.build());
		
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////------------------------------createDstOnlyTunnel() Function example  -----------------------------------////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	CreateDstOnlyTunnelInputBuilder dstOnlyTunnelBuilder = new CreateDstOnlyTunnelInputBuilder();
		dstOnlyTunnelBuilder.setSrcIpAddress("10.0.0.1/32");
		dstOnlyTunnelBuilder.setCurrentDstIpAddress("10.0.0.8/32");
		dstOnlyTunnelBuilder.setNewDstIpAddress("10.0.0.80/32");
		dstOnlyTunnelBuilder.setFlowPriority(400);
		dstOnlyTunnelBuilder.setIdleTimeout(0);
		dstOnlyTunnelBuilder.setHardTimeout(0); //if you want the flow to remain forever in the switch
		
		List<Integer> pathNodes = Lists.newArrayList();
		pathNodes.add(1);
		pathNodes.add(2);
		pathNodes.add(3);
		pathNodes.add(4);
		pathNodes.add(5);
		pathNodes.add(6);
		pathNodes.add(7);
		pathNodes.add(8);
		dstOnlyTunnelBuilder.setSwitchesInPath(pathNodes);
		
		this.activeSDNService.createDstOnlyTunnel(dstOnlyTunnelBuilder.build());
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////------------------------------removeAllFlowsFromASwitch() Function example  -----------------------------////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	RemoveAllFlowsFromASwitchInputBuilder removeFlowsInputBuilder = new RemoveAllFlowsFromASwitchInputBuilder();
		removeFlowsInputBuilder.setSwitchId(1);
		
		this.activeSDNService.removeAllFlowsFromASwitch(removeFlowsInputBuilder.build()); 
		
		
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////------------------------------removeAFlowRuleFromSwitch() Function example  -----------------------------////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	RemoveAFlowRuleFromSwitchInputBuilder removeFlowInputBuilder = new RemoveAFlowRuleFromSwitchInputBuilder();
		//you have to provide a flowkey/flowID value. When you use function to get all flows from a switch
		//each flow will have a unique id and you can use that to delete a flow from the switch
		removeFlowInputBuilder.setFlowKey("value"); 
		removeFlowInputBuilder.setSwitchId(1);
		this.activeSDNService.removeAFlowRuleFromSwitch(removeFlowInputBuilder.build());
	
	 /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////------------------------------removeEventFromSwitch() Function example  -----------------------------////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	RemoveEventFromSwitchInputBuilder removeEvent = new RemoveEventFromSwitchInputBuilder();
		//When you create an event you should keep a copy of that event locally.
		removeEvent.setEventId("value");
		removeEvent.setSwitchId(1);
		this.activeSDNService.removeEventFromSwitch(removeEvent.build());
		
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////------------------------------getSwitchFlowTable() Function example  -----------------------------////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	you can call this function to get all flows rules from a switch. This function will automatically populate a local 
	HashMap data structure called networkConfiguration<SwitchID, ListOfFlowRules>  
	
		getSwitchFlowTable (int switchId)
		
		
	 */

	public HashMap<String, ConnectedHostInfo> getHostTable() {
		return hostTable;
	}

	public HashMap<String, List<String>> getInstalledPaths() {
		return installedPaths;
	}

	public NetworkGraph getTopology() {
		return topology;
	}
}
