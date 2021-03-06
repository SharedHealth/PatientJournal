package org.freeshr.journal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Patient {

    @JsonProperty("hid")
    private String healthId;

    @JsonProperty("present_address")
    private Address address;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("given_name")
    private String givenName;

    @JsonProperty("sur_name")
    private String surName;


    public String getName() {
        return StringUtils.isBlank(surName) ? givenName : givenName + " " + surName;
    }


    public String getHealthId() {
        return healthId;
    }

    public void setHealthId(String healthId) {
        this.healthId = healthId;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getGender() {
        if ("M".equalsIgnoreCase(gender))
            return "Male";
        if ("F".equalsIgnoreCase(gender))
            return "Female";
        if ("O".equalsIgnoreCase(gender))
            return "Other";
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Patient patient = (Patient) o;

        return this.healthId.equals(patient.healthId);
    }

    @Override
    public int hashCode() {
        return healthId.hashCode();
    }

    @Override
    public String toString() {
        return "Patient{" +
                "healthId='" + healthId + '\'' +
                ", address=" + address +
                ", gender='" + gender + '\'' +
                '}';
    }

}

