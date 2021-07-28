package com.siksaurus.yamstack.account.service;

import com.siksaurus.yamstack.account.domain.Account;
import com.siksaurus.yamstack.global.security.JwtAuthToken;
import com.siksaurus.yamstack.global.security.JwtAuthTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;
    private final AccountService accountService;

    private final static long LOGIN_RETENTION_MINUTES = 60 * 12;
    private final static long REFRESH_TOKEN_MINUTE = 60 * 24 * 7;

    public Optional<Account> login(String email, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);

        //사용자 비밀번호 체크, 패스워드 일치하지 않는다면 Exception 발생 및 이후 로직 실행 안됨
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        //로그인 성공하면 인증 객체 생성 및 스프링 시큐리티 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Account account = accountService.getAccountByEmail(email);
        return Optional.ofNullable(account);
    }

    public JwtAuthToken createAuthToken(Account account) {
        Date expiredDate = Date.from(LocalDateTime.now().plusMinutes(LOGIN_RETENTION_MINUTES).atZone(ZoneId.systemDefault()).toInstant());
        return jwtAuthTokenProvider.createAuthToken(account.getEmail(), account.getRole(), "access", expiredDate);
    }

    public JwtAuthToken createRefreshToken(Account account) {
        Date expiredDate = Date.from(LocalDateTime.now().plusMinutes(REFRESH_TOKEN_MINUTE).atZone(ZoneId.systemDefault()).toInstant());
        return jwtAuthTokenProvider.createAuthToken(account.getEmail(), account.getRole(), "refresh", expiredDate);
    }

    public Map<String, String> refreshAccessToken(String token) {
        JwtAuthToken refreshToken = jwtAuthTokenProvider.convertAuthToken(token);
        Claims claims = refreshToken.getData();
        String email = (String) claims.get("sub");
        Account account = accountService.getAccountByEmail(email);

        Map<String, String> rst = new HashMap<>();
        rst.put("access", this.createAuthToken(account).getToken());
        rst.put("refresh", this.createRefreshToken(account).getToken());

        return rst;
    }


    public boolean checkAuthCode(String email, String authCode) {
        Account account = accountService.getAccountByEmail(email);
        boolean rst = account.getAuthCode().equals(authCode);
        if(rst) {
            account.setEmailChecked(true);
            account.setAuthCode("");
            accountService.saveAccount(account);
        }
        return rst;
    }


}
