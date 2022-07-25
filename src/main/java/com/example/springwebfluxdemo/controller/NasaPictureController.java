package com.example.springwebfluxdemo.controller;

import com.example.springwebfluxdemo.service.ReactiveNasaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/pictures")
@RequiredArgsConstructor
public class NasaPictureController {
    @Value("${nasa.base.url}")
    private String nasaUrl;
    @Value("${nasa.api.key}")
    private String apiKey;
    private final ReactiveNasaService service;

    @GetMapping("/{sol}/largest")
    public Mono<byte[]> getLargestPictureBySol(@PathVariable String sol) {
        return service.getLargestPic(UriComponentsBuilder.fromHttpUrl(nasaUrl)
                .queryParam("sol", sol)
                .queryParam("api_key", apiKey)
                .toUriString());
    }
}
