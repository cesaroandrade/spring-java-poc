package com.porvenir.oracle.oraclaservice.service;

import com.porvenir.oracle.oraclaservice.entity.Country;
import com.porvenir.oracle.oraclaservice.repository.CountryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryService {

    private final CountryRepository repository;

    public CountryService(CountryRepository repository) {
        this.repository = repository;
    }

    public List<Country> findAll() {
        return repository.findAll();
    }
}
