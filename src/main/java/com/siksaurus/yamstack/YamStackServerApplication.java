package com.siksaurus.yamstack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class YamStackServerApplication {
    public static final String APPLICATION_LOCATIONS = "spring.config.location=" +
            "classpath:application.yml," +
            "./app/config/springboot-mucketList_server/real-application.yml";
    public static void main(String[] args) {
        //SpringApplication.run(YamStackServerApplication.class, args);
        new SpringApplicationBuilder(YamStackServerApplication.class)
                .properties(APPLICATION_LOCATIONS)
                .run(args);
    }

}
