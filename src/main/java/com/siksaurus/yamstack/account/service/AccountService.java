package com.siksaurus.yamstack.account.service;

import com.siksaurus.yamstack.account.domain.Account;
import com.siksaurus.yamstack.account.domain.AccountRole;
import com.siksaurus.yamstack.account.domain.repository.AccountRepository;
import com.siksaurus.yamstack.yam.domain.Yam;
import com.siksaurus.yamstack.yam.service.YamService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final YamService yamService;
    private final PasswordEncoder passwordEncoder;

    public Account saveAccount(Account account) {
        return accountRepository.save(account);
    }

    public Account addAccount(Account account) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        return accountRepository.save(account);
    }

    public Account changePassword(String email, String password) {
        Account account = this.getAccountByEmail(email);
        account.setPassword(passwordEncoder.encode(password));
        return accountRepository.save(account);
    }

    public Account getAccountByEmail(String email) {
        return accountRepository.findByEmail(email).get();
    }

    public Account getAccountByName(String name) {
        return accountRepository.findByName(name).get();
    }

    @Transactional
    public void deleteAccountByEmail(String email) {
        Account account = this.getAccountByEmail(email);
        List<Yam> yamList = yamService.getYamListByUserEmail(email);
        yamList.forEach(yam -> yam.setAccount(null));
        yamService.saveYams(yamList);
        accountRepository.deleteById(account.getId());
    }

    public boolean checkDuplicateEmail(String email) {
        Account account;
        try{
            account = accountRepository.findByEmail(email).get();
            return false;
        }catch (NoSuchElementException e) {
            return true;
        }
    }

    public boolean checkDuplicateName(String name) {
        Account account;
        try{
            account = accountRepository.findByName(name).get();
            return false;
        }catch (NoSuchElementException e) {
            return true;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Account account = accountRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));

        return new User(account.getEmail(), account.getPassword(), authorities(account.getRole()));
    }

    private Collection<? extends GrantedAuthority> authorities(AccountRole role) {
        return Collections.singletonList(new SimpleGrantedAuthority(role.toString()));
    }
}
