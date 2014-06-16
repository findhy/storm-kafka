package org.findhy.storm.topology;

import org.findhy.storm.bolt.WikiStormBolt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;

import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.TopologyBuilder;

public class WikiStormTopology {
	
	public static final Logger LOG = LoggerFactory.getLogger(WikiStormTopology.class);
	
	public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException {
		
		//获取topology名称
		String topologyName = args[0];
		String kafkaTopicName = args[1];
		
		SpoutConfig kafkaConfig = new SpoutConfig(new ZkHosts("master"), "wikipedia", "", "kafka-storm-spout");
        kafkaConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("wikispout", new KafkaSpout(kafkaConfig),1);
		builder.setBolt("wikibolt", new WikiStormBolt(kafkaTopicName),1).shuffleGrouping("wikispout");
		
		Config conf = new Config();
	    conf.setDebug(true);
	    conf.setNumWorkers(2);
	    StormSubmitter.submitTopology(topologyName, conf, builder.createTopology());
	}
}

