package com.siksaurus.yamstack.account.controller;

import com.siksaurus.yamstack.account.domain.Account;
import com.siksaurus.yamstack.account.domain.AccountRole;
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
public class AccountDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class loginDTO {
        @NotNull
        @Email
        private String id;

        @NotNull
        private String password;
    }

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
        private AccountRole role;

        @Builder
        public UserCreateDTO(String id, String password, AccountRole role) {
            Assert.notNull(id, "Not Null");
            Assert.notNull(password, "Not Null");
            Assert.notNull(role, "Not Null");

            this.id = id;
            this.password = password;
            this.role = role;
        }

        public Account toEntity() {
            Account account = Account.builder()
                    .id(id)
                    .password(password)
                    .role(role)
                    .build();

            return account;
        }
    }
}
