package com.liu.logmsgcollect;

import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.ShardSearchFailure;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * produce translog from es.
 * @Auther: liudongfei
 * @Date: 2019/4/19 13:38
 * @Description:
 */
public class ESearchTransLogProducer {

    /**
     * get es client.
     * @return
     */
    public TransportClient getClient() throws UnknownHostException {
        Settings settings = Settings.builder()
                .put("cluster.name", "my-cluster")
                .put("client.transport.sniff", false)
                .build();
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("centos-7.shared"), 9300));
        return client;

    }

    /**
     * create index.
     * @param client client
     * @return
     */
    public CreateIndexResponse createIndex(TransportClient client) throws ExecutionException, InterruptedException {
        CreateIndexRequest request = new CreateIndexRequest("mess");
        // 2、设置索引的settings
        request.settings(Settings.builder().put("index.number_of_shards", 3) // 分片数
                        .put("index.number_of_replicas", 2) // 副本数
                //.put("analysis.analyzer.default.tokenizer", "ik_smart") // 默认分词器
        );

        // 3、设置索引的mappings
        request.mapping("_doc",
                "  {\n"
                        + "    \"_doc\": {\n"
                        + "      \"properties\": {\n"
                        + "        \"message\": {\n"
                        + "          \"type\": \"text\"\n"
                        + "        }\n"
                        + "      }\n"
                        + "    }\n"
                        + "  }",
                XContentType.JSON);

        // 4、 设置索引的别名
        request.alias(new Alias("mmm"));

        // 5、 发送请求
        CreateIndexResponse createIndexResponse = client.admin().indices().create(request).get();
        return createIndexResponse;
    }

    /**
     * index document.
     * @param client client
     * @return
     */
    public IndexResponse indexDocument(TransportClient client) throws ExecutionException, InterruptedException {
        IndexRequest request = new IndexRequest("mess", "_doc", "11");
        String jsonString = "{"
                + "\"user\":\"kimchy\","
                + "\"postDate\":\"2013-01-30\","
                + "\"message\":\"trying out Elasticsearch\""
                + "}";
        request.source(jsonString, XContentType.JSON);
        return client.index(request).get();
    }

    /**
     * get docment.
     * @param client client
     * @return response
     * @throws ExecutionException e
     * @throws InterruptedException e
     */
    public GetResponse getDocment(TransportClient client) throws ExecutionException, InterruptedException {
        GetRequest request = new GetRequest("mess", "_doc", "11");
        //选择返回的字段
        String[] includes = new String[]{"message", "*Date"};
        String[] excludes = Strings.EMPTY_ARRAY;
        FetchSourceContext fetchSourceContext = new FetchSourceContext(true, includes, excludes);
        request.fetchSourceContext(fetchSourceContext);
        return client.get(request).get();
    }

    /**
     * bulk document.
     * @param client client
     * @throws ExecutionException e
     * @throws InterruptedException e
     */
    public void bulkDocument(TransportClient client) throws ExecutionException, InterruptedException {
        BulkRequest request = new BulkRequest();
        request.add(new IndexRequest("mess", "_doc", "1")
                .source(XContentType.JSON,"field", "foo"));
        request.add(new IndexRequest("mess", "_doc", "2")
                .source(XContentType.JSON,"field", "bar"));
        request.add(new IndexRequest("mess", "_doc", "3")
                .source(XContentType.JSON,"field", "baz"));

        BulkResponse bulkItemResponses = client.bulk(request).get();
        for (BulkItemResponse bulkItemResponse : bulkItemResponses) {
            DocWriteResponse itemResponse = bulkItemResponse.getResponse();
            if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.INDEX
                    || bulkItemResponse.getOpType() == DocWriteRequest.OpType.CREATE) {
                IndexResponse indexResponse = (IndexResponse) itemResponse;
                System.out.println(indexResponse.getResult());
                //TODO 新增成功的处理

            } else if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.UPDATE) {
                UpdateResponse updateResponse = (UpdateResponse) itemResponse;
                System.out.println(updateResponse.getResult());
                //TODO 修改成功的处理

            } else if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.DELETE) {
                DeleteResponse deleteResponse = (DeleteResponse) itemResponse;
                System.out.println(deleteResponse.getResult());
                //TODO 删除成功的处理
            }
        }

    }

    public void searchDocument(TransportClient client) throws ExecutionException, InterruptedException {
        SearchRequest request = new SearchRequest("mess");
        request.types("_doc");
        // 2、用SearchSourceBuilder来构造查询请求体 ,请仔细查看它的方法，构造各种查询的方法都在这。
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.termQuery("field", "bar"));
        //sourceBuilder.from(0);
        //sourceBuilder.size(10);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        SearchResponse searchResponse = client.search(request).get();
        //4、处理响应
        //搜索结果状态信息
        RestStatus status = searchResponse.status();
        TimeValue took = searchResponse.getTook();
        Boolean terminatedEarly = searchResponse.isTerminatedEarly();
        boolean timedOut = searchResponse.isTimedOut();

        //分片搜索情况
        int totalShards = searchResponse.getTotalShards();
        int successfulShards = searchResponse.getSuccessfulShards();
        int failedShards = searchResponse.getFailedShards();
        for (ShardSearchFailure failure : searchResponse.getShardFailures()) {
            // failures should be handled here
            System.out.println(failure);
        }
        //处理搜索命中文档结果
        SearchHits hits = searchResponse.getHits();

        long totalHits = hits.getTotalHits();
        float maxScore = hits.getMaxScore();
        System.out.println("maxScore:\t" + maxScore);

        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            // do something with the SearchHit

            String index = hit.getIndex();
            String type = hit.getType();
            String id = hit.getId();
            float score = hit.getScore();

            //取_source字段值
            String sourceAsString = hit.getSourceAsString(); //取成json串
            Map<String, Object> sourceAsMap = hit.getSourceAsMap(); // 取成map对象

            System.out.println(sourceAsString);
        }
    }



    public static void main(String[] args) throws UnknownHostException, ExecutionException, InterruptedException {
        ESearchTransLogProducer logProducer = new ESearchTransLogProducer();
        TransportClient client = logProducer.getClient();
        //CreateIndexResponse createIndexResponse = logProducer.createIndex(client);
        //IndexResponse indexResponse = logProducer.indexDocument(client);
        //if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
            //System.out.println("新增文档成功");
        //} else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
            //System.out.println("修改文档成功");
        //}
        //GetResponse getResponse = logProducer.getDocment(client);
        //if (getResponse.isExists()) {
            //long version = getResponse.getVersion();
            //String sourceAsString = getResponse.getSourceAsString();
            //System.out.println(sourceAsString);
        //}
        //logProducer.bulkDocument(client);
        logProducer.searchDocument(client);


    }
}
