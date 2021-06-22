package com.siksaurus.yamstack.account.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Account {
    @Id
    @GeneratedValue
    @Column(name = "account_id")
    private long id;

    private String email;

    @JsonIgnore
    @Column(name = "password")
    private String password;

    private String name;

    private boolean isEmailChecked;

    @JsonIgnore
    private String authCode;

    @Enumerated(EnumType.ORDINAL)
    private AccountRole role;

    private LocalDate lastLoginDate;

    private LocalDateTime pwChangedDate;

    @Builder
    public Account(String email, String password, String name, AccountRole role) {
        Assert.notNull(email,"Id Not Null");
        Assert.notNull(password,"Password Not Null");
        Assert.notNull(name, "Name Not Null");
        Assert.notNull(role,"Role Not Null");

        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
    }

}
