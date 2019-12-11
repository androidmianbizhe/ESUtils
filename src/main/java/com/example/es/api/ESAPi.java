package com.example.es.api;

import com.example.es.bean.ESIndexSettingAndMapping;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


@Component
public class ESAPi {

    @Autowired
    TransportClient client;

    // 创建文档
    public boolean createIndexAndMapping(String indexName, ESIndexSettingAndMapping mapping) {
        if (!indexExists(indexName)) {
            CreateIndexRequest request = new CreateIndexRequest(indexName);
            ActionFuture<CreateIndexResponse> res = client.admin().indices().create(request);
            try {
                return res.get().isAcknowledged();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // 检查索引是否存在
    public boolean indexExists(String indexName) {
        IndicesAdminClient adminClient = client.admin().indices();

        IndicesExistsRequest request = new IndicesExistsRequest(indexName);
        IndicesExistsResponse response = adminClient.exists(request).actionGet();
        if (response.isExists()) {
            return true;
        }
        return false;
    }

    // 删除索引
    public boolean deleteIndex(String indexName) {
        DeleteIndexRequest request = new DeleteIndexRequest(indexName);
        ActionFuture<DeleteIndexResponse> delete = client.admin().indices().delete(request);
        try {
            return delete.get().isAcknowledged();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }
}
