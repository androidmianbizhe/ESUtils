package com.example.es.config;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
//import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

@Configuration
public class ESConfig {

    @Value("${elasticsearch.ip}")
    private String hostName;

    @Value("${elasticsearch.port}")
    private Integer port;

    @Value("${elasticsearch.cluster.name}")
    private String clusterName;

    @Value("${elasticsearch.pool}")
    private String poolSize;

    @Bean
    public TransportClient transportClient() {
        TransportClient client = null;
        try {
            Settings settings = Settings.builder()
                    .put("client.transport.sniff", true)
                    .put("cluster.name", clusterName).build();
            client = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new TransportAddress(new InetSocketAddress(hostName, 9300)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return client;
    }




}
