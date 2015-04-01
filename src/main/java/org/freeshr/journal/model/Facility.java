package org.freeshr.journal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.join;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Facility {

    private String facilityId;

    private String facilityName;

    private List<String> catchments = new ArrayList<>();

    private String facilityType;

    private Address facilityLocation;

    public String getFacilityType() {
        return facilityType;
    }

    public void setFacilityType(String facilityType) {
        this.facilityType = facilityType;
    }

    public Address getFacilityLocation() {
        return facilityLocation;
    }

    public void setFacilityLocation(Address facilityLocation) {
        this.facilityLocation = facilityLocation;
    }

    public void setCatchments(String commaSeparatedCatchments) {
        String[] catchments = commaSeparatedCatchments.split(",");
        for (String catchment : catchments) {
            this.catchments.add(catchment.trim());
        }
    }

    public Facility() {
    }

    //Only for tests
    public Facility(String facilityId, String facilityName, String facilityType, String catchments, Address location) {
        this.facilityId = facilityId;
        this.facilityName = facilityName;
        this.facilityType = facilityType;
        this.setFacilityLocation(location);
        setCatchments(catchments);
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }


    public String getFacilityId() {
        return facilityId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public String getCatchmentsAsCommaSeparatedString() {
        return join(catchments, ",");
    }

    public List<String> getCatchments() {
        return this.catchments;
    }

}