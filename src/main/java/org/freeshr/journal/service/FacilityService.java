package org.freeshr.journal.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import org.freeshr.journal.infrastructure.WebClient;
import org.freeshr.journal.launch.ApplicationProperties;
import org.freeshr.journal.utils.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.freeshr.journal.utils.HttpUtil.AUTH_TOKEN_KEY;
import static org.freeshr.journal.utils.HttpUtil.CLIENT_ID_KEY;

@Component
public class FacilityService {
    private ApplicationProperties applicationProperties;
    
    @Autowired
    public FacilityService(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public Facility getFacility(String url) {
        Map<String, String> headers = new HashMap<>();
        headers.put("accept", "application/json");
        headers.put(AUTH_TOKEN_KEY, applicationProperties.getIdpAuthToken());
        headers.put(CLIENT_ID_KEY, applicationProperties.getIdpClientId());
        WebClient webClient = new WebClient(url, headers);
        try {
            String content = webClient.get("");
            return createFacility(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Facility createFacility(String content) throws IOException {
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
