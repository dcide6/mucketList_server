package com.siksaurus.yamstack.yam.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Tag {

    @Id
    @GeneratedValue
    private long id;

    private String name;
}
