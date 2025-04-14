package com.java.oauth2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CustomOAuth2User implements OAuth2User {

  private String issuer;
  private String name;
  private String email;
  private int id;

  @Override
  public Map<String, Object> getAttributes() {
    return null;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return null;
  }

  @Override
  public String getName() {
    return this.name;
  }

  public String getIssuer() {
    return this.issuer;
  }

  public int getId() {
    return this.id;
  }
  
  public String getEmail() {
	  return this.email;
  }

}
