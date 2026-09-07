package br.com.tradugiria;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TraduGiriaApplication {

    public static void main(String[] args) {
        SpringApplication.run(TraduGiriaApplication.class, args);
    }
}
