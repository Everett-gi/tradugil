package br.com.tradugil;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TradugilApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradugilApplication.class, args);
    }
}
