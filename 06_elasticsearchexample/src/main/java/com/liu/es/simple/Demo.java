package com.liu.es.simple;

import com.liu.es.util.ESUtil;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexResponse;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.elasticsearch.cluster.health.ClusterIndexHealth;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.PrefixQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.RegexpQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.filter.Filters;
import org.elasticsearch.search.aggregations.bucket.missing.Missing;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.percentiles.Percentiles;
import org.elasticsearch.search.aggregations.metrics.stats.Stats;
import org.elasticsearch.search.aggregations.metrics.stats.extended.ExtendedStats;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * elastic的增删改查.
 * @Auther: liudongfei
 * @Date: 2019/7/12 17:09
 * @Description:
 */
public class Demo {

    /**
     * 检查索引是否存在.
     * @throws IOException e
     */
    public static void isExistIdx() throws IOException {
        Settings settings = Settings.builder().put("cluster.name", "my-cluster").build();
        TransportClient client = new PreBuiltTransportClient(settings).addTransportAddress(
                new TransportAddress(InetAddress.getByName("mincdh"), 9300));
        IndicesAdminClient indicesAdminClient = client.admin().indices();
        IndicesExistsResponse existsResponse = indicesAdminClient.prepareExists("abc").get();
        System.out.println(existsResponse.isExists());

        client.close();
    }

    /**
     * 创建索引.
     * @throws UnknownHostException e
     */
    public static void createIdx() throws UnknownHostException {
        Settings settings = Settings.builder().put("cluster.name", "my-cluster").build();
        TransportClient client = new PreBuiltTransportClient(settings).addTransportAddress(
                new TransportAddress(InetAddress.getByName("mincdh"), 9300));
        IndicesAdminClient indicesAdminClient = client.admin().indices();
        CreateIndexResponse createIndexResponse = indicesAdminClient.prepareCreate("myindex").get();
        boolean indexResponseAcknowledged = createIndexResponse.isAcknowledged();
        System.out.println(indexResponseAcknowledged);

    }

    /**
     * 根据索引id查看索引.
     * @throws IOException e
     */
    public static void queryIdx() throws IOException {
        Settings settings = Settings.builder().put("cluster.name", "my-cluster").build();
        TransportClient client = new PreBuiltTransportClient(settings).addTransportAddress(
                new TransportAddress(InetAddress.getByName("mincdh"), 9300));
        IndicesAdminClient indicesAdminClient = client.admin().indices();
        // 查询所有索引
        GetIndexResponse getIndexResponse = indicesAdminClient.prepareGetIndex().execute().actionGet();
        // 根据id查询索引
        GetIndexResponse blog = indicesAdminClient.prepareGetIndex().setIndices("blog").execute().actionGet();
        for (String index: blog.getIndices()) {
            System.out.println(index);
        }

    }

    /**
     * 根据条件删除索引.
     * @throws IOException e
     */
    public static void deleteIdx() throws IOException {
        Settings settings = Settings.builder().put("cluster.name", "my-cluster").build();
        TransportClient client = new PreBuiltTransportClient(settings).addTransportAddress(
                new TransportAddress(InetAddress.getByName("mincdh"), 9300));
        IndicesAdminClient indicesAdminClient = client.admin().indices();
        AcknowledgedResponse acknowledgedResponse = indicesAdminClient
                .prepareDelete("myindex").execute().actionGet();
        System.out.println(acknowledgedResponse.isAcknowledged());
    }

    /**
     * 新增记录.
     * @throws IOException e
     */
    public static void insertDoc() throws IOException {
        Settings settings = Settings.builder().put("cluster.name", "my-cluster").build();
        TransportClient client = new PreBuiltTransportClient(settings).addTransportAddress(
                new TransportAddress(InetAddress.getByName("mincdh"), 9300));
        String json = "{" 
                + "\"id\":\"1\"," 
                + "\"title\":\"Java设计模式之装饰模式\"," 
                + "\"content\":\"在不必改变原类文件和使用继承的情况下，动态地扩展一个对象的功能。\"," 
                + "\"postdate\":\"2018-02-03 14:38:00\"," 
                + "\"url\":\"csdn.net/79239072\"" 
                + "}";
        IndexResponse indexResponse = client
                .prepareIndex("myindex", "blog", "1").setSource(json, XContentType.JSON).get();
        RestStatus status = indexResponse.status();
        System.out.println(status);
    }

    /**
     * 根据doc的id查看记录.
     * @throws IOException e
     */
    public static void queryDoc() throws IOException {
        TransportClient client = ESUtil.getClient();
        GetResponse getResponse = client.prepareGet("myindex", "blog", "1").get();
        System.out.println(getResponse.isExists());
        System.out.println(getResponse.getIndex());
        System.out.println(getResponse.getId());
        System.out.println(getResponse.getSourceAsString());

    }

    /**
     * 更新指定的doc.
     * @throws IOException e
     * @throws ExecutionException e
     * @throws InterruptedException e
     */
    public static void updateDoc() throws IOException, ExecutionException, InterruptedException {
        TransportClient client = ESUtil.getClient();
        UpdateRequest updateRequest = new UpdateRequest("myindex", "blog", "1").doc(
                XContentFactory.jsonBuilder()
                        .startObject().field("title", "Java设计模式之装饰模式原理").endObject());
        UpdateResponse updateResponse = client.update(updateRequest).get();
        System.out.println(updateResponse.status());
        System.out.println(updateResponse.getVersion());

    }

    /**
     * upsert操作，如果doc存在则更新，如果不存在则insert.
     * @throws IOException e
     * @throws ExecutionException e
     * @throws InterruptedException e
     */
    public static void upsertDoc() throws IOException, ExecutionException, InterruptedException {
        TransportClient client = ESUtil.getClient();
        //如果id为2的记录存在则不会执行下面的内容插入，会执行title的更新
        XContentBuilder xContentBuilder = XContentFactory.jsonBuilder().startObject()
                .field("id", "1")
                .field("title", "装饰模式")
                .field("content", "动态地扩展一个对象的功能")
                .field("postdate", "2018-02-03 14:38:10")
                .field("url", "csdn.net/79239072")
                .endObject();

        UpdateRequest updateRequest = new UpdateRequest("myindex", "blog", "2").doc(
                XContentFactory.jsonBuilder().startObject()
                        .field("title","装饰模式解读")
                        .endObject()
        ).upsert(
                new IndexRequest("myindex", "blog", "2").source(xContentBuilder)
        );
        UpdateResponse updateResponse = client.update(updateRequest).get();
        System.out.println(updateResponse.status());
        System.out.println(updateResponse.getVersion());
    }

    /**
     * 根据条件删除记录.
     */
    public static void deleteDoc() {
        TransportClient client = ESUtil.getClient();
        DeleteResponse deleteResponse = client.prepareDelete("myindex", "blog", "2").get();
        System.out.println(deleteResponse.status());
    }

    /**
     * 根据查询删除记录.
     */
    public static void deleteByQuery() {
        TransportClient client = ESUtil.getClient();
        BulkByScrollResponse response = DeleteByQueryAction.INSTANCE
                .newRequestBuilder(client)
                .filter(QueryBuilders.matchQuery("title", "模式"))
                .source("index1")//设置索引名称
                .get();
        //被删除文档数目
        long deleted = response.getDeleted();
        System.out.println(deleted);
    }

    /**
     * 根据条件查询多条记录.
     * @throws ExecutionException e
     * @throws InterruptedException e
     */
    public static void queryMultiDoc() throws ExecutionException, InterruptedException {
        TransportClient client = ESUtil.getClient();
        MultiGetResponse multiGetResponse =
                client.prepareMultiGet().add("myindex", "blog", "1", "2").get();
        for (MultiGetItemResponse response: multiGetResponse) {
            GetResponse getResponse = response.getResponse();
            if (getResponse != null && getResponse.isExists()) {
                System.out.println(getResponse.getSourceAsString());
            }
        }

    }

