package com.siksaurus.yamstack.yam.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.jsonwebtoken.lang.Assert;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class Tag {

    @Id
    @JsonIgnore
    @GeneratedValue
    private long id;

    private String name;

    @Builder
    public Tag(String name) {
        Assert.notNull(name, "Name Not Null");

        this.name = name;
    }
}
