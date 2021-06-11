package com.siksaurus.yamstack.account.service;

import com.siksaurus.yamstack.account.domain.Account;
import com.siksaurus.yamstack.account.domain.AccountRole;
import com.siksaurus.yamstack.account.domain.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public Account saveAccount(Account account) {
        return accountRepository.save(account);
    }

    public Account addAccount(Account account) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        return accountRepository.save(account);
    }

    public Account changePassword(String id, String password) {
        Account account = this.getAccountById(id);
        account.setPassword(passwordEncoder.encode(password));
        return accountRepository.save(account);
    }

    public Account getAccountById(String id) {
        return accountRepository.findById(id).get();
    }

    public Account getAccountByName(String name) {
        return accountRepository.findByName(name).get();
    }

    public void deleteAccount(Account account) {
        accountRepository.deleteById(account.getId());
    }

    public boolean checkDuplicateId(String id) {
        Account account;
        try{
            account = accountRepository.findById(id).get();
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

        Account account = accountRepository.findById(username).orElseThrow(() -> new UsernameNotFoundException(username));

        return new User(account.getId(), account.getPassword(), authorities(account.getRole()));
    }

    private Collection<? extends GrantedAuthority> authorities(AccountRole role) {
        return Collections.singletonList(new SimpleGrantedAuthority(role.toString()));
    }
}
