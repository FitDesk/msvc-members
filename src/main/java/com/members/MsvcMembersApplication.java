package com.members;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.boot.SpringApplication;

@SpringBootApplication
@EnableFeignClients
public class MsvcMembersApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsvcMembersApplication.class, args);
    }

}
