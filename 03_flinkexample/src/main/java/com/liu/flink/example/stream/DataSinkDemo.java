package com.liu.flink.example.stream;

import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer010;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;

import java.util.Arrays;

public class DataSinkDemo {

    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> streamSource = env.fromCollection(Arrays.asList("Alex", "Fei"));
        streamSource.writeAsText("", FileSystem.WriteMode.OVERWRITE);
        streamSource.writeToSocket("", 9999, new SimpleStringSchema());
        FlinkKafkaProducer010<String> flinkKafkaProducer010 = new FlinkKafkaProducer010<>(
                "localhost:9092", "mytopic", new SimpleStringSchema());
        streamSource.addSink(flinkKafkaProducer010);

    }
}
