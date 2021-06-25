package com.siksaurus.yamstack.restaurant.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.siksaurus.yamstack.yam.domain.Yam;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Restaurant {

    @Id
    @GeneratedValue
    @Column(name = "restr_id")
    private long id;

    private String apiId;  //카카오에서 관리하는 id
    private String name;
    private String addName;
    private String roadAddName;
    private String region1depth;
    private String region2depth;
    private String region3depth;
    private String x;
    private String y;
    private String category1depth;
    private String category2depth;
    private Integer closedCount;

    @JsonIgnore
    @OneToMany(mappedBy = "restaurant")
    private Set<Yam> yams;

    @Builder
    public Restaurant(String apiId,
                      String name,
                      String addName,
                      String roadAddName,
                      String region1depth,
                      String region2depth,
                      String region3depth,
                      String x,
                      String y,
                      String category1depth,
                      String category2depth) {
        Assert.notNull(name,"Name Not Null");
        Assert.notNull(addName,"Address Name Not Null");

        this.apiId = apiId;
        this.name = name;
        this.addName = addName;
        this.roadAddName = roadAddName;
        this.region1depth = region1depth;
        this.region2depth = region2depth;
        this.region3depth = region3depth;
        this.x = x;
        this.y = y;
        this.category1depth = category1depth;
        this.category2depth = category2depth;
        this.closedCount = 0;
    }
}
