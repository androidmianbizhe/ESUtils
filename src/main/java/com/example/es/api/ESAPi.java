package com.example.es.api;

import com.example.es.bean.ESIndexSettingAndMapping;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;


@Component
public class ESAPi {

    @Autowired
    TransportClient client;

    // cerateIndex
    public boolean createIndex(String indexName, ESIndexSettingAndMapping mapping) {
        if (!indexExists(indexName)) {
            CreateIndexRequest request = new CreateIndexRequest(indexName);
            CreateIndexResponse createIndexResponse = client.admin().indices()
                    .prepareCreate(indexName)
                    .setSettings(
                            Settings.builder()
                                    .put("index.number_of_shards", mapping.getSetting().getNumber_of_shards())
                                    .put("index.number_of_replicas", mapping.getSetting().getNumber_of_replicas())
                    ).get();
            return createIndexResponse.isAcknowledged();
        }
        return false;
    }

    // 创建文档
    public boolean putMapping(String indexName, ESIndexSettingAndMapping mapping) throws ExecutionException, InterruptedException, IOException {
        if (indexExists(indexName)) {
            CreateIndexRequest request = new CreateIndexRequest(indexName);
            CreateIndexRequestBuilder indexRequestBuilder = client.admin().indices()
                    .prepareCreate(indexName);

            //创建mapping约束字段
            XContentBuilder xContentMappingBuilder = jsonBuilder()
                    .startObject() //{
                    .startObject(mapping.getMappings().getDocumentName()) // type
                    .startObject("properties"); // properties

            for (ESIndexSettingAndMapping.Mappings.Property p : mapping.getMappings().getProperties()) {

                if (StringUtils.isEmpty(p.getProperName()) || StringUtils.isEmpty(p.getProperType())) {
                    continue;
                } else {
                    xContentMappingBuilder.startObject(p.getProperName());

                    // 属性
                    xContentMappingBuilder.field("type", p.getProperType());

                    if (StringUtils.isEmpty(p.getStore())) { //store
                        xContentMappingBuilder.field("store", p.getStore());
                    }
                    if (StringUtils.isEmpty(p.getAnalyzer())) { //store
                        xContentMappingBuilder.field("analyzer", p.getAnalyzer());
                    }
                    if (StringUtils.isEmpty(p.getStore())) { //store
                        xContentMappingBuilder.field("index", p.getIndex());
                    }
                    if (StringUtils.isEmpty(p.getStore())) { //store
                        xContentMappingBuilder.field("format", p.getFormat());
                    }
                    xContentMappingBuilder.endObject();
                }
            }

            xContentMappingBuilder.endObject(); //properties
            xContentMappingBuilder.endObject(); // typeName
            xContentMappingBuilder.endObject(); // }

            //添加mapping 绑定到 index
            CreateIndexResponse response = indexRequestBuilder
                    .addMapping(mapping.getMappings().getDocumentName(), xContentMappingBuilder)
                    .get();
            return response.isAcknowledged();
        }
        return false;
    }

    // 检查索引是否存在
    public boolean indexExists(String indexName) {
        IndicesAdminClient adminClient = client.admin().indices();

        IndicesExistsRequest request = new IndicesExistsRequest(indexName);
        IndicesExistsResponse response = adminClient.exists(request).actionGet();
        return response.isExists();
    }

    // 删除索引
    public boolean deleteIndex(String indexName) {
        DeleteIndexRequest request = new DeleteIndexRequest(indexName);
        ActionFuture<DeleteIndexResponse> delete = client.admin().indices().delete(request);
        return delete.actionGet().isAcknowledged();
    }

    // 添加文档数据
    public boolean addDocument(String indexName, String documentName, HashMap<String, Object> jsonMap) {
        IndexResponse indexResponse = client.prepareIndex(indexName, documentName).setSource(jsonMap).get();
        return indexResponse.status() == RestStatus.OK;
    }

    // 更新文档数据
    public boolean updateDocument(String indexName, String documentName, String id, HashMap<String, Object> jsonMap) {
        UpdateRequest updateRequest = new UpdateRequest(indexName, documentName, id).doc(jsonMap);
        UpdateResponse updateResponse = null;
        try {
            updateResponse = client.update(updateRequest).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return updateResponse.getGetResult().isExists();
    }

    // 删除数据
    public void deleteDocument(String indexName, String documentName, String id) {
        DeleteRequest deleteRequest = new DeleteRequest(indexName, documentName, id);
        try {
            client.delete(deleteRequest).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    // 通过id查找
    public Map<String, Object> getResById(String index, String document, String id) {
        GetResponse response = client.prepareGet(index, document, id).get();
        return response.getSource();
    }

    // 查找
    public List<Map<String, Object>> search(String index, String document, String property, String key, Integer start, Integer size) {
        final List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        SearchResponse searchResponse = client.prepareSearch().setTypes(index, document)
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.matchQuery(property, key))
                .setFrom(start)
                .setSize(size)
                .setExplain(true)
                .get();
        long totalHits1 = searchResponse.getHits().totalHits;  //命中个数
        for (SearchHit searchHit : searchResponse.getHits().getHits()) {
            final Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            list.add(sourceAsMap);
        }
        return list;
    }

    // 批量增加
    public void addDocuments(String indexName, String document, HashMap<String, HashMap<String, Object>> map) throws IOException {
        // 创建批量请求构造器
        BulkRequestBuilder bulkRequest = client.prepareBulk();

        // 遍历数据
        for (String key : map.keySet()) {

            // 其中一条数据
            HashMap<String, Object> doc = map.get(key);
            XContentBuilder xContentBuilder = jsonBuilder().startObject();

            for (String p : doc.keySet()) {
                // 注入属性 值
                xContentBuilder.field(p, doc.get(p));
            }
            xContentBuilder.endObject();

            // indexName 索引, document 文档, key id
            IndexRequestBuilder builder = client.prepareIndex(indexName, document, key)
                    .setSource(xContentBuilder);
            // 添加到 构造器里面
            bulkRequest.add(builder);
        }
        bulkRequest.get();
    }
}
