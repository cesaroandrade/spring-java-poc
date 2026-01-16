package com.porvenir.oracle.oraclaservice.controller;

import com.porvenir.oracle.oraclaservice.entity.Country;
import com.porvenir.oracle.oraclaservice.service.CountryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
public class CountryController {

    private final CountryService service;

    public CountryController(CountryService service) {
        this.service = service;
    }

    @GetMapping
    public List<Country> getAllCountries() {
        return service.findAll();
    }
}
