package com.siksaurus.yamstack.restaurant.controller;

import io.jsonwebtoken.lang.Assert;
import lombok.*;

import java.util.Set;

public class RestaurantDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class createRestaurantDTO {
        //Account info
        private String email;

        //restaurant info
        private String id;
        private String place_name;
        private String category_name;
        private String category_group_code;
        private String category_group_name;
        private String phone;
        private String address_name;
        private String road_address_name;
        private String x;
        private String y;
        private String place_url;

        //yam info
        private Set<String> foods;
        private Set<String> tags;
        private String memo;

        @Builder
        public createRestaurantDTO(String email,
                                   String id,
                                   String place_name,
                                   String category_name,
                                   String category_group_code,
                                   String category_group_name,
                                   String phone,
                                   String address_name,
                                   String road_address_name,
                                   String x,
                                   String y,
                                   String place_url,
                                   Set<String> foods,
                                   Set<String> tags,
                                   String memo) {
            Assert.notNull(email, "Email Not Null");
            Assert.notNull(place_name, "Place Name Not Null");
            Assert.notNull(category_name, "Category Name Not Null");
            Assert.notNull(address_name, "Address Not Null");

            this.email = email;
            this.id = id;
            this.place_name = place_name;
            this.category_name = category_name;
            this.category_group_code = category_group_code;
            this.category_group_name = category_group_name;
            this.phone = phone;
            this.address_name = address_name;
            this.road_address_name = road_address_name;
            this.x = x;
            this.y = y;
            this.place_url = place_url;
            this.foods = foods;
            this.tags = tags;
            this.memo = memo;
        }

    }


}
