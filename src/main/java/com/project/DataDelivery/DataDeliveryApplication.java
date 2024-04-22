package com.project.DataDelivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class DataDeliveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataDeliveryApplication.class, args);
    }
}

// WAR Package
//@SpringBootApplication
//public class DataDeliveryApplication extends SpringBootServletInitializer {
//
//    public static void main(String[] args) {
//        SpringApplication.run(DataDeliveryApplication.class, args);
//    }
//
//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
//        return builder.sources(DataDeliveryApplication.class);
//    }
//}
