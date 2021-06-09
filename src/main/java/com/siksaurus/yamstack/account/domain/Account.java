package com.siksaurus.yamstack.account.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.siksaurus.yamstack.yam.domain.Yam;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Account {
    @Id
    @Column(name = "account_id")
    private String id;

    @JsonIgnore
    @Column(name = "password")
    private String password;

    private String name;

    private boolean isEmailChecked;

    @JsonIgnore
    private String authCode;

    @Enumerated(EnumType.ORDINAL)
    private AccountRole role;

    @Builder
    public Account(String id, String password, String name, AccountRole role) {
        Assert.notNull(id,"Id Not Null");
        Assert.notNull(password,"Password Not Null");
        Assert.notNull(name, "Name Not Null");
        Assert.notNull(role,"Role Not Null");

        this.id = id;
        this.password = password;
        this.name = name;
        this.role = role;
    }

}
