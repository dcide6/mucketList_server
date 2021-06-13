package com.siksaurus.yamstack.restaurant.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.siksaurus.yamstack.yam.domain.Yam;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Restaurant {

    @Id
    @GeneratedValue
    @Column(name = "restr_id")
    private long id;

    private String name;
    private String addName;
    private String roadAddName;
    private String region1depth;
    private String region2depth;
    private String region3depth;
    private double x;
    private double y;
    private String category1depth;
    private String category2depth;

    @OneToMany(mappedBy = "restaurant")
    private Set<Yam> yams;
}
