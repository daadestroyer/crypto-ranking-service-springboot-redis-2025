package com.thecoderstv.crpytorankingservice.controller;

import com.thecoderstv.crpytorankingservice.model.CoinInfo;
import com.thecoderstv.crpytorankingservice.model.HistoryData;
import com.thecoderstv.crpytorankingservice.service.CoinsDataService;
import com.thecoderstv.crpytorankingservice.utils.Utility;
import io.github.dengliming.redismodule.redistimeseries.Sample;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/coins")
@Slf4j
public class CoinsRankingController {
    @Autowired
    private CoinsDataService coinsDataService;

    // http://localhost:1001/api/v1/coins
    @GetMapping
    public ResponseEntity<?> fetchAllCoins() {
        List<CoinInfo> coinInfos = coinsDataService.fetchAllCoinsFromRedisJSON();
        return new ResponseEntity<>(coinInfos, HttpStatus.OK);
    }
    // http://localhost:1001/api/v1/coins/history/BTC/24h
    @GetMapping("/history/{symbol}/{timePeriod}")
    public ResponseEntity<?> fetchCoinHistory(@PathVariable String symbol, @PathVariable String timePeriod) {
        List<Sample.Value> coinTSData = coinsDataService.fetchCoinHistoryPerTimePeriodFromRedisTS(symbol, timePeriod);
        List<HistoryData> coinHistory =
                coinTSData
                        .stream()
                        .map(value -> new HistoryData(
                                Utility.convertUnixTimeToDate(value.getTimestamp()),
                                Utility.round(value.getValue(), 2)
                        )).collect(Collectors.toList());
        return new ResponseEntity<>(coinHistory, HttpStatus.OK);
    }
}
