package it.giorgiaauroraadorni.booktique.model;

import javax.persistence.*;

@Entity
@Table(name="addresses")
public class Address extends AuditModel {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    // Street Name + House Number
    @Column(nullable = false)
    private String streetAddress;

    private String building;

    @Column(length = 189, nullable = false)
    private String city;

    // Province Abbreviation
    @Column(length = 2, nullable = false)
    private String province;

    @Column(length = 90)
    private String region;

    @Column(length = 18, nullable = false)
    private String postalCode;

    @Column(length = 90, nullable = false)
    private String country;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
