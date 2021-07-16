package com.siksaurus.yamstack.account.controller;

import com.siksaurus.yamstack.account.domain.Account;
import com.siksaurus.yamstack.account.domain.AccountRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

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

        private String newPassword;

        private String newName;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class resendAuthCodeDTO {

        private String email;
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

    @Getter
    @Setter
    @NoArgsConstructor
    public static class refreshTokenDTO {
        private String refreshToken;
    }

}
