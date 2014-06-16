package org.findhy.storm.bolt;

import java.util.Map;
import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

public class WikiStormBolt extends BaseRichBolt {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5318068835682735850L;

	public static Logger LOG = LoggerFactory.getLogger(WikiStormBolt.class);
	OutputCollector _collerctor;
	Producer<String, String> producer;

	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		LOG.info("begin to prepare in bolt from CyouSendToKafkaBolt");
		this._collerctor = collector;
		
		Properties props = new Properties();
		props.put("metadata.broker.list", "master:9092");
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		props.put("partitioner.class", "org.findhy.storm.kafka.partitioner.WikiPartitioner");
		props.put("request.required.acks", "1");
		ProducerConfig config = new ProducerConfig(props);

		producer = new Producer<String, String>(config);
	}

	@Override
	public void execute(Tuple input) {
		LOG.info("begin to execute tuple in bolt from CyouSendToKafkaBolt");
		KeyedMessage<String, String> data = new KeyedMessage<String, String>("wikipedia-from-storm",input.getValue(0).toString());
		producer.send(data);
		_collerctor.ack(input);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("kafkabolt"));
	}

}

