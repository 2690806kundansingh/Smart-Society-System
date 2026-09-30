package com.smartsociety.user.security.oauth2;

import com.smartsociety.user.entity.User;
import com.smartsociety.user.security.UserPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

public class OAuth2UserPrincipal extends UserPrincipal implements OAuth2User {

    private final Map<String, Object> attributes;
    private final String nameAttributeKey;

    public OAuth2UserPrincipal(User user, Map<String, Object> attributes, String nameAttributeKey) {
        super(user);
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        Object val = attributes.get(nameAttributeKey);
        return val != null ? String.valueOf(val) : getUsername();
    }
}
