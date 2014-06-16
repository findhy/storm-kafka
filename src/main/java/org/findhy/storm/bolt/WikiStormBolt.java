package org.findhy.storm.bolt;

import java.util.Map;
import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import org.findhy.storm.kafka.KafkaProperties;
import org.findhy.storm.mode.Wikipedia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

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
		props.put("metadata.broker.list", KafkaProperties.broker_list);
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		props.put("partitioner.class", KafkaProperties.partitioner_class);
		props.put("request.required.acks", "1");
		ProducerConfig config = new ProducerConfig(props);

		producer = new Producer<String, String>(config);
	}

	@Override
	public void execute(Tuple input) {
		LOG.info("begin to execute tuple in bolt from WikiStormBolt");
		Wikipedia wiki = JSON.parseObject(input.getValue(0).toString(),Wikipedia.class);
		KeyedMessage<String, String> data = new KeyedMessage<String, String>(KafkaProperties.storm_bolt_topic,JSON.toJSONString(wiki));
		producer.send(data);
		_collerctor.ack(input);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("kafkabolt"));
	}

}

