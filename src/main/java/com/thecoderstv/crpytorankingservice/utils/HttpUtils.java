package com.thecoderstv.crpytorankingservice.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Collections;

public class HttpUtils {
    private static String API_HOST = "coinranking1.p.rapidapi.com";
    private static String API_KEY = "827bfd0966msh16bc82fac8a25b5p1c5a44jsna0bb5791655a";

    public static HttpEntity<String> getHttpEntity() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        httpHeaders.set("x-rapidapi-host", API_HOST);
        httpHeaders.set("x-rapidapi-key", API_KEY);
        return new HttpEntity<>(null, httpHeaders);
    }
}
