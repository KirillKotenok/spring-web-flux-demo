package com.example.springwebfluxdemo.service;

import com.example.springwebfluxdemo.domain.Picture;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

@Service
public class ReactiveNasaService {

    public Mono<byte[]> getLargestPic(String nasaUrl) {
        return WebClient.builder().build()
                .method(HttpMethod.GET)
                .uri(URI.create(nasaUrl))
                .exchangeToMono(resp -> resp.bodyToMono(JsonNode.class))
                .flatMapIterable(jsonNode -> jsonNode.findValuesAsText("img_src"))
                .flatMap(picUrl -> getRedirectLocation(picUrl)
                        .flatMap(this::createPicture))
                .reduce((p1, p2) -> p1.getSize() > p2.getSize() ? p1 : p2)
                .map(Picture::getUrl)
                .flatMap(url -> WebClient.create(url)
                        .mutate()
                        .codecs(config -> config.defaultCodecs().maxInMemorySize(10_000_000))
                        .build()
                        .get()
                        .retrieve()
                        .bodyToMono(byte[].class));
    }

    private Mono<String> getRedirectLocation(String pictureUrl) {
        return headRequest(pictureUrl)
                .mapNotNull(HttpHeaders::getLocation)
                .map(URI::toString);
    }

    private Mono<Picture> createPicture(String pictureUrl) {
        return headRequest(pictureUrl)
                .mapNotNull(HttpHeaders::getContentLength)
                .map(size -> new Picture(pictureUrl, size));
    }

    private Mono<HttpHeaders> headRequest(String url) {
        return WebClient.create(url)
                .head()
                .exchangeToMono(ClientResponse::toBodilessEntity)
                .map(HttpEntity::getHeaders);
    }
}
