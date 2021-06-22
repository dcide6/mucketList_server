package com.siksaurus.yamstack.yam.service;

import com.siksaurus.yamstack.account.domain.Account;
import com.siksaurus.yamstack.restaurant.controller.RestaurantDTO;
import com.siksaurus.yamstack.restaurant.domain.Restaurant;
import com.siksaurus.yamstack.yam.domain.Food;
import com.siksaurus.yamstack.yam.domain.Tag;
import com.siksaurus.yamstack.yam.domain.Yam;
import com.siksaurus.yamstack.yam.domain.repository.YamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class YamService {

    private final YamRepository yamRepository;
    private final TagService tagService;
    private final FoodService foodService;

    public Yam saveYam(Yam yam) {
        return yamRepository.save(yam);
    }

    public int saveYams(List<Yam> yams) {
        try{
            yamRepository.saveAll(yams);
            return 0;
        }catch (Exception e){
            log.error("saveYams error :"+e.getMessage());
            return -1;
        }
    }

    @Transactional
    public Yam saveYamFromRequest(Account account, Restaurant restaurant, RestaurantDTO.createRestaurantDTO dto) {
        Set<Tag> tags;
        Set<Food> foods;
        tags = tagService.saveTags(dto.getTags());
        foods = foodService.saveFoods(dto.getFoods());

        Yam yam = Yam.builder()
                .genTime(LocalDate.now())
                .account(account)
                .restaurant(restaurant)
                .foods(foods)
                .tags(tags)
                .memo(dto.getMemo())
                .build();
        return this.saveYam(yam);
    }

    public List<Yam> getYamListByUserEmail(String email) {
        return yamRepository.findByAccount_Email(email);
    }

    public void deleteYam(Yam yam) {
        yamRepository.delete(yam);
    }
}
