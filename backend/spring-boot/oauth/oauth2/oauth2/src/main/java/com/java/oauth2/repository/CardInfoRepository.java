package com.java.oauth2.repository;

import com.java.oauth2.entity.CardInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardInfoRepository extends JpaRepository<CardInfo, Integer> {

    // findAll()는 JpaRepository에 기본적으로 제공되는 메소드입니다.
    List<CardInfo> findAll();

    CardInfo findByNo(int no);

    CardInfo findByNoAndUseYN(int no, char useYN);

    // 추가적인 메소드를 필요에 따라 정의할 수 있습니다.
    List<CardInfo> findByUseYN(String useYN);

    List<CardInfo> findTop100ByRegUserNoAndUseYNOrderByNoDesc(int userNo, char userYN);

    // 기본적인 LIKE 검색 (Containing = "%값%")
    List<CardInfo> findTop100ByUseYNAndNameContainingOrCompanyContainingOrPositionContainingOrderByNoDesc(char useYN, String name, String company, String position);

}
