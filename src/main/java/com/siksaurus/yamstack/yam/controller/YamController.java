package com.siksaurus.yamstack.yam.controller;

import com.siksaurus.yamstack.yam.domain.Yam;
import com.siksaurus.yamstack.yam.service.YamService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/yam")
@RequiredArgsConstructor
public class YamController {

    private final YamService yamService;

    @GetMapping("/{email}")
    public ResponseEntity<Page<Yam>> getYamsByEmail(@PathVariable String email, YamPageRequest pageable) {

        Page<Yam> yams = yamService.getYamListByUserEmail(email, pageable.of());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(yams);
    }
}
