package com.siksaurus.yamstack.yam.domain;

import com.siksaurus.yamstack.restaurant.domain.Restaurant;
import com.siksaurus.yamstack.review.domain.Review;
import com.siksaurus.yamstack.user.domain.User;
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
    @JoinColumn(name = "user_id")
    private User user;

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
    private String company;

    @OneToOne(mappedBy = "yam")
    private Review review;

}
