package com.thecoderstv.crpytorankingservice.service;

import com.thecoderstv.crpytorankingservice.model.*;
import com.thecoderstv.crpytorankingservice.utils.HttpUtils;
import io.github.dengliming.redismodule.redisjson.RedisJSON;
import io.github.dengliming.redismodule.redisjson.args.GetArgs;
import io.github.dengliming.redismodule.redisjson.args.SetArgs;
import io.github.dengliming.redismodule.redisjson.utils.GsonUtils;
import io.github.dengliming.redismodule.redistimeseries.DuplicatePolicy;
import io.github.dengliming.redismodule.redistimeseries.RedisTimeSeries;
import io.github.dengliming.redismodule.redistimeseries.Sample;
import io.github.dengliming.redismodule.redistimeseries.TimeSeriesOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CoinsDataService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisJSON redisJSON;

    @Autowired
    private RedisTimeSeries redisTimeSeries;

    private static final String GET_COINS_URL = "https://coinranking1.p.rapidapi.com/coins?referenceCurrencyUuid=yhjMzLPhuIDl&timePeriod=24h&tiers=1&orderBy=marketCap&orderDirection=desc&limit=50&offset=0";
    private static final String REDIS_KEY_COINS = "coins";
    private static final String GET_COIN_HISTORY_API = "https://coinranking1.p.rapidapi.com/coin/";
    private static final String COIN_HISTORY_TIME_PERIOD_PARAM = "/history?timePeriod=";
    private static final List<String> timePeriods = List.of("24h", "7d", "30d", "3m", "1y", "3y", "5y");


    // fetching all coins
    public void fetchCoinsData() {
        log.info("Fetching coins data...");
        ResponseEntity<Coins> responseEntity = restTemplate
                .exchange(GET_COINS_URL, HttpMethod.GET, HttpUtils.getHttpEntity(), Coins.class);

        storeCoinsToRedisJSON(responseEntity.getBody());
    }

    // fetch coin history
    public void fetchCoinHistory() {
        log.info("Fetching coin history...");
        List<CoinInfo> allCoins = getAllCoinsFromRedisJSON();
        allCoins.forEach(coinInfo -> {
            timePeriods.forEach(s -> {
                fetchCoinHistoryForTimePeriod(coinInfo, s);
            });
        });
    }


    // ----------------------------------------------------------------------------------------------------

    private void storeCoinsToRedisJSON(Coins coins) {
        // entire data stored to redis json
        redisJSON.set(REDIS_KEY_COINS, SetArgs.Builder.create(".", GsonUtils.toJson(coins)));
    }


    private List<CoinInfo> getAllCoinsFromRedisJSON() {
        CoinData coinData = redisJSON.get(REDIS_KEY_COINS, CoinData.class, new GetArgs().path(".data").indent("\t").newLine("\n").space(" "));
        return coinData.getCoins();
    }

    private void fetchCoinHistoryForTimePeriod(CoinInfo coinInfo, String timePeriod) {
        log.info("Fetching coin history for time period: {}", timePeriod);
        String url = GET_COIN_HISTORY_API + coinInfo.getUuid() + COIN_HISTORY_TIME_PERIOD_PARAM + timePeriod;
        ResponseEntity<CoinPriceHistory> coinPriceHistoryResponseEntity = restTemplate.exchange(url, HttpMethod.GET, HttpUtils.getHttpEntity(), CoinPriceHistory.class);
        log.info("Coin history fetched for time period: {}", timePeriod);
        storeCoinHistoryToRedisTimeSeries(coinPriceHistoryResponseEntity.getBody(), coinInfo.getSymbol(), timePeriod);
    }

    private void storeCoinHistoryToRedisTimeSeries(CoinPriceHistory coinPriceHistory, String symbol, String timePeriod) {
        log.info("Storing coin history to redis time series for symbol: {} and time period: {}", symbol, timePeriod);
        List<CoinPriceHistoryExchangeRate> coinExchangeRate = coinPriceHistory.getData().getHistory();
        // Symbol  : timePeriod
        // BTCUSD : 24h
        // BTCUSD : 7d
        // ETH : 30d
        coinExchangeRate
                .stream()
                .filter(ch -> ch.getPrice() != null && ch.getTimestamp() != null)
                .forEach(ch -> {
                    redisTimeSeries.add(new Sample(symbol + ":" + timePeriod,
                                    Sample.Value.of(Long.valueOf(ch.getTimestamp()), Double.valueOf(ch.getPrice()))),
                            new TimeSeriesOptions()
                                    .unCompressed()
                                    .duplicatePolicy(DuplicatePolicy.LAST));
                });
    }

    public List<CoinInfo> fetchAllCoinsFromRedisJSON() {
        return getAllCoinsFromRedisJSON();
    }


    public List<Sample.Value> fetchCoinHistoryPerTimePeriodFromRedisTS(String symbol, String timePeriod) {
        Map<String,Object> tsInfo = fetchTSInfoForSymbol(symbol,timePeriod);
        Long firstTimeStamp = Long.valueOf(tsInfo.get("firstTimestamp").toString());
        Long lastTimeStamp = Long.valueOf(tsInfo.get("lastTimestamp").toString());
        List<Sample.Value> coinsTSData = fetTSDataForCoin(symbol,timePeriod,firstTimeStamp,lastTimeStamp);
        return coinsTSData;
    }

    private List<Sample.Value> fetTSDataForCoin(String symbol, String timePeriod, Long firstTimeStamp, Long lastTimeStamp) {
        String key = symbol + ":" + timePeriod;
        return redisTimeSeries.range(key,firstTimeStamp,lastTimeStamp);
    }

    private Map<String, Object> fetchTSInfoForSymbol(String symbol, String timePeriod) {
        return redisTimeSeries.info(symbol + ":" + timePeriod);
    }
}
