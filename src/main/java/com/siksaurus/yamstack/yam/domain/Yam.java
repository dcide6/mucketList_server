package com.siksaurus.yamstack.yam.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.siksaurus.yamstack.restaurant.domain.Restaurant;
import com.siksaurus.yamstack.review.domain.Review;
import com.siksaurus.yamstack.account.domain.Account;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
@Setter
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

}
