package com.siksaurus.yamstack.global;

import com.siksaurus.yamstack.account.domain.Account;
import com.siksaurus.yamstack.account.domain.AccountRole;
import com.siksaurus.yamstack.account.service.AccountService;
import com.siksaurus.yamstack.restaurant.domain.Restaurant;
import com.siksaurus.yamstack.restaurant.service.RestaurantService;
import com.siksaurus.yamstack.review.service.ReviewService;
import com.siksaurus.yamstack.yam.domain.Yam;
import com.siksaurus.yamstack.yam.service.YamService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DBinit implements ApplicationRunner {

    public final AccountService accountService;
    public final RestaurantService restaurantService;
    public final YamService yamService;
    public final ReviewService reviewService;
    public final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Account account = Account.builder()
                .id("test@aaa.bbb")
                .password("test1234")
                .name("한상호")
                .role(AccountRole.USER)
                .build();

        Account rst_account = this.accountService.saveAccount(account);

        Restaurant restaurant = new Restaurant();
        restaurant.setName("상도동식당");
        restaurant.setAddName("서울 상도동 111");
        restaurant.setRegion1depth("서울");
        restaurant.setRegion2depth("동작구");
        restaurant.setRegion3depth("상도동");
        restaurant.setCategory1depth("한식");
        restaurant.setCategory2depth("기사식당");

        Restaurant rst_rest = this.restaurantService.saveRestaurant(restaurant);

        Yam yam = new Yam();
        yam.setGenTime(LocalDate.now());
        yam.setAccount(rst_account);
        yam.setRestaurant(rst_rest);
        yam.setMemo("언젠간 가야지..");

        this.yamService.saveYam(yam);
    }
}
