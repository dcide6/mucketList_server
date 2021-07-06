package com.siksaurus.yamstack.restaurant.controller;

import org.springframework.data.domain.Sort;

public class RestaurantPageRequest {
    private int page;
    private int size;
    private Sort.Direction direction;

    public RestaurantPageRequest() {
        this.page = 0;
        this.size = 10;
        this.direction = Sort.Direction.DESC;
    }

    public void setPage(int page) {
        this.page = page <= 0 ? 0 : page;
    }

    public void setSize(int size) {
        int DEFAULT_SIZE = 10;
        int MAX_SIZE = 50;
        this.size = size > MAX_SIZE ? DEFAULT_SIZE : size;
    }

    public void setDirection(Sort.Direction direction) {
        this.direction = direction;
    }

    // getter
    public org.springframework.data.domain.PageRequest of() {
        return org.springframework.data.domain.PageRequest.of(page, size, direction, "nullValue");
    }
}
