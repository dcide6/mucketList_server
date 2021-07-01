package com.siksaurus.yamstack.yam.controller;

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

    @GetMapping("/metaInfo/{email}")
    public ResponseEntity<MetaInfo> getYamFilterInfo(@PathVariable String email) {

        MetaInfo metaInfo = yamService.getYamListMetaInfo(email);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(metaInfo);
    }

    @PostMapping("/{email}")
    public ResponseEntity<Page<Yam>> getYamsByEmail(@PathVariable String email, @RequestBody YamDTO.filterYamInfo filter, YamPageRequest pageable) {

        Page<Yam> yams = yamService.getYamListFilter(email, filter, pageable.of());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(yams);
    }
}
