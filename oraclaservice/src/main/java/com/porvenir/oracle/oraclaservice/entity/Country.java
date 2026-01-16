package com.porvenir.oracle.oraclaservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "COUNTRIES")
@Data
public class Country {

    @Id
    @Column(name = "COUNTRY_ID", length = 2, nullable = false)
    private String countryId;

    @Column(name = "COUNTRY_NAME", length = 60)
    private String countryName;

    @Column(name = "REGION_ID")
    private Long regionId;

}
