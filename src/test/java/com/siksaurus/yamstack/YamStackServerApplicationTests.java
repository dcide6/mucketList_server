package com.siksaurus.yamstack;

import com.siksaurus.yamstack.restaurant.domain.Restaurant;
import com.siksaurus.yamstack.restaurant.service.RestaurantService;
import com.siksaurus.yamstack.review.domain.Company;
import com.siksaurus.yamstack.review.domain.Review;
import com.siksaurus.yamstack.review.service.ReviewService;
import com.siksaurus.yamstack.user.domain.User;
import com.siksaurus.yamstack.user.domain.UserRole;
import com.siksaurus.yamstack.user.service.UserService;
import com.siksaurus.yamstack.yam.domain.Food;
import com.siksaurus.yamstack.yam.domain.Tag;
import com.siksaurus.yamstack.yam.domain.Yam;
import com.siksaurus.yamstack.yam.service.YamService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Set;

@SpringBootTest
class YamStackServerApplicationTests {

    @Autowired
    YamService yamService;

    @Autowired
    UserService userService;

    @Autowired
    RestaurantService restaurantService;

    @Autowired
    ReviewService reviewService;

    @Test
    void contextLoads() {
    }

    @Test
    void test() {
        //유저 등록
        User user = new User();
        user.setId("test@gmail.com");
        user.setPassword("test111");
        user.setRoles(Set.of(UserRole.USER));
        user.setName("한상호");

        User rst_user = userService.saveUser(user);

        //식당 검색
        Restaurant restaurant = new Restaurant();
        restaurant.setName("엽기떡볶이");
        restaurant.setAddName("서울 상도동 성대로17길 32");

        Restaurant rst_restr = restaurantService.saveRestaurant(restaurant);

        //정보 입력
        Yam yam = new Yam();
        yam.setUser(user);
        yam.setRestaurant(restaurant);

        Tag tag = new Tag();
        tag.setName("떡볶이");

        Food food = new Food();
        food.setName("매운맛떡볶이");

//        yam.setTags(Set.of(tag));
//        yam.setFoods(Set.of(food));
        yam.setGenTime(LocalDate.now());
        yam.setMemo("언젠간 가봐야지...");

        Yam rst_yam = yamService.saveYam(yam);

        Review review = new Review();
        review.setComment("강추! 다시 생각나는 맛");
        review.setShared(true);
        review.setCompany(Company.FRIEND);

        Review rst_review = reviewService.saveReview(review);

        User user1 = userService.getUser(rst_user.getId());
        Restaurant restaurant1 = restaurantService.getRestaurantById(rst_restr);
        Review review1 = reviewService.getReview(rst_review);
    }

}
