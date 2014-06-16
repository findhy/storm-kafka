package org.findhy.storm.kafka.producer;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
/**
 * storm.kafka.producer.CyouKafkaProducer
 * java -classpath storm-kafka-0.8-plus-test-0.1.0-SNAPSHOT-jar-with-dependencies.jar storm.kafka.producer.CyouKafkaProducer
 * @author sunwei_oversea
 *
 */
public class WikiKafkaProducer extends WebSocketClient{
	
	static Producer<String,String> producer;
	
	public WikiKafkaProducer(URI serverUri, Draft draft) {
		super(serverUri, draft);
	}

	public WikiKafkaProducer(URI serverURI) {
		super(serverURI);
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		System.out.println("opened connection");
		// if you plan to refuse connection based on ip or httpfields overload:
		// onWebsocketHandshakeReceivedAsClient
	}

	@Override
	public void onMessage(String message) {
		sendData(message);
	}

	public void sendData(String message){
		KeyedMessage<String, String> data = new KeyedMessage<String, String>("wikipedia","wiki",message);
		producer.send(data);
	}
	
	public void onFragment(Framedata fragment) {
		System.out.println("received fragment: "
				+ new String(fragment.getPayloadData().array()));
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		// The codecodes are documented in class
		// org.java_websocket.framing.CloseFrame
		System.out.println("Connection closed by "
				+ (remote ? "remote peer" : "us"));
	}

	@Override
	public void onError(Exception ex) {
		ex.printStackTrace();
		// if the error is fatal then onClose will be called additionally
	}

	public static void main(String[] args) throws URISyntaxException {
		Properties props = new Properties();
		props.put("metadata.broker.list", "master:9092");
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		props.put("partitioner.class", "org.findhy.storm.kafka.partitioner.WikiPartitioner");
		props.put("request.required.acks", "1");
		ProducerConfig config = new ProducerConfig(props);
		
		producer = new Producer<String,String>(config);
		
		WikiKafkaProducer c = new WikiKafkaProducer(new URI("ws://wikimon.hatnote.com:9000"),new Draft_10()); 
		c.connect();
	}
	
	/*public static void main(String[] args){
		Properties props = new Properties();
		props.put("metadata.broker.list", "master:9092");
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		//props.put("partitioner.class", "example.producer.SimplePartitioner");
		props.put("request.required.acks", "1");
		ProducerConfig config = new ProducerConfig(props);
		
		Producer<String,String> producer = new Producer<String,String>(config);
		
		while(true){
			KeyedMessage<String, String> data = new KeyedMessage<String, String>("Wikipedia","1","2");
			producer.send((Seq<KeyedMessage<String, String>>) data);
		}
	}*/

}
