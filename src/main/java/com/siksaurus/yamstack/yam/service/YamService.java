package com.siksaurus.yamstack.yam.service;

import com.siksaurus.yamstack.account.domain.Account;
import com.siksaurus.yamstack.restaurant.controller.RestaurantDTO;
import com.siksaurus.yamstack.restaurant.domain.Restaurant;
import com.siksaurus.yamstack.review.domain.Review;
import com.siksaurus.yamstack.yam.controller.MetaInfo;
import com.siksaurus.yamstack.yam.controller.YamDTO;
import com.siksaurus.yamstack.yam.domain.Food;
import com.siksaurus.yamstack.yam.domain.Tag;
import com.siksaurus.yamstack.yam.domain.Yam;
import com.siksaurus.yamstack.yam.domain.repository.YamQueryRepository;
import com.siksaurus.yamstack.yam.domain.repository.YamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class YamService {

    private final YamRepository yamRepository;
    private final YamQueryRepository yamQueryRepository;
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

    public Page<Yam> getYamListByUserEmail(String email, Pageable pageable) {
        return yamRepository.findByAccount_Email(email, pageable);
    }

    public Yam getYamById(Long id) {
        return yamRepository.findById(id).get();
    }
    public Page<Yam> getYamListFilter(String email, YamDTO.filterYamInfo info, Pageable pageable) {
        return yamQueryRepository.findDynamicQuery(email, info, pageable);
    }

    public void deleteYam(Yam yam) {
        yamRepository.delete(yam);
    }

    public MetaInfo getYamListMetaInfo(String email) {
        MetaInfo metaInfo = new MetaInfo();
        List<Yam> yams = this.getYamListByUserEmail(email);
        if(yams != null && yams.size() > 0) {
            Map<String, Set<String>> regionInfo = new HashMap<>();
            Set<String> categories = new HashSet<>();
            Set<String> tags = new LinkedHashSet<>();
            List<String> tagList = new ArrayList<>();
            int yamSize = 0;
            int completeSize = 0;
            int noRevisitSize = 0;
            for(Yam yam : yams) {
                //달성률 data
                if(yam.getCompeteTime() == null) yamSize++;
                else {
                    if(yam.isGood()) completeSize++;
                    else noRevisitSize++;
                }

                Restaurant restaurant = yam.getRestaurant();
                String key = restaurant.getRegion1depth();
                String value = restaurant.getRegion2depth();
                if(value.equals("")) value = restaurant.getRegion3depth();
                String category = restaurant.getCategory2depth();
                if(regionInfo.containsKey(key)) {
                    Set<String> newSet = regionInfo.get(key);
                    newSet.add(value);
                    regionInfo.put(key, newSet);
                }else {
                    regionInfo.put(key, new HashSet<>(Arrays.asList(value)));
                }
                categories.add(category);
                yam.getTags().forEach(tag -> tagList.add(tag.getName()));
            };
            //빈도순정렬
            tagList.sort(Comparator.comparing(tagList.stream().collect(Collectors.groupingBy(x -> x, Collectors.counting()))::get).reversed());
            tagList.forEach(tag->tags.add(tag));

            metaInfo.setRegionInfo(regionInfo);
            metaInfo.setCategories(categories);
            metaInfo.setTags(tags);
            metaInfo.setYamSize(yamSize);
            metaInfo.setCompleteSize(completeSize);
            metaInfo.setNoRevisitSize(noRevisitSize);
        }
        return metaInfo;
    }
}
