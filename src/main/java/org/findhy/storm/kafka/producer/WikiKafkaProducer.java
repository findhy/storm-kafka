package org.findhy.storm.kafka.producer;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.findhy.storm.kafka.KafkaProperties;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import kafka.server.KafkaConfig;

/**
 * @author sunwei_oversea
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
		//System.out.println("received: " + message);
		KeyedMessage<String, String> data = new KeyedMessage<String, String>(KafkaProperties.producer_topic,"wiki",message);
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
		props.put("metadata.broker.list", KafkaProperties.broker_list);
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		props.put("partitioner.class", KafkaProperties.partitioner_class);
		props.put("request.required.acks", "1");
		ProducerConfig config = new ProducerConfig(props);
		producer = new Producer<String,String>(config);
		
		WikiKafkaProducer c = new WikiKafkaProducer(new URI(KafkaProperties.producer_url),new Draft_10()); 
		c.connect();
	}

}
