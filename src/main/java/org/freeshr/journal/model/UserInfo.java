package org.freeshr.journal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

//TODO: To use apache CollectionsUtils

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfo {
    public static final String SHR_FACILITY_GROUP = "SHR_FACILITY";
    public static final String SHR_USER_GROUP = "SHR USER";
    public static final String SHR_PROVIDER_GROUP = "SHR_PROVIDER";
    public static final String SHR_PATIENT_GROUP = "SHR_PATIENT";
    public static final String FACILITY_ADMIN_GROUP = "Facility Admin";
    public static final String DATASENSE_FACILITY_GROUP = "Datasense Facility";

    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("email")
    private String email;
    @JsonProperty("is_active")
    private int isActive;
    @JsonProperty("activated")
    private boolean activated;
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("groups")
    private List<String> groups;
    @JsonProperty("profiles")
    private List<UserProfile> userProfiles;


    public UserInfo() {
    }

    public UserInfo(String id, String name, String email, int isActive, boolean activated, String accessToken, List<String> groups,
                    List<UserProfile> userProfiles) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.isActive = isActive;
        this.activated = activated;
        this.accessToken = accessToken;
        this.groups = groups;
        this.userProfiles = userProfiles;
    }

    public String getName() {
        return name;
    }

    public List<String> getGroups() {
        return groups;
    }

    public String getId() {
        return id;
    }

    public List<UserProfile> getUserProfiles() {
        return userProfiles;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public boolean isActivated() {
        return activated;
    }

    public int getIsActive() {
        return isActive;
    }

    public String getEmail() {
        return email;
    }


    public UserProfile getPatientProfile() {
        List<UserProfile> userProfiles = getUserProfiles();
        for (UserProfile userProfile : userProfiles) {
            if (userProfile.isPatient()) {
                return userProfile;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserInfo userInfo = (UserInfo) o;

        if (!email.equals(userInfo.email)) return false;
        if (!id.equals(userInfo.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + email.hashCode();
        return result;
    }
}