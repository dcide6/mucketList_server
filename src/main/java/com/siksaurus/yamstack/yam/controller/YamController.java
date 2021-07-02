package com.siksaurus.yamstack.yam.controller;

import com.siksaurus.yamstack.global.security.JwtAuthToken;
import com.siksaurus.yamstack.global.security.JwtAuthTokenProvider;
import com.siksaurus.yamstack.yam.domain.Yam;
import com.siksaurus.yamstack.yam.service.YamService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/yam")
@RequiredArgsConstructor
public class YamController {

    private final YamService yamService;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;

    @GetMapping("/metaInfo")
    public ResponseEntity<MetaInfo> getYamFilterInfo(@RequestHeader(value = "x-auth-token") String token) {

        JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token);
        String email = (String) jwtAuthToken.getData().get("sub");

        MetaInfo metaInfo = yamService.getYamListMetaInfo(email);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(metaInfo);
    }

    @PostMapping
    public ResponseEntity<Page<Yam>> getYamsByEmail(@RequestHeader(value = "x-auth-token") String token,
                                                    @RequestBody YamDTO.filterYamInfo filter,
                                                    YamPageRequest pageable) {

        JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token);
        String email = (String) jwtAuthToken.getData().get("sub");

        Page<Yam> yams = yamService.getYamListFilter(email, filter, pageable.of());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(yams);
    }
}
