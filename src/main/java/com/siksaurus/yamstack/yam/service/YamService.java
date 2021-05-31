package com.siksaurus.yamstack.yam.service;

import com.siksaurus.yamstack.user.domain.User;
import com.siksaurus.yamstack.yam.domain.Yam;
import com.siksaurus.yamstack.yam.domain.repository.YamRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class YamService {

    private final YamRepository yamRepository;

    public YamService(YamRepository yamRepository) {
        this.yamRepository = yamRepository;
    }

    public Yam saveYam(Yam yam) {
        return yamRepository.save(yam);
    }

    public List<Yam> getYamListByUserId(User user) {
        return yamRepository.findById(user.getId());
    };

    public void deleteYam(Yam yam) {
        yamRepository.delete(yam);
    }
}
