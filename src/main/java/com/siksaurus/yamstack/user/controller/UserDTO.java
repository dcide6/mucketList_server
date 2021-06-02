package com.siksaurus.yamstack.user.controller;

import com.siksaurus.yamstack.user.domain.User;
import com.siksaurus.yamstack.user.domain.UserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.Assert;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UserCreateDTO {

        @NotNull
        @Email
        private String id;

        @NotNull
        private String password;

        @NotNull
        private Set<UserRole> role;

        @Builder
        public UserCreateDTO(String id, String password, Set<UserRole> role) {
            Assert.notNull(id, "Not Null");
            Assert.notNull(password, "Not Null");
            Assert.notNull(role, "Not Null");

            this.id = id;
            this.password = password;
            this.role = role;
        }

        public User toEntity() {
            User user = User.builder()
                    .id(id)
                    .password(password)
                    .roles(role)
                    .build();

            return user;
        }
    }
}
