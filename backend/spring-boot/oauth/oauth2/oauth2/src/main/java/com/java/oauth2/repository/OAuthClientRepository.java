package com.java.oauth2.repository;

import com.java.oauth2.entity.OAuthClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuthClientRepository extends JpaRepository<OAuthClient, Integer> {

    Optional<OAuthClient> findByEmailAndUseYN(String email, char useYN);

    OAuthClient findByOauthId(String oauthId);

    OAuthClient findByNoAndUseYN(int no, char useYN);

}
