package com.siksaurus.yamstack.user.domain;

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
public class User {
    @Id
    @Column(name = "user_id")
    private String id;
    @JsonIgnore
    private String password;

    private String name;

    @Enumerated(EnumType.ORDINAL)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<UserRole> roles = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Yam> yams;

    @Builder
    public User(String id, String password, String name, Set<UserRole> roles) {
        Assert.notNull(id,"Not Null");
        Assert.notNull(password,"Not Null");
        Assert.notNull(name, "Not Null");
        Assert.notNull(roles,"Not Null");

        this.id = id;
        this.password = password;
        this.name = name;
        this.roles = roles;
    }

}
