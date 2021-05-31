package com.siksaurus.yamstack.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.siksaurus.yamstack.yam.domain.Yam;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @Column(name = "user_id")
    private String id;
    @JsonIgnore
    private String pw;

    private String name;
    private UserRole role;

    @OneToMany(mappedBy = "user")
    private Set<Yam> yams;
}
