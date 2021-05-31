package com.siksaurus.yamstack.user.service;

import com.siksaurus.yamstack.user.domain.User;
import com.siksaurus.yamstack.user.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User getUser(User user) {
        return userRepository.findById(user.getId()).get();
    }

    public void deleteUser(User user) {
        userRepository.deleteById(user.getId());
    }
}
