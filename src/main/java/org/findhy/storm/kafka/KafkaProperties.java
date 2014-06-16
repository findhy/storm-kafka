package org.findhy.storm.kafka;

public interface KafkaProperties {

	final static String broker_list = "master:9092";
	final static String partitioner_class = "org.findhy.storm.kafka.partitioner.WikiPartitioner";
	final static String producer_url = "ws://wikimon.hatnote.com:9000";
	
	final static String producer_topic = "wikipedia";
	final static String storm_bolt_topic = "wikipedia-from-storm";
}
