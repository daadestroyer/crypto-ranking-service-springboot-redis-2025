package com.thecoderstv.crpytorankingservice.service;

import com.thecoderstv.crpytorankingservice.model.Coins;
import com.thecoderstv.crpytorankingservice.utils.HttpUtils;
import io.github.dengliming.redismodule.redisjson.RedisJSON;
import io.github.dengliming.redismodule.redisjson.args.SetArgs;
import io.github.dengliming.redismodule.redisjson.utils.GsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class CoinsDataService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisJSON redisJSON;

    private static final String GET_COINS_URL = "https://coinranking1.p.rapidapi.com/coins?referenceCurrencyUuid=yhjMzLPhuIDl&timePeriod=24h&tiers=1&orderBy=marketCap&orderDirection=desc&limit=50&offset=0";
    private static final String REDIS_KEY_COINS = "coins";

    // fetching all coins
    public void fetchCoinsData() {
        log.info("Fetching coins data...");
        ResponseEntity<Coins> responseEntity = restTemplate
                .exchange(GET_COINS_URL, HttpMethod.GET, HttpUtils.getHttpEntity(), Coins.class);

        storeCoinsToRedisJSON(responseEntity.getBody());
    }
    private void storeCoinsToRedisJSON(Coins coins) {
        // entire data stored to redis json
        redisJSON.set(REDIS_KEY_COINS, SetArgs.Builder.create(".", GsonUtils.toJson(coins)));
    }
}
