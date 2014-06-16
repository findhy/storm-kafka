storm-kafka
===========
#### 说明 ####
Storm与Kafka框架集成，依赖https://github.com/wurstmeister/storm-kafka-0.8-plus 
 
参考https://github.com/wurstmeister/storm-kafka-0.8-plus-test  

详细介绍可以参考这里：http://findhy.com/blog/2014/06/16/storm-kafka-dev/  

包括：

- Kafka的Producer test，通过Websocket读取Wikipedia的实时修改数据
- Storm的Spout test，订阅Kafka的producer，数据在bolt中处理完成之后再次发送到Kafka中


#### 下载 ####

    git clone https://github.com/findhy/storm-kafka.git

#### 修改KafkaProperties ####

    final static String storm_bolt_topic = "wikipedia-from-storm-2";

#### 创建Kafka topic接受Storm处理完的数据 ####

    cd /home/hadoop/kafka_2.8.2-0.8.1
    ./bin/kafka-topics.sh --create --zookeeper master:2181 --replication-factor 1 --partitions 1 --topic wikipedia-from-storm-2

#### 打包 ####

    cd storm-kafka
	mvn clean
    mvn clean package -P cluster

#### 提交Kafka Producer ####

    java -classpath ./target/storm-kafka-0.1.0-SNAPSHOT-jar-with-dependencies.jar org.findhy.storm.kafka.producer.WikiKafkaProducer

#### 提交Storm Topology ####

    storm jar ./target/storm-kafka-0.1.0-SNAPSHOT-jar-with-dependencies.jar org.findhy.storm.topology.WikiStormTopology -c nimbus.host=10.0.1.254 storm-kafka-2 

#### 订阅Storm bolt发过来的数据 ####

    cd /home/hadoop/kafka_2.8.2-0.8.1
    ./bin/kafka-console-consumer.sh --zookeeper master:2181 --topic wikipedia-from-storm-2 --from-beginning

可以看到提取出用户和文章标题的信息