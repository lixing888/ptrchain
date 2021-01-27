package com.ptrchain.messagecenterptrchain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@SpringBootApplication
@EnableSwagger2
public class MessageCenterPtrchainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessageCenterPtrchainApplication.class, args);
    }
}
