package com.porvenir.oracle.oraclaservice.repository;

import com.porvenir.oracle.oraclaservice.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, String> {
}
