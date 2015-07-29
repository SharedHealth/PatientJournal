package org.freeshr.journal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.freeshr.journal.infrastructure.WebClient;
import org.freeshr.journal.launch.ApplicationProperties;
import org.freeshr.journal.model.Provider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.freeshr.journal.utils.HttpUtil.AUTH_TOKEN_KEY;
import static org.freeshr.journal.utils.HttpUtil.CLIENT_ID_KEY;

@Component
public class ProviderService {
    private ApplicationProperties applicationProperties;

    private Logger logger = Logger.getLogger(ProviderService.class);

    @Autowired
    public ProviderService(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public Provider getProvider(String url) {
        logger.debug(String.format("Fetching Provider Details from [%s]", url));
        Map<String, String> headers = new HashMap<>();
        headers.put("accept", "application/json");
        headers.put(AUTH_TOKEN_KEY, applicationProperties.getIdpAuthToken());
        headers.put(CLIENT_ID_KEY, applicationProperties.getIdpClientId());
        WebClient webClient = new WebClient(url, headers);
        try {
            String content = webClient.get("");
            return createProvider(content);
        } catch (IOException e) {
            logger.error("unable to fetch provider", e);
        }
        return null;
    }

    private Provider createProvider(String content) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map providerMap = objectMapper.readValue(content.getBytes(), Map.class);
        Provider provider = new Provider();
        provider.setId(providerMap.get("id").toString());
        provider.setName((String) providerMap.get("name"));
        Map organization = (Map) providerMap.get("organization");
        if (organization != null) {
            String reference = (String) organization.get("reference");
            provider.setFacilityId(parseUrl(reference));
        }
        return provider;
    }

    private String parseUrl(String referenceUrl) {
        String s = StringUtils.substringAfterLast(referenceUrl, "/");
        return StringUtils.substringBefore(s, ".json");

    }
}
