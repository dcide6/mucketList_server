package com.siksaurus.yamstack.yam.domain;

import io.jsonwebtoken.lang.Assert;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Tag {

    @Id
    @GeneratedValue
    private long id;

    private String name;

    @Builder
    public Tag(String name) {
        Assert.notNull(name, "Name Not Null");

        this.name = name;
    }
}
