package com.liu.kafka.simple;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.AlterConfigsResult;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.apache.kafka.clients.admin.DescribeConfigsResult;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.config.ConfigResource;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * kafka管理.
 * 0.11.0.0版本开始才支持
 * @Auther: liudongfei
 * @Date: 2019/12/25 14:10
 * @Description:
 */
public class MyKafkaAdminClient {

    /**
     * 创建topic.
     * @param client client
     * @param topic topic
     */
    public void createTopic(AdminClient client, String topic) throws ExecutionException, InterruptedException {
        NewTopic newTopic = new NewTopic(topic, 2, (short) 1);

        CreateTopicsResult result = client.createTopics(Collections.singleton(newTopic));
        result.all().get();
        client.close();
    }

    /**
     * 查看所有的topic.
     * @param client client
     */
    public void listTopic(AdminClient client) throws ExecutionException, InterruptedException {
        ListTopicsResult result = client.listTopics();
        KafkaFuture<Collection<TopicListing>> listings = result.listings();
        Collection<TopicListing> topicListings = listings.get();
        for (TopicListing topicListing : topicListings) {
            System.out.println(topicListing.name());
        }
        client.close();
    }

    /**
     * 查看指定topic信息.
     * @param client client
     * @param topic topic
     */
    public void describeTopic(AdminClient client, String topic) throws ExecutionException, InterruptedException {
        DescribeTopicsResult result = client.describeTopics(Collections.singleton(topic));
        result.all().get();
    }

    /**
     * 删除指定的topic.
     * @param client client
     * @param topic topic
     */
    public void deleteTopic(AdminClient client, String topic) throws ExecutionException, InterruptedException {
        DeleteTopicsResult result = client.deleteTopics(Collections.singleton(topic));
        result.all().get();
    }

    /**
     * 查看topic的配置信息.
     * @param client client
     * @param topic topic
     */
    public void describeTopicConfig(AdminClient client, String topic) throws ExecutionException, InterruptedException {
        ConfigResource resource = new ConfigResource(ConfigResource.Type.TOPIC, topic);
        DescribeConfigsResult result = client.describeConfigs(Collections.singleton(resource));
        Config config = result.all().get().get(resource);
        System.out.println(config);
        client.close();
    }

    /**
     * 修改topic的配置信息.
     * @param client client
     * @param topic topic
     */
    public void alterTopicConfig(AdminClient client, String topic) throws ExecutionException, InterruptedException {
        ConfigResource resource = new ConfigResource(ConfigResource.Type.TOPIC, topic);
        ConfigEntry entry = new ConfigEntry("cleanup.policy", "compact");
        Config config = new Config(Collections.singleton(entry));
        HashMap<ConfigResource, Config> configs = new HashMap<>();
        configs.put(resource, config);
        AlterConfigsResult result = client.alterConfigs(configs);
        result.all().get();
    }

    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String brokerList = "mincdh:9092";
        String topic = "topic-demo2";
        Properties prop = new Properties();
        prop.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        prop.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
        AdminClient client = AdminClient.create(prop);


    }
}
