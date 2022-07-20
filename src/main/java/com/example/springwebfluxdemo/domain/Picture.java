package com.example.springwebfluxdemo.domain;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Picture {
    private String url;
    private Long size;

    public Picture(String url, Long size) {
        this.url = url;
        this.size = size;
    }
}
