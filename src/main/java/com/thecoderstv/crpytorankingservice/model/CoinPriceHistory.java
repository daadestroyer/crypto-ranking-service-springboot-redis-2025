package com.thecoderstv.crpytorankingservice.model;


import lombok.Data;

@Data
public class CoinPriceHistory {
    private String status;
    private CoinPriceHistoryData data;
}