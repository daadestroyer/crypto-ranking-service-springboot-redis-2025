package com.thecoderstv.crpytorankingservice.config;

import io.github.dengliming.redismodule.redisjson.RedisJSON;
import io.github.dengliming.redismodule.redisjson.client.RedisJSONClient;
import io.github.dengliming.redismodule.redistimeseries.RedisTimeSeries;
import io.github.dengliming.redismodule.redistimeseries.client.RedisTimeSeriesClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {
    private static final String REDIS_HOST = "redis://127.0.0.1:6379";

    @Bean
    public Config config() {
        Config config = new Config();
        config
                .useSingleServer()
                .setAddress(REDIS_HOST); // for local we don't have password so we are not using
        // if we are using redis cloud we can give password
        return config;
    }
    // We need object of RedisTimeSeries and RedisJSON
    public RedisTimeSeriesClient redisTimeSeriesClient(Config config) {
        return new RedisTimeSeriesClient(config);
    }
    public RedisTimeSeries redisTimeSeries(RedisTimeSeriesClient redisTimeSeriesClient) {
        return redisTimeSeriesClient.getRedisTimeSeries();
    }
    public RedisJSONClient redisJSONClient(Config config) {
        return new RedisJSONClient(config);
    }
    public RedisJSON redisJSON(RedisJSONClient redisJSONClient) {
        return redisJSONClient.getRedisJSON();
    }
}


