package com.example.es.api;

import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ESAPiTest {
//
//    @Autowired
//    private TransportClient client;

    @Test
    public void createIndex() throws UnknownHostException {
        //设置集群名称

        TransportClient client = null;
        try {
            Settings settings = Settings.builder()
                    .put("client.transport.sniff", true)
                    .put("cluster.name", "elasticsearch").build();
            client = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new TransportAddress(new InetSocketAddress("127.0.0.1", 9300)));
        } catch (Exception e) {
            e.printStackTrace();
        }
//        return client;
    }

    @Test
    public void addMapping() {
    }

    @Test
    public void deleteIndex() {
    }

    @Test
    public void createDoc() {
    }

    @Test
    public void updateDoc() {
    }

    @Test
    public void get() {
    }

    @Test
    public void queryByFilter() {
    }

    @Test
    public void deleteDoc() {
    }

    @Test
    public void deleteByQuery() {
    }
}