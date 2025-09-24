package com.pronexa.connect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class PronexaConnectApplication {

    public static void main(String[] args) {

        // Load .env from project root
        Dotenv dotenv = Dotenv.configure()
                               .directory(System.getProperty("user.dir"))
                               .ignoreIfMalformed()
                               .ignoreIfMissing()
                               .load();

        // Set environment variables for Spring placeholders
        dotenv.entries().forEach(entry -> 
            System.setProperty(entry.getKey(), entry.getValue())
        );

        SpringApplication.run(PronexaConnectApplication.class, args);
    }
}
