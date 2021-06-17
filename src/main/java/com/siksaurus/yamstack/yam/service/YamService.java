package com.siksaurus.yamstack.yam.service;

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

    public int saveYams(List<Yam> yams) {
        try{
            yamRepository.saveAll(yams);
            return 0;
        }catch (Exception e){
            return -1;
        }
    }

    public List<Yam> getYamListByUserEmail(String email) {
        return yamRepository.findByAccount_Email(email);
    }

    public void deleteYam(Yam yam) {
        yamRepository.delete(yam);
    }
}
