package org.freeshr.journal.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import org.freeshr.journal.infrastructure.WebClient;
import org.freeshr.journal.launch.ApplicationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class FacilityService {
    @Autowired
    private ApplicationProperties applicationProperties;

    public Facility getFacility(String url) {
        Map<String, String> headers = new HashMap<>();
        headers.put("accept", "application/json");
        headers.put("X-Auth-Token", applicationProperties.getIdpAuthToken());
        WebClient webClient = new WebClient(url, headers);
        try {
            String content = webClient.get("");
            return createFacility(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    Facility createFacility(String content) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map facilityMap = objectMapper.readValue(content.getBytes(), Map.class);
        Facility facility = new Facility();
        facility.setFacilityId((String) facilityMap.get("id"));
        facility.setFacilityName((String) facilityMap.get("name"));
        LinkedHashMap properties = (LinkedHashMap) facilityMap.get("properties");
        facility.setFacilityType((String) properties.get("org_type"));
        List<String> catchments = (List<String>) properties.get("catchment");
        String catchmentsString = Joiner.on(",").join(catchments);
        facility.setCatchments(catchmentsString);
        facility.setFacilityLocation(getAddress(((LinkedHashMap) properties.get("locations"))));
        return facility;
    }

    private Address getAddress(LinkedHashMap linkedHashMap) {
        Address address = new Address();
        address.setDivision((String) linkedHashMap.get("division_code"));
        address.setDistrict((String) linkedHashMap.get("district_code"));
        address.setUpazila((String) linkedHashMap.get("upazila_code"));
        address.setCityCorporation((String) linkedHashMap.get("paurasava_code"));
        address.setUnionOrUrbanWardId((String) linkedHashMap.get("union_code"));
        return address;
    }



}
