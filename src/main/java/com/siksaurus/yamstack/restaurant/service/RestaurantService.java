package com.siksaurus.yamstack.restaurant.service;

import com.siksaurus.yamstack.global.Util;
import com.siksaurus.yamstack.restaurant.controller.RestaurantDTO;
import com.siksaurus.yamstack.restaurant.domain.Restaurant;
import com.siksaurus.yamstack.restaurant.domain.repository.RestaurantRepository;
import com.siksaurus.yamstack.review.controller.ReviewVO;
import com.siksaurus.yamstack.review.domain.Company;
import com.siksaurus.yamstack.review.domain.Review;
import com.siksaurus.yamstack.yam.domain.Food;
import com.siksaurus.yamstack.yam.domain.Tag;
import com.siksaurus.yamstack.yam.domain.Yam;
import com.siksaurus.yamstack.yam.domain.repository.YamRepository;
import com.siksaurus.yamstack.yam.service.YamService;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final YamService yamService;
    private final EntityManager entityManager;

    public Restaurant saveRestaurant(Restaurant restaurant) {
        if (restaurant.getX() != null && restaurant.getY() != null) {
            Double latitude = Double.parseDouble(restaurant.getY());
            Double longitude = Double.parseDouble(restaurant.getX());
            String pointWKT = String.format("POINT(%s %s)", longitude, latitude);

            // WKTReader를 통해 WKT를 실제 타입으로 변환
            Point point = null;
            try {
                point = (Point) new WKTReader().read(pointWKT);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            restaurant.setPoint(point);
        }
        return restaurantRepository.save(restaurant);
    }

    public Restaurant getRestaurantByApiId(String apiId) {
        return restaurantRepository.findByApiId(apiId).get();
    }

    public List<Restaurant> getRestaurantByName(String name) {
        return restaurantRepository.findByName(name).get();
    }

    public Restaurant getRestaurantFromRequest(RestaurantDTO.createRestaurantDTO dto) {
        Restaurant rst = restaurantRepository.findByApiId(dto.getId()).orElse(null);
        if (rst == null) {
            if (dto.getId() != null) {
                String address = dto.getAddress_name();
                String category = dto.getCategory_name();
                String[] addresses = address.split(" ");
                String[] categories = category.split(">");

                Restaurant restaurant = Restaurant.builder()
                        .apiId(dto.getId())
                        .name(dto.getPlace_name())
                        .addName(dto.getAddress_name())
                        .roadAddName(dto.getRoad_address_name())
                        .region1depth(addresses[0])
                        .region2depth(addresses[1])
                        .region3depth(addresses[2])
                        .x(dto.getX())
                        .y(dto.getY())
                        .category1depth(categories[0].trim())
                        .category2depth(categories[1].trim())
                        .build();
                rst = this.saveRestaurant(restaurant);
            } else {
                Restaurant restaurant = Restaurant.builder()
                        .name(dto.getPlace_name())
                        .addName(dto.getAddress_name())
                        .category1depth(dto.getCategory_name())
                        .category2depth(dto.getCategory_name())
                        .build();
                rst = this.saveRestaurant(restaurant);
            }
        }

        return rst;
    }

    public RestaurantVO getRestaurantVO(Long id, String x, String y) {
        RestaurantVO vo = new RestaurantVO();
        Restaurant restaurant = restaurantRepository.findById(id).get();
        double lat_rest = restaurant.getY() != null ? Double.parseDouble(restaurant.getY()) : 0.0;
        double lon_rest = restaurant.getX() != null ? Double.parseDouble(restaurant.getX()) : 0.0;

        vo.setRestaurant(restaurant);
        List<Yam> yams = yamService.getYamListByRestaurantId(id);
        if (yams != null && yams.size() > 0) {
            List<Food> foodList = new ArrayList<>();
            List<Tag> tagList = new ArrayList<>();
            List<Company> companyList = new ArrayList<>();
            Set<String> foods = new LinkedHashSet<>();
            Set<String> tags = new LinkedHashSet<>();

            for (Yam yam : yams) {
                if (yam.getCompeteTime() != null) vo.addCountVisitNum();
                if (yam.isGood()) vo.addCountRecommend();
                foodList.addAll(yam.getFoods());
                tagList.addAll(yam.getTags());
                Review review = yam.getReview();
                if (review != null && review.getCompany() != null) {
                    vo.addCountReviewNum();
                    companyList.add(review.getCompany());
                }
            }

            //빈도순 정렬
            tagList.sort(Comparator.comparing(tagList.stream().collect(Collectors.groupingBy(v -> v, Collectors.counting()))::get).reversed());
            tagList.forEach(tag -> tags.add(tag.getName()));
            foodList.sort(Comparator.comparing(foodList.stream().collect(Collectors.groupingBy(v -> v, Collectors.counting()))::get).reversed());
            foodList.forEach(food -> foods.add(food.getName()));
            companyList.sort(Comparator.comparing(companyList.stream().collect(Collectors.groupingBy(v -> v, Collectors.counting()))::get).reversed());

            vo.setYamPick(yams.size());
            vo.setFoods(foods);
            vo.setTags(tags);
            vo.setCompany(companyList.size() > 0 ? companyList.get(0) : null);
            vo.setReviews(this.getReviewVOFromYam(yams, 10));

            if(x != null && y != null && lat_rest > 0 && lon_rest > 0) {
                double lat = Double.parseDouble(y);
                double lon = Double.parseDouble(x);
                vo.setDist(Util.distance(lat_rest,lon_rest,lat,lon, "kilometer"));
            }
        }
        return vo;
    }

    private List<ReviewVO> getReviewVOFromYam(List<Yam> yams, int size) {
        List<ReviewVO> rst = new ArrayList<>();
        if (yams == null || yams.size() == 0) return rst;

        //리뷰가 있는 yam만 최근 생성일 기준으로 정렬
        List<Yam> yamList = yams.stream().filter(yam -> yam.getReview() != null).sorted((o1, o2) -> o1.getReview().getGenTime().compareTo(o2.getReview().getGenTime()) * -1).collect(Collectors.toList());

        List<Yam> subList = yamList.size() > size ? yamList.subList(0, size - 1) : yamList;
        for (Yam yam : subList) {
            ReviewVO vo = new ReviewVO(yam.getReview());
            rst.add(vo);
        }
        return rst;
    }

    public Page<RestaurantVO> getRestaurantVOList(String mode, String x, String y, Pageable pageable) {
        if (mode.equals("near")) return this.getRestaurantAllNearer(x,y, pageable);
        else return this.getRestaurantVOListByMode(mode, x, y, pageable);
    }

    private Page<RestaurantVO> getRestaurantAllNearer (String x, String y, Pageable pageable) {
        List<RestaurantVO> rst = new ArrayList<>();

        double lon = Double.parseDouble(x);
        double lat = Double.parseDouble(y);
        final int offset = (int) pageable.getOffset();
        final int limit = (int) pageable.getPageSize();

        String sql = "select *, ST_DISTANCE_SPHERE(POINT(" + x + ", " + y + "), point) as dist " +
                "from restaurant " +
                "order by dist" + " " +
                "limit " + limit + " "+
                "offset " + offset;

        String sql_count = "select COUNT(*) from restaurant";

        Query query = entityManager.createNativeQuery(sql, Restaurant.class);
        Query query_count = entityManager.createNativeQuery(sql_count);

        List<Restaurant> restaurants = query.getResultList();

        for(Restaurant restaurant : restaurants) {
            RestaurantVO vo = this.getRestaurantVO(restaurant.getId(), x, y);
            rst.add(vo);
        }

        BigInteger count = (BigInteger) query_count.getSingleResult();
        Page<RestaurantVO> result = new PageImpl<>(rst, pageable, count.bitCount());

        return result;
    }

    private Page<RestaurantVO> getRestaurantVOListByMode(String mode, String x, String y, Pageable pageable) {
        List<RestaurantVO> rst = new ArrayList<>();

        LocalDate from = LocalDate.now().minusDays(7);
        LocalDate to = LocalDate.now();
        List<Yam> yams = new ArrayList<>();
        switch (mode) {
            case "want":
                yams = yamService.getYamListBetweenGenTime(from, to);
                break;
            case "recommend":
                yams = yamService.getYamListBetweenCompleteTime(from, to);
                break;
            case "done":
                yams = yamService.getYamListBetweenCompletTimeAndNotGood(from, to);
                break;
        }
        List<Long> restaurantIds = yams.stream().map(yam -> yam.getRestaurant().getId()).collect(Collectors.toList());
        Set<Long> idSet = new LinkedHashSet<>();
        //빈도순 정렬
        restaurantIds.sort(Comparator.comparing(restaurantIds.stream().collect(Collectors.groupingBy(v -> v, Collectors.counting()))::get).reversed());
        restaurantIds.forEach(id -> idSet.add(id));

        idSet.forEach(id -> rst.add(this.getRestaurantVO(id, x, y)));

        final int start = (int) pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), rst.size());
        final Page<RestaurantVO> result = new PageImpl<>(rst.subList(start, end), pageable, rst.size());

        return result;
    }

    public void deleteRestaurant(Restaurant restaurant) {
        restaurantRepository.deleteById(restaurant.getId());
    }
}
