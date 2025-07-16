package com.otterdram.otterdram.common.geo.address;

import com.otterdram.otterdram.common.geo.city.City;
import com.otterdram.otterdram.common.geo.country.Country;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

    // Country
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    // City
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    // Address
    @Column(name = "address", length = 255)
    private String address;

    public Address(Country country, City city, String address) {
        this.country = country;
        this.city = city;
        this.address = address;
    }

}
