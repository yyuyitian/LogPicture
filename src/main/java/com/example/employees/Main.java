/* Copyright � 2015 Oracle and/or its affiliates. All rights reserved. */
package com.example.employees;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import javax.servlet.ServletException;

public class Main {
    
    public static final Optional<String> PORT = Optional.ofNullable(System.getenv("PORT"));
    public static final Optional<String> HOSTNAME = Optional.ofNullable(System.getenv("HOSTNAME"));

    public static void main(String[] args) throws LifecycleException, ServletException {
        // 启动 tomcat
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(Integer.getInteger("port", 8080));
        tomcat.getConnector();
        // 创建 WebApp
        Context context = tomcat.addWebapp("", new File("src/main/webapp").getAbsolutePath());
        WebResourceRoot resources = new StandardRoot(context);
        resources.addPreResources(
                new DirResourceSet(resources, "/WEB-INF/classes",
                        new File("target/classes").getAbsolutePath(), "/"));
        context.setResources(resources);

        tomcat.start();
        tomcat.getServer().await();
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http")));

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("coachingclass");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());

        searchSourceBuilder.aggregation(AggregationBuilders.sum("sum").field("fees"));
        searchSourceBuilder.aggregation(AggregationBuilders.avg("avg").field("fees"));
        searchSourceBuilder.aggregation(AggregationBuilders.min("min").field("fees"));
        searchSourceBuilder.aggregation(AggregationBuilders.max("max").field("fees"));
        searchSourceBuilder.aggregation(AggregationBuilders.cardinality("cardinality").field("fees"));
        searchSourceBuilder.aggregation(AggregationBuilders.count("count").field("fees"));

        searchRequest.source(searchSourceBuilder);
        Map<String, Object> map = null;

        try {
            SearchResponse searchResponse = null;
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            if (searchResponse.getHits().getTotalHits().value > 0) {
                SearchHit[] searchHit = searchResponse.getHits().getHits();
                for (SearchHit hit : searchHit) {
                    map = hit.getSourceAsMap();
                    System.out.println("Index data:" + Arrays.toString(map.entrySet().toArray()));

                }
            }

            Sum sum = searchResponse.getAggregations().get("sum");
            double result = sum.getValue();
            System.out.println("aggs Sum: " + result);
            Avg aggAvg = searchResponse.getAggregations().get("avg");
            double valueAvg = aggAvg.getValue();
            System.out.println("aggs Avg::" + valueAvg);
            Min aggMin = searchResponse.getAggregations().get("min");
            double minOutput = aggMin.getValue();
            System.out.println("aggs Min::" + minOutput);
            Max aggMax = searchResponse.getAggregations().get("max");
            double maxOutput = aggMax.getValue();
            System.out.println("aggs Max::" + maxOutput);
            Cardinality aggCadinality = searchResponse.getAggregations().get("cardinality");
            long valueCadinality = aggCadinality.getValue();
            System.out.println("aggs Cadinality::" + valueCadinality);
            ValueCount aggCount = searchResponse.getAggregations().get("count");
            long valueCount = aggCount.getValue();
            System.out.println("aggs Count::" + valueCount);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}