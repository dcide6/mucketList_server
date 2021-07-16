package com.siksaurus.yamstack.account.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class AccountStat {

    @Id
    private long accountId;
    private LocalDate date;
    private boolean isJoin;

    @Builder
    public AccountStat(long accountId, LocalDate date, boolean isJoin) {
        this.accountId = accountId;
        this.date = date;
        this.isJoin = isJoin;
    }

}
