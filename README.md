storm-kafka
===========
#### 说明 ####
Storm与Kafka集成测试框架，依赖这个库：https://github.com/wurstmeister/storm-kafka-0.8-plus 
 
部分参考：https://github.com/wurstmeister/storm-kafka-0.8-plus-test  

框架详细介绍可以看这里：http://findhy.com/blog/2014/06/16/storm-kafka-dev/  

包括：

- Kafka的Producer test，通过Websocket读取Wikipedia的实时修改数据
- Storm的Spout test，订阅Kafka的producer，数据在bolt中处理完成之后再次发送到Kafka中


#### 1.启动zookeeper ####

    ./bin/zkServer.sh start  

#### 2.启动Kafka ####

    nohup ./bin/kafka-server-start.sh ./config/server.properties &

这时候用jps看，会有一个Kafka的进程

#### 3.下载代码 ####

    git clone https://github.com/findhy/storm-kafka.git

#### 4.修改KafkaProperties ####

    final static String storm_bolt_topic = "wikipedia-from-storm-2";

#### 5.创建Kafka topic接受Storm处理完的数据 ####

    cd /home/hadoop/kafka_2.8.2-0.8.1
    ./bin/kafka-topics.sh --create --zookeeper master:2181 --replication-factor 1 --partitions 1 --topic wikipedia-from-storm-2

#### 6.打包 ####

    cd storm-kafka
	mvn clean
    mvn clean package -P cluster

#### 7.提交Kafka Producer ####

    java -classpath ./target/storm-kafka-0.1.0-SNAPSHOT-jar-with-dependencies.jar org.findhy.storm.kafka.producer.WikiKafkaProducer

#### 8.提交Storm Topology ####

    storm jar ./target/storm-kafka-0.1.0-SNAPSHOT-jar-with-dependencies.jar org.findhy.storm.topology.WikiStormTopology -c nimbus.host=10.0.1.254 storm-kafka-2 

#### 9.console订阅Storm bolt发过来的数据 ####

    cd /home/hadoop/kafka_2.8.2-0.8.1
    ./bin/kafka-console-consumer.sh --zookeeper master:2181 --topic wikipedia-from-storm-2 --from-beginning

#### 10.consumer类订阅Storm bolt发过来的数据 ####

    java -classpath ./target/storm-kafka-0.1.0-SNAPSHOT-jar-with-dependencies.jar org.findhy.storm.kafka.consumer.WikiKafkaConsumer wikipedia-from-storm-2