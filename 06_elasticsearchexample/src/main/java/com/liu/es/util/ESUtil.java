package com.liu.es.util;

import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.Filters;
import org.elasticsearch.search.aggregations.bucket.filter.FiltersAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.FiltersAggregator;
import org.elasticsearch.search.aggregations.bucket.missing.Missing;
import org.elasticsearch.search.aggregations.bucket.missing.MissingAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.range.DateRangeAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.metrics.avg.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.Cardinality;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.elasticsearch.search.aggregations.metrics.max.MaxAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.min.Min;
import org.elasticsearch.search.aggregations.metrics.min.MinAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.percentiles.Percentiles;
import org.elasticsearch.search.aggregations.metrics.percentiles.PercentilesAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.stats.Stats;
import org.elasticsearch.search.aggregations.metrics.stats.StatsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.stats.extended.ExtendedStats;
import org.elasticsearch.search.aggregations.metrics.stats.extended.ExtendedStatsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountAggregationBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;

/**
 * elastic工具类.
 * @Auther: liudongfei
 * @Date: 2019/7/20 17:09
 * @Description:
 */
public class   ESUtil {
    private static final String CLUSTER_NAME = "my-cluster";
    private static final String HOSTNAME = "mincdh";
    private static final int PORT = 9300;
    private static Settings settings = Settings.builder().put("cluster.name", CLUSTER_NAME).build();
    private static TransportClient client;

    static {
        // TODO 加载配置
    }

    /**
     * 获取ES客户端链接.
     * @return
     */
    public static TransportClient getClient() {
        if (client == null) {
            synchronized (TransportClient.class) {
                try {
                    client = new PreBuiltTransportClient(settings).addTransportAddress(
                            new TransportAddress(InetAddress.getByName(HOSTNAME), PORT)
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return client;
    }

    /**
     * 获取索引管理的IndicesAdminClient.
     * @return
     */
    public static IndicesAdminClient getIndicesAdminClient() {
        return getClient().admin().indices();
    }

    public static ClusterAdminClient getClusterAdminClient() {
        return getClient().admin().cluster();
    }

    /**
     * 检查索引是否存在.
     * @param indexName 索引名称
     * @return
     */
    public static boolean isIndexExists(String indexName) {
        IndicesExistsResponse indicesExistsResponse = getIndicesAdminClient().prepareExists(indexName).get();
        return indicesExistsResponse.isExists();
    }

    /**
     * 创建索引.
     * @param indexName 索引名称
     * @return
     */
    public static boolean createIndex(String indexName) {
        CreateIndexResponse createIndexResponse = getIndicesAdminClient().prepareCreate(indexName).get();
        return createIndexResponse.isAcknowledged();
    }

    /**
     * 创建索引.
     * @param indexName 索引名称
     * @param shards 分片数
     * @param replicas 副本数
     * @return
     */
    public static boolean createIndex(String indexName, int shards, int replicas) {
        Settings settings = Settings.builder()
                .put("index.number_of_shards", shards)
                .put("index.number_of_replicas", replicas)
                .build();
        CreateIndexResponse createIndexResponse = getIndicesAdminClient()
                .prepareCreate(indexName).setSettings(settings).execute().actionGet();
        return createIndexResponse.isAcknowledged();
    }

    /**
     * 为index索引设置mapping.
     * @param indexName 索引名
     * @param typeName 类型名
     * @param mapping 映射名
     * @return
     */
    public static boolean setMapping(String indexName, String typeName, String mapping) {
        AcknowledgedResponse acknowledgedResponse = getIndicesAdminClient()
                .preparePutMapping(indexName).setType(typeName).setSource(mapping, XContentType.JSON).get();
        return acknowledgedResponse.isAcknowledged();
    }

    /**
     * 删除索引.
     * @param indexName 索引名称
     * @return
     */
    public static boolean deleteIndex(String indexName) {
        AcknowledgedResponse acknowledgedResponse = getIndicesAdminClient()
                .prepareDelete(indexName).execute().actionGet();
        return acknowledgedResponse.isAcknowledged();
    }

    /**
     * 查询计划.
     * @param queryBuilder query
     */
    public static SearchHits query(String index, QueryBuilder queryBuilder, int querySize) {

        SearchResponse searchResponse =
                getClient()
                        .prepareSearch(index)
                        .setQuery(queryBuilder)
                        .addSort(SortBuilders.scoreSort().order(SortOrder.DESC))//根据评分倒叙
                        .addSort("id", SortOrder.DESC)//根据字段排序
                        .setFrom(0)
                        .setSize(querySize)
                        .execute()
                        .actionGet();
        return searchResponse.getHits();
    }

    /**
     * 获取指定字段的最大值.
     * @param field 字段
     * @param index 索引
     * @return
     */
    public static double max(String field, String index) {
        MaxAggregationBuilder aggregationBuilder = AggregationBuilders.max("aggMax").field(field);
        SearchResponse searchResponse = getClient().prepareSearch(index).addAggregation(aggregationBuilder).get();
        Max aggMax = searchResponse.getAggregations().get("aggMax");
        return aggMax.getValue();
    }

    /**
     * 获取指定字段的最小值.
     * @param field 字段
     * @param index 索引
     * @return
     */
    public static double min(String field, String index) {
        MinAggregationBuilder aggregationBuilder = AggregationBuilders.min("aggMin").field(field);
        SearchResponse searchResponse = getClient().prepareSearch(index).addAggregation(aggregationBuilder).get();
        Min aggMax = searchResponse.getAggregations().get("aggMin");
        return aggMax.getValue();
    }

    /**
     * 获取指定字段的和.
     * @param field 字段
     * @param index 索引
     * @return
     */
    public static double sum(String field, String index) {
        SumAggregationBuilder aggregationBuilder = AggregationBuilders.sum("aggSum").field(field);
        SearchResponse searchResponse = getClient().prepareSearch(index).addAggregation(aggregationBuilder).get();
        Sum aggSum = searchResponse.getAggregations().get("aggSum");
        return aggSum.getValue();
    }

    /**
     * 获取指定字段的平均值.
     * @param field 字段
     * @param index 索引
     * @return
     */
    public static double avg(String field, String index) {
        AvgAggregationBuilder aggregationBuilder = AggregationBuilders.avg("aggAvg").field(field);
        SearchResponse searchResponse = getClient().prepareSearch(index).addAggregation(aggregationBuilder).get();
        Avg aggAvg = searchResponse.getAggregations().get("aggAvg");
        return aggAvg.getValue();
    }

    /**
     * 获取指定字段的基本统计.
     * @param field 字段
     * @param index 索引
     * @return
     */
    public static Stats stats(String field, String index) {
        StatsAggregationBuilder aggregationBuilder = AggregationBuilders.stats("aggStats").field(field);
        SearchResponse searchResponse = getClient().prepareSearch(index).addAggregation(aggregationBuilder).get();
        Stats aggStats = searchResponse.getAggregations().get("aggStats");
        return aggStats;
    }

    /**
     * 获取指定字段的高级统计.
     * @param field 字段
     * @param index 索引
     * @return
     */
    public static ExtendedStats extendedStats(String field, String index) {
        ExtendedStatsAggregationBuilder aggregationBuilder =
                AggregationBuilders.extendedStats("aggExtendedStats").field(field);
        SearchResponse searchResponse = getClient().prepareSearch(index).addAggregation(aggregationBuilder).get();
        return searchResponse.getAggregations().get("aggExtendedStats");
    }

    /**
     * 获取指定字段的基数统计.
     * @param field 字段
     * @param index 索引
     * @return
     */
    public static double cardinality(String field, String index) {
        CardinalityAggregationBuilder aggregationBuilder =
                AggregationBuilders.cardinality("aggCard").field(field);
        SearchResponse searchResponse = getClient().prepareSearch(index).addAggregation(aggregationBuilder).get();
        Cardinality aggCard = searchResponse.getAggregations().get("aggCard");
        return aggCard.getValue();
    }

    /**
     * 获取指定字段的百分位统计.
     * @param field 字段
     * @param index 索引
     * @return
     */
    public static Percentiles percentiles(String field, String index) {
        PercentilesAggregationBuilder aggregationBuilder =
                AggregationBuilders.percentiles("aggPerc").field(field);
        SearchResponse searchResponse = getClient().prepareSearch(index).addAggregation(aggregationBuilder).get();
        return searchResponse.getAggregations().get("aggPerc");
    }

    /**
     * 获取文档数量大小.
     * @param field 字段
     * @param index 索引
     * @return
     */
    public static double valueCount(String field, String index) {
        ValueCountAggregationBuilder aggregationBuilder = AggregationBuilders.count("aggCount").field(field);
        SearchResponse searchResponse = getClient().prepareSearch(index).addAggregation(aggregationBuilder).get();
        ValueCount aggCount = searchResponse.getAggregations().get("aggCount");
        return aggCount.getValue();
    }


    /**
     * 根据指定字段进行桶聚合.
     * @param index index
     * @param field field
     * @return
     */
    public static Terms termsAgg(String index, String field) {
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("terms").field(field);
        SearchResponse searchResponse =
                getClient().prepareSearch(index).addAggregation(aggregationBuilder).execute().actionGet();
        Terms terms = searchResponse.getAggregations().get("terms");
        return terms;
    }

    /**
     * 过滤器聚合.
     * @param field 字段
     * @param key 健
     * @param index 索引
     * @return
     */
    public static Filter filter(String field, String key, String index) {
        TermsQueryBuilder queryBuilder = QueryBuilders.termsQuery(field, key);
        FilterAggregationBuilder aggregationBuilder = AggregationBuilders.filter("filter", queryBuilder);

        SearchResponse searchResponse =
                getClient().prepareSearch(index).addAggregation(aggregationBuilder).execute().actionGet();
        return searchResponse.getAggregations().get("filter");
    }

    /**
     * 多过滤器聚合.
     * @param index 索引
     * @param field1 字段1
     * @param key1 健1
     * @param field2 字段2
     * @param key2 健2
     * @return
     */
    public static Filters filters(String index, String field1, String key1, String field2, String key2) {

        FiltersAggregationBuilder aggregationBuilder = AggregationBuilders.filters("filters",
                new FiltersAggregator.KeyedFilter(key1, QueryBuilders.termQuery(field1, key1)),
                new FiltersAggregator.KeyedFilter(key2, QueryBuilders.termQuery(field2, key2))
        );

        SearchResponse searchResponse =
                getClient().prepareSearch(index).addAggregation(aggregationBuilder).execute().actionGet();
        return searchResponse.getAggregations().get("filters");
    }

    /**
     * 日期区间聚合.
     * @param index 索引
     * @param field 字段
     * @param from 开始
     * @param to 结束
     * @return
     */
    public static Range range(String index, String field, String from, String to) {
        DateRangeAggregationBuilder aggregationBuilder = AggregationBuilders.dateRange("dateRange")
                .field(field)
                .format("yyyy-MM-dd")
                .addUnboundedFrom(from)
                .addUnboundedTo(to);

        SearchResponse searchResponse =
                getClient().prepareSearch(index).addAggregation(aggregationBuilder).execute().actionGet();
        return searchResponse.getAggregations().get("dateRange");
    }

    /**
     * missing 聚合.
     * @param index 索引
     * @param field 字段
     * @return
     */
    public static Missing missing(String index, String field) {
        MissingAggregationBuilder aggregationBuilder = AggregationBuilders.missing("missing").field(field);

        SearchResponse searchResponse =
                getClient().prepareSearch(index).addAggregation(aggregationBuilder).execute().actionGet();

        return searchResponse.getAggregations().get("missing");
    }

}
