storm-kafka
===========
#### 说明 ####
Storm与Kafka框架集成，依赖https://github.com/wurstmeister/storm-kafka-0.8-plus 
 
参考https://github.com/wurstmeister/storm-kafka-0.8-plus-test修改，包括：

- Kafka的Producer test，通过Websocket读取Wikipedia的实时修改数据
- Storm的Spout test，订阅Kafka的producer，数据在bolt中处理完成之后再次发送到Kafka中


#### 下载 ####

    git clone https://github.com/findhy/storm-kafka.git

#### 打包 ####

    cd storm-kafka
    mvn clean package -P cluster

#### 提交Kafka Producer ####

    java -classpath ./target/storm-kafka-0.1.0-SNAPSHOT-jar-with-dependencies.jar org.findhy.storm.kafka.producer.WikiKafkaProducer

#### 提交Storm Topology ####

    storm jar storm-kafka-0.1.0-SNAPSHOT-jar-with-dependencies.jar org.findhy.storm.topology.WikiStormTopology -c nimbus.host=10.0.1.254 storm-kafka-2


#### 订阅Storm bolt发过来的数据 ####

    kafka-console-consumer.sh --zookeeper master:2181 --topic wikipedia-from-storm --from-beginning

可以看到提取出用户和文章标题的信息