package com.siksaurus.yamstack.account.controller;

import com.siksaurus.yamstack.account.domain.Account;
import com.siksaurus.yamstack.account.domain.AccountRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

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
        private String email;

        @NotNull
        private String password;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreateAccountDTO {

        @NotNull
        @Email
        private String email;

        @NotNull
        private String password;

        @NotNull
        private String name;

        public Account toEntity() {
            Account account = Account.builder()
                    .email(email)
                    .password(password)
                    .name(name)
                    .role(AccountRole.USER)
                    .build();

            return account;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UpdateAccountDTO {

        @NotNull
        @Email
        private String email;

        private String password;

        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class IdentifyAccountDTO {

        @NotNull
        @Email
        private String email;

        @NotNull
        private String authCode;
    }

}