    /**
     * 批量插入操作.
     * @throws IOException e
     */
    public static void insertMultiDoc() throws IOException {
        TransportClient client = ESUtil.getClient();
        BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();

        bulkRequestBuilder.add(client.prepareIndex("twitter", "tweet", "1")
            .setSource(XContentFactory.jsonBuilder().startObject()
            .field("user", "kimchy")
            .field("postDate", new Date())
            .field("message", "trying out Elasticsearch")
            .endObject()));

        bulkRequestBuilder.add(client.prepareIndex("twitter", "tweet", "2")
                .setSource(XContentFactory.jsonBuilder().startObject()
                        .field("user", "kimchy")
                        .field("postDate", new Date())
                        .field("message", "another post")
                        .endObject()));
        BulkResponse bulkItemResponses = bulkRequestBuilder.get();
        System.out.println(bulkItemResponses.status());
        if (bulkItemResponses.hasFailures()) {
            System.out.println("存在失败操作");
        }
    }

    /**
     * 批处理器:根据请求的数量或大小自动刷新批量(写入或删除)操作，
     * 也可以在给定的时间段之后自动刷新批量操作.
     */
    public static void batchProcessor() {
        TransportClient client = ESUtil.getClient();
        BulkProcessor bulkProcessor = BulkProcessor.builder(
                client,
                new BulkProcessor.Listener() {

                    public void beforeBulk(long executionId,BulkRequest request) {
                        //设置bulk批处理的预备工作
                        System.out.println("请求数:" + request.numberOfActions());
                    }

                    public void afterBulk(long executionId,BulkRequest request,BulkResponse response) {
                        //设置bulk批处理的善后工作
                        if (!response.hasFailures()) {
                            System.out.println("执行成功！");
                            DocWriteResponse writeResponse = response.getItems()[0].getResponse();

                        } else {
                            System.out.println("执行失败！");
                        }
                    }

                    public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                        //设置bulk批处理的异常处理工作
                        System.out.println(failure);
                    }
                })
                .setBulkActions(1000)//设置提交批处理操作的请求阀值数
                .setBulkSize(new ByteSizeValue(5, ByteSizeUnit.MB))//设置提交批处理操作的请求大小阀值
                .setFlushInterval(TimeValue.timeValueSeconds(5))//设置刷新索引时间间隔
                .setConcurrentRequests(1)//设置并发处理线程个数
                //设置回滚策略，等待时间100ms,retry次数为3次
                .setBackoffPolicy(BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3))
                .build();
        // Add your requests
        bulkProcessor.add(new DeleteRequest("twitter", "tweet", "1"));
        bulkProcessor.add(new DeleteRequest("twitter", "tweet", "2"));

        // 刷新所有请求
        bulkProcessor.flush();
        // 关闭bulkProcessor
        bulkProcessor.close();
        // 刷新索引
        client.admin().indices().prepareRefresh().get();
        // Now you can start searching!
        SearchResponse searchResponse = client.prepareSearch().get();



    }

    /**
     * 数据查询-全文搜索记录.
     */
    public static void searchDocByIndex() {
        TransportClient client = ESUtil.getClient();
        //构造查询对象
        MatchAllQueryBuilder query = QueryBuilders.matchAllQuery();

        SearchResponse searchResponse = client.prepareSearch("blog")
                .setQuery(query)
                .setSize(3)
                .get();
        SearchHits hits = searchResponse.getHits();
        for (SearchHit hit:hits) {
            System.out.println("source:" + hit.getSourceAsString());
            System.out.println("index:" + hit.getIndex());
            System.out.println("type:" + hit.getType());
            System.out.println("id:" + hit.getId());
            //遍历文档的每个字段
            Map<String,Object> map = hit.getSourceAsMap();
            for (String key:map.keySet()) {
                System.out.println(key + "=" + map.get(key));
            }
            System.out.println("--------------------");
        }

    }

    /**
     * match模糊匹配，先对输入的数据进行分词，对分词后的结果进行查询，
     * 文档只要包含match查询条件的一部分就会被返回.
     */
    public static void matchQuery() {
        MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery("content", "搜索计")
                .operator(Operator.AND);//条件之间的逻辑
        SearchHits searchHits = ESUtil.query("blog", queryBuilder, 3);
        for (SearchHit hit:searchHits) {
            System.out.println("source:" + hit.getSourceAsString());
            System.out.println("index:" + hit.getIndex());
            System.out.println("type:" + hit.getType());
            System.out.println("id:" + hit.getId());
            //遍历文档的每个字段
            Map<String,Object> map = hit.getSourceAsMap();
            for (String key:map.keySet()) {
                System.out.println(key + "=" + map.get(key));
            }
            System.out.println("--------------------");
        }
    }

    /**
     * 多字段查询，其中一个字段含有查询条件就返回.
     */
    public static void multiMatchQuery() {
        MultiMatchQueryBuilder queryBuilder = QueryBuilders
                .multiMatchQuery("设计", "content", "title")
                .operator(Operator.AND);
        SearchHits searchHits = ESUtil.query("blog", queryBuilder, 3);
        for (SearchHit hit:searchHits) {
            System.out.println("source:" + hit.getSourceAsString());
            System.out.println("index:" + hit.getIndex());
            System.out.println("type:" + hit.getType());
            System.out.println("id:" + hit.getId());
            //遍历文档的每个字段
            Map<String,Object> map = hit.getSourceAsMap();
            for (String key:map.keySet()) {
                System.out.println(key + "=" + map.get(key));
            }
            System.out.println("--------------------");
        }
    }

    /**
     * 词项查询，输入的值不会被分词器分词.
     */
    public static void termQuery() {
        TermQueryBuilder queryBuilder = QueryBuilders.termQuery("content", "设计");
        SearchHits searchHits = ESUtil.query("blog", queryBuilder, 3);
        for (SearchHit hit:searchHits) {
            System.out.println("source:" + hit.getSourceAsString());
            System.out.println("index:" + hit.getIndex());
            System.out.println("type:" + hit.getType());
            System.out.println("id:" + hit.getId());
            //遍历文档的每个字段
            Map<String,Object> map = hit.getSourceAsMap();
            for (String key:map.keySet()) {
                System.out.println(key + "=" + map.get(key));
            }
            System.out.println("--------------------");
        }

    }

    /**
     * 同一个字段的多词项查询，输入的词不会被分词.
     */
    public static void termsQuery() {
        TermsQueryBuilder queryBuilder = QueryBuilders.termsQuery("content", "一", "人");
        SearchHits searchHits = ESUtil.query("blog", queryBuilder, 3);
        for (SearchHit hit:searchHits) {
            System.out.println("source:" + hit.getSourceAsString());
            System.out.println("index:" + hit.getIndex());
            System.out.println("type:" + hit.getType());
            System.out.println("id:" + hit.getId());
            //遍历文档的每个字段
            Map<String,Object> map = hit.getSourceAsMap();
            for (String key:map.keySet()) {
                System.out.println(key + "=" + map.get(key));
            }
            System.out.println("--------------------");
        }
    }

    /**
     * 范围检索.
     */
    public static void rangeQuery() {

        RangeQueryBuilder queryBuilder =
                QueryBuilders.rangeQuery("posttime").from("2018-01-01").to("2018-09-09").format("yyyy-MM-dd");
        SearchHits searchHits = ESUtil.query("blog", queryBuilder, 3);
        for (SearchHit hit:searchHits) {
            System.out.println("source:" + hit.getSourceAsString());
            System.out.println("index:" + hit.getIndex());
            System.out.println("type:" + hit.getType());
            System.out.println("id:" + hit.getId());
            //遍历文档的每个字段
            Map<String,Object> map = hit.getSourceAsMap();
            for (String key:map.keySet()) {
                System.out.println(key + "=" + map.get(key));
            }
            System.out.println("--------------------");
        }
    }

    /**
     * 前缀匹配查询，不会对词项分词.
     */
    public static void perfixQuery() {
        PrefixQueryBuilder queryBuilder = QueryBuilders.prefixQuery("content", "是一个");
        SearchHits searchHits = ESUtil.query("blog", queryBuilder, 3);
        for (SearchHit hit:searchHits) {
            System.out.println("source:" + hit.getSourceAsString());
            System.out.println("index:" + hit.getIndex());
            System.out.println("type:" + hit.getType());
            System.out.println("id:" + hit.getId());
            //遍历文档的每个字段
            Map<String,Object> map = hit.getSourceAsMap();
            for (String key:map.keySet()) {
                System.out.println(key + "=" + map.get(key));
            }
            System.out.println("--------------------");
        }
    }

    /**
     * 正则匹配搜索，不会对词项进行分词.
     */
    public static void regQuery() {
        RegexpQueryBuilder queryBuilder = QueryBuilders.regexpQuery("content", "设计.*");
        SearchHits searchHits = ESUtil.query("blog", queryBuilder, 3);
        for (SearchHit hit:searchHits) {
            System.out.println("source:" + hit.getSourceAsString());
            System.out.println("index:" + hit.getIndex());
            System.out.println("type:" + hit.getType());
            System.out.println("id:" + hit.getId());
            //遍历文档的每个字段
            Map<String,Object> map = hit.getSourceAsMap();
            for (String key:map.keySet()) {
                System.out.println(key + "=" + map.get(key));
            }
            System.out.println("--------------------");
        }
    }

    /**
     * 根据文档的id进行查询.
     */
    public static void idsQuery() {
        IdsQueryBuilder queryBuilder = QueryBuilders.idsQuery().addIds("1", "3");
        SearchHits searchHits = ESUtil.query("blog", queryBuilder, 3);
        for (SearchHit hit:searchHits) {
            System.out.println("source:" + hit.getSourceAsString());
            System.out.println("index:" + hit.getIndex());
            System.out.println("type:" + hit.getType());
            System.out.println("id:" + hit.getId());
            //遍历文档的每个字段
            Map<String,Object> map = hit.getSourceAsMap();
            for (String key:map.keySet()) {
                System.out.println(key + "=" + map.get(key));
            }
            System.out.println("--------------------");
        }
    }

    /**
     * 获取指定字段的最大值.
     */
    public static void getMax() {
        double max = ESUtil.max("id", "blog");
        System.out.println(max);
    }

    /**
     * 获取指定字段的最小值.
     */
    public static void getMin() {
        double min = ESUtil.min("id", "blog");
        System.out.println(min);
    }

    /**
     * 获取指定字段的和.
     */
    public static void getSum() {
        double sum = ESUtil.sum("id", "blog");
        System.out.println(sum);
    }

    /**
     * 获取指定字段的平均值.
     */
    public static void getAvg() {
        double avg = ESUtil.avg("id", "blog");
        System.out.println(avg);
    }

    /**
     * 获取指定字段的基本统计.
     */
    public static void getStats() {
        Stats stats = ESUtil.stats("id", "blog");
        System.out.println(stats);
    }

    /**
     * 获取指定字段的高级统计.
     */
    public static void getExtendedStats() {
        ExtendedStats extendedStats = ESUtil.extendedStats("id", "blog");
        System.out.println(extendedStats);
    }


    /**
     * 获取指定字段的基数统计.
     */
    public static void getCard() {
        double cardinality = ESUtil.cardinality("id", "blog");
        System.out.println(cardinality);
    }

    /**
     * 获取指定字段的百分位统计.
     */
    public static void getPerc() {
        Percentiles percentiles = ESUtil.percentiles("id", "blog");
        System.out.println(percentiles);
    }

    /**
     * 获取指定字段的记录数量.
     */
    public static void getCount() {
        double valueCount = ESUtil.valueCount("id", "blog");
        System.out.println(valueCount);
    }

    /**
     * 根据指定分组聚合.
     */
    public static void getTerms() {
        Terms terms = ESUtil.termsAgg("blog", "id");
        for (Terms.Bucket entry: terms.getBuckets()) {
            System.out.println(entry.getKey() + ":" + entry.getDocCount());
        }
    }

    /**
     * 根据指定的字段进行过滤聚合.
     */
    public static void getFilter() {
        Filter filter = ESUtil.filter("id", "1", "blog");
        System.out.println(filter.getDocCount());
    }

    /**
     * 根据指定的多个字段和值进行过滤聚合.
     */
    public static void getFilters() {
        Filters filters = ESUtil.filters("blog", "id", "1", "id", "2");
        for (Filters.Bucket entry:filters.getBuckets()) {
            System.out.println(entry.getKey() + ":" + entry.getDocCount());
        }
    }

    /**
     * 根据日期范围聚合.
     */
    public static void getRange() {
        Range range = ESUtil.range("blog", "posttime", "2018-01-10", "2018-01-01");
        for (Range.Bucket entry:range.getBuckets()) {
            System.out.println(entry.getKey() + ":" + entry.getDocCount());
        }
    }

    /**
     * 获取missing的聚合.
     */
    public static void getMissing() {
        Missing missing = ESUtil.missing("blog", "posttime");
        System.out.println(missing.getDocCount());
    }

    /**
     * 获取集群健康状况.
     */
    public static void getClusterHealth() {
        ClusterAdminClient clusterAdminClient = ESUtil.getClusterAdminClient();
        ClusterHealthResponse clusterHealthResponse = clusterAdminClient.prepareHealth().get();
        String clusterName = clusterHealthResponse.getClusterName();
        System.out.println("clusterName=" + clusterName);
        int numberOfDataNodes = clusterHealthResponse.getNumberOfDataNodes();
        System.out.println("numberOfDataNodes=" + numberOfDataNodes);
        int numberOfNodes = clusterHealthResponse.getNumberOfNodes();
        System.out.println("numberOfNodes=" + numberOfNodes);

        for (ClusterIndexHealth health : clusterHealthResponse.getIndices().values()) {
            String index = health.getIndex();
            int numberOfShards = health.getNumberOfShards();
            int numberOfReplicas = health.getNumberOfReplicas();
            System.out.printf("index=%s,numberOfShards=%d,numberOfReplicas=%d\n",index,numberOfShards,numberOfReplicas);
            ClusterHealthStatus status = health.getStatus();
            System.out.println(status.toString());
        }
    }

    /**
     * 获取指定索引的状态.
     */
    public static void getIndexHealth() {

        ClusterHealthResponse response =
                ESUtil.getClusterAdminClient().prepareHealth("blog")
                        .setWaitForGreenStatus().setTimeout(TimeValue.timeValueSeconds(3)).get();
        ClusterHealthStatus healthStatus = response.getIndices().get("blog").getStatus();
        if (! healthStatus.equals(ClusterHealthStatus.GREEN)) {
            throw new RuntimeException("Index is in" + healthStatus + "state");
        }
    }

    /**
     * .
     * @param args args
     * @throws IOException e
     * @throws ExecutionException e
     * @throws InterruptedException e
     */
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        //isExistIdx();
        //createIdx();
        //queryIdx();
        //deleteIdx();
        //insertDoc();
        //queryDoc();
        //updateDoc();
        //upsertDoc();
        //deleteDoc();
        //queryMultiDoc();
        //insertMultiDoc();
        //batchProcessor();
        //searchDocByIndex();
        //matchQuery();
        //multiMatchQuery();
        //termQuery();
        termsQuery();
        rangeQuery();
        //perfixQuery();
        //regQuery();
        //idsQuery();
        //getMax();
        //getMin();
        //getSum();
        //getAvg();
        //getStats();
        //getExtendedStats();
        //getCard();
        //getPerc();
        //getCount();
        //getTerms();
        //getFilter();
        //getFilters();
        //getRange();
        //getMissing();
        //getClusterHealth();
        //getIndexHealth();
    }


}
