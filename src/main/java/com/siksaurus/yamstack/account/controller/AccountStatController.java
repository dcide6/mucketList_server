package com.siksaurus.yamstack.account.controller;

import com.siksaurus.yamstack.account.domain.AccountStat;
import com.siksaurus.yamstack.account.domain.repository.AccountStatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/accountStat")
@RequiredArgsConstructor
public class AccountStatController {

    private final AccountStatRepository accountStatRepository;

    @GetMapping("/{from}/{to}")
    public ResponseEntity<List<AccountStat>> getAccountStatAll(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
                                                               @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to) {

        List<AccountStat> list = accountStatRepository.findAllByDateBetween(from, to);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(list);
    }

}
