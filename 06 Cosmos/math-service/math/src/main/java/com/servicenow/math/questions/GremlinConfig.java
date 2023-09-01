package com.servicenow.math.questions;

import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Cluster;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// now lets import GraphSONMessageSerializerV2

import org.apache.tinkerpop.gremlin.util.ser.GraphSONMessageSerializerV2;

import java.util.HashMap;
import java.util.Map;




@Configuration
public class GremlinConfig {

    @Value("${spring.datasource.url}")
    private String cosmosDbUrl;

    @Value("${spring.datasource.username}")
    private String cosmosDbAccount;

    @Value("${spring.datasource.password}")
    private String cosmosDbKey;


    @Bean
    public Client gremlinClient() {
        Map<String, Object> serializerConfig = new HashMap<>();
        serializerConfig.put("serializeResultToString", true);

        GraphSONMessageSerializerV2 serializer = new GraphSONMessageSerializerV2();
        serializer.configure(serializerConfig, null);

        Cluster cluster = Cluster.build(cosmosDbUrl)
                .credentials(cosmosDbAccount, cosmosDbKey)
                .serializer(serializer)
                .port(443)
                .create();
        return cluster.connect().alias("g");
    }
}
