package com.siksaurus.yamstack.review.controller;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewCreateResponse {
    private String message;
    private String code;
    private int status;
    private long review_id;
}
