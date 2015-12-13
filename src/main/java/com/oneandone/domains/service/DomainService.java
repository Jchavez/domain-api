package com.oneandone.domains.service;

import com.oneandone.domains.model.Domain;
import com.oneandone.domains.validator.DomainValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DomainService {

    @Resource
    private Map<String, Domain> configuredDomains;

    @Autowired
    private DomainValidator domainValidator;

    public Set<Domain> findAll() {
        return configuredDomains.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toSet());
    }

    public Optional<Domain> findDomainByName(String domainName) {
        return Optional.ofNullable(configuredDomains.get(domainName));
    }

    public Domain updateDomain(String domainName, Domain newDomain) {
        domainValidator.validate(domainName, newDomain);
        return configuredDomains.put(domainName, newDomain);
    }

    public boolean createDomain(Domain newDomain) {
        domainValidator.validate(newDomain);
        configuredDomains.put(newDomain.getName(), newDomain);
        return true;
    }

    public boolean deleteDomain(String domainName) {
        if (configuredDomains.containsKey(domainName)) {
            configuredDomains.remove(domainName);
            return true;
        }
        return false;
    }
}
