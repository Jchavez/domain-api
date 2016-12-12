package com.baeldung.example.controller;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.baeldung.example.exception.ValidationException;
import com.baeldung.example.model.Domain;
import com.baeldung.example.service.DomainService;

@RestController
@RequestMapping(value = "domains")
public class DomainController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomainController.class);

    @Autowired
    private DomainService domainService;

    @RequestMapping
    public ResponseEntity<Set<Domain>> getDomains() {
        Set<Domain> domains = domainService.findAll();
        return new ResponseEntity<>(domains, HttpStatus.OK);
    }

    @RequestMapping(value = "{domainName}", produces = "application/json")
    public ResponseEntity<Domain> getDomain(@PathVariable String domainName) {
        Optional<Domain> domainOptional = domainService.findDomainByName(domainName);
        if (!domainOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Domain domain = domainOptional.get();
        return new ResponseEntity<>(domain, HttpStatus.OK);
    }

    @RequestMapping(value = "{domainName}", method = PUT, consumes = "application/json", produces = "application/json")
    public ResponseEntity<Domain> updateDomain(@PathVariable String domainName, @RequestBody Domain newDomain) {
        if (!domainService.findDomainByName(domainName).isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Domain updatedDomain = domainService.updateDomain(domainName, newDomain);
        return new ResponseEntity<>(updatedDomain, HttpStatus.OK);
    }

    @RequestMapping(method = POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<Domain> createDomain(@RequestBody Domain newDomain) {
        if (domainService.findDomainByName(newDomain.getName()).isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        boolean isDomainCreated = domainService.createDomain(newDomain);
        if (isDomainCreated) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "{domainName}", method = DELETE)
    public ResponseEntity<Domain> deleteDomain(@PathVariable String domainName) {
        boolean isDomainDeleted = domainService.deleteDomain(domainName);
        if (isDomainDeleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ExceptionHandler(value = ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void validationExceptionHandler(ValidationException ve) {
        LOGGER.error("Got exception", ve);
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void exceptionHandler(Exception e) {
        LOGGER.error("Got exception", e);
    }
}
