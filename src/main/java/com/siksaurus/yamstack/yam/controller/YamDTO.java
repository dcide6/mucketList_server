package com.siksaurus.yamstack.yam.controller;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

public class YamDTO {

    @Getter
    @Setter
    public static class filterYamInfo {
        private String region; //지역
        private String category; //카테고리
        private List<String> tags; //태그들
        private String searchName; //식당 이름
        private int mode; // 0:모두다 1:얌얌리스트 2:완료얌리스트

        public filterYamInfo() {
            this.mode = 0; //기본값
        }
    }

    @Getter
    @Setter
    public static class updateYam {
        private long id;
        private Set<String> foods;
        private Set<String> tags;
        private String memo;
    }

    @Getter
    @Setter
    public static class updateYamVisit {
        private long id;
        private boolean reVisit;
    }

    @Getter
    @Setter
    public static class deleteYam {
        private long id;
        private boolean isClosed;
    }
}
