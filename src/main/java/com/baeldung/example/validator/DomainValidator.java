package com.baeldung.example.validator;

import org.springframework.stereotype.Component;

import com.baeldung.example.exception.ValidationException;
import com.baeldung.example.model.Domain;

@Component
public class DomainValidator {

    public void validate(String domainName, Domain domain) {
        if (!domainName.equals(domain.getName())) {
            throw new ValidationException("the domainName path variable does not match the domain.name");
        }
        validate(domain);
    }

    public void validate(Domain domain) {
        if (domain.getName() == null || domain.getName().isEmpty()) {
            throw new ValidationException("the domain name must not be empty");
        }
        if (domain.getTld() == null || domain.getTld().isEmpty()) {
            throw new ValidationException("the domain tld must not be empty");
        }
    }
}
