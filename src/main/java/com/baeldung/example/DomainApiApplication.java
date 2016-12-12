package com.baeldung.example;

import static org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType.HAL;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.config.EnableHypermediaSupport;

import com.baeldung.example.model.Domain;

@SpringBootApplication
@EnableHypermediaSupport(type = HAL)
public class DomainApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DomainApiApplication.class, args);
    }

    @Bean
    public Map<String, Domain> configuredDomains() {
        Map<String, Domain> configuredDomains = new HashMap<>();
        Domain domain1 = new Domain("example.com", "com", "2016-09-12", Boolean.TRUE);
        Domain domain2 = new Domain("example.nyc", "nyc", "2016-010-22", Boolean.FALSE);
        Domain domain3 = new Domain("example.london", "london", "2016-11-30", Boolean.FALSE);
        Domain domain4 = new Domain("example.paris", "paris", "2016-02-06", Boolean.TRUE);
        Domain domain5 = new Domain("example.camera", "camera", "2016-05-17", Boolean.TRUE);
        Domain domain6 = new Domain("example.photography", "photography", "2016-02-20", Boolean.FALSE);

        configuredDomains.put(domain1.getName(), domain1);
        configuredDomains.put(domain2.getName(), domain2);
        configuredDomains.put(domain3.getName(), domain3);
        configuredDomains.put(domain4.getName(), domain4);
        configuredDomains.put(domain5.getName(), domain5);
        configuredDomains.put(domain6.getName(), domain6);

        return configuredDomains;
    }
}
