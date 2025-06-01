package com.thecoderstv.crpytorankingservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CoinData {
    private CoinStats stats;
    private List<CoinInfo> coins = new ArrayList<>();
}
