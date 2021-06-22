package com.siksaurus.yamstack.yam.domain;

import com.siksaurus.yamstack.restaurant.domain.Restaurant;
import com.siksaurus.yamstack.review.domain.Review;
import com.siksaurus.yamstack.account.domain.Account;
import io.jsonwebtoken.lang.Assert;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Yam {
    @Id
    @GeneratedValue
    @Column(name = "yam_id")
    private long id;
    private LocalDate genTime;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "restr_id")
    private Restaurant restaurant;

    @ManyToMany
    @JoinTable(name = "yam_food_table")
    private Set<Food> foods;

    @ManyToMany
    @JoinTable(name = "yam_tag_table")
    private Set<Tag> tags;

    private String memo;
    private boolean closed;

    @OneToOne(mappedBy = "yam")
    private Review review;

    @Builder
    public Yam(LocalDate genTime,
               Account account,
               Restaurant restaurant,
               Set<Food> foods,
               Set<Tag> tags,
               String memo) {
        Assert.notNull(genTime, "GenTime Not Null");
        Assert.notNull(account, "Account Not Null");
        Assert.notNull(restaurant, "Restaurant Not Null");

        this.genTime = genTime;
        this.account = account;
        this.restaurant = restaurant;
        this.foods = foods;
        this.tags = tags;
        this.memo = memo;
    }

}
