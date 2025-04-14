package com.java.oauth2.service;

import java.util.Objects;
import com.java.oauth2.common.Utils;
import com.java.oauth2.dto.FileDTO;
import com.java.oauth2.dto.FileResDTO;
import com.java.oauth2.entity.CardInfo;
import com.java.oauth2.entity.OAuthClient;
import com.java.oauth2.repository.CardInfoRepository;
import com.java.oauth2.repository.OAuthClientRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardServiceImp implements CardService {

    private final CardInfoRepository cardInfoRepository;
    private final OAuthClientRepository oAuthClientRepository;
    private final Utils utils;

    private final FileService fileService;

    // application.properties에서 호스팅 도메인 정보를 가져옴
    @Value("${hosting.uri}")
    private String hostingUri;

    @Override
    public CardInfo RegCardInfo(CardInfo cardInfo, HttpServletRequest request) {

        String userNo = utils.getUserNo(request);

        if(userNo != null) {
            cardInfo.setRegUserNo(Integer.parseInt(userNo));
            cardInfo.setUseYN('Y');
            CardInfo save_cardInfo = cardInfoRepository.save(cardInfo);

            if (save_cardInfo != null) {
                // 성공적으로 저장됨
                return save_cardInfo;
            } else {
                // 저장 실패
                // 처리 로직 추가
                return null;
            }
        }
        else {

            return null;
        }


    }

    @Override
    public boolean PictureRegCardInfo(MultipartFile userPictureFile, MultipartFile cardPictureFile, String name, String tel, HttpServletRequest request) {

        //name 과 tel 값이 없으면 패스
        if(name != null && !name.isEmpty() && tel != null && !tel.isEmpty()) {

            String userNo = utils.getUserNo(request);

            int fileUserNo = 0;
            int filePictureNo = 0;

            //명함프로필 사진파일정보 업로드
            if(userPictureFile!=null && userNo != null && !userNo.isEmpty()) {

                System.out.println("userPictureFile : " + userPictureFile);
                FileResDTO userPictureFileResDTO = fileService.upload(userPictureFile, Integer.parseInt(userNo));
                FileDTO userPictrueFileDTO = userPictureFileResDTO.getFile();
                fileUserNo = userPictrueFileDTO.getNo();

            }else {
                System.out.println("Nothing userPictureFile");
            }

            //명함 사진파일정보 업로드
            if(cardPictureFile!=null && userNo != null && !userNo.isEmpty()) {

                System.out.println("cardPictureFile : " + cardPictureFile);
                FileResDTO cardPictureFilefileResDTO = fileService.upload(cardPictureFile, Integer.parseInt(userNo));
                FileDTO cardPictureFileFileDTO = cardPictureFilefileResDTO.getFile();
                filePictureNo = cardPictureFileFileDTO.getNo();
            }else {
                System.out.println("Nothing cardPictureFile");
            }

            System.out.println("name : " + name);
            System.out.println("tel : " + tel);


            CardInfo cardInfo = CardInfo.builder()
                    .regUserNo(Integer.parseInt(userNo))
                    .useYN('Y')
                    .fileUserNo(fileUserNo)
                    .filePictureNo(filePictureNo)
                    .name(name)
                    .tel(tel)
                    .modDate(null) // 수정일자는 초반에 들어가지 않도록 수정.
                    .build();

            CardInfo save_cardInfo = cardInfoRepository.save(cardInfo);

            if (save_cardInfo != null) {
                // 성공적으로 저장됨
            } else {
                // 저장 실패
                // 처리 로직 추가
                return false;
            }


        }

        return true;
    }

    @Override
    public boolean EditDetailUpdateCardInfo(@RequestParam(value = "userPictureFile", required = false) MultipartFile userPictureFile,
                                            @RequestParam(value = "cardPictrueFile", required = false) MultipartFile cardPictrueFile,
                                            @RequestParam("cardNo") String cardNo,
                                            @RequestParam("company") String company,
                                            @RequestParam("position") String position,
                                            @RequestParam("name") String name,
                                            @RequestParam("adr") String adr,
                                            @RequestParam("email") String email,
                                            @RequestParam("tel") String tel,
                                            @RequestParam("fax") String fax,
                                            @RequestParam("memo") String memo,
                                            HttpServletRequest request) {

        //name 과 tel 값이 없으면  not null
        if(name != null && !name.isEmpty() && tel != null && !tel.isEmpty()) {

            String userNo = utils.getUserNo(request);

            int fileUserNo = 0;
            int filePictureNo = 0;
            boolean updateYN = false; //업데이트 필요시 아래로직에서 true check
            CardInfo save_cardInfo = null;

            //명함 유저 프로필 사진파일정보 업로드
            if(userPictureFile!=null && userNo != null && !userNo.isEmpty()) {

                System.out.println("userPictureFile : " + userPictureFile);
                FileResDTO userPictureFileResDTO = fileService.upload(userPictureFile, Integer.parseInt(userNo));
                FileDTO userPictrueFileDTO = userPictureFileResDTO.getFile();
                fileUserNo = userPictrueFileDTO.getNo();

            }else {
                System.out.println("Nothing userPictureFile");
            }

            //명함 사진파일정보 업로드
            if(cardPictrueFile!=null && userNo != null && !userNo.isEmpty()) {

                System.out.println("cardPictrueFile : " + cardPictrueFile);
                FileResDTO cardPictruefileResDTO = fileService.upload(cardPictrueFile, Integer.parseInt(userNo));
                FileDTO cardPictrueFileDTO = cardPictruefileResDTO.getFile();
                filePictureNo = cardPictrueFileDTO.getNo();
            }else {
                System.out.println("Nothing cardPictrueFile");
            }


            System.out.println("cardNo = " + cardNo + ", company = " + company + ", position = " + position + ", name = " + name + ", adr = " + adr + ", email = " + email + ", tel = " + tel + ", fax = " + fax + ", memo = " + memo + ", request = " + request);

            if(cardNo != null && !cardNo.isEmpty()) {
                CardInfo cardInfo = cardInfoRepository.findByNoAndUseYN(Integer.parseInt(cardNo), 'Y');

                if(fileUserNo != 0){
                    cardInfo.setFileUserNo(fileUserNo);
                    updateYN = true;
                }
                if(filePictureNo != 0){
                    cardInfo.setFilePictureNo(filePictureNo);
                    updateYN = true;
                }
                if(company != null && !company.isEmpty()) {
                    cardInfo.setCompany(company);
                    updateYN = true;
                }
                if(position != null && !position.isEmpty()) {
                    cardInfo.setPosition(position);
                    updateYN = true;
                }
                if(adr != null && !adr.isEmpty()) {
                    cardInfo.setAdr(adr);
                    updateYN = true;
                }
                if(email != null && !email.isEmpty()) {
                    cardInfo.setEmail(email);
                    updateYN = true;
                }
                if(fax != null && !fax.isEmpty()) {
                    cardInfo.setFax(fax);
                    updateYN = true;
                }
                if(memo != null && !memo.isEmpty()) {
                    cardInfo.setMemo(memo);
                    updateYN = true;
                }

                // 이름과 전화번호는 기존과 다르면 업데이트
                if(!cardInfo.getName().equals(name)){
                    cardInfo.setName(name);
                    updateYN = true;
                }
                if(!cardInfo.getTel().equals(tel)){
                    cardInfo.setTel(tel);
                    updateYN = true;
                }

                // 나머지도 기존과 다르면 저장
                if (!Objects.equals(cardInfo.getCompany(), company)) {
                    cardInfo.setCompany(company);
                    updateYN = true;
                }
                if (!Objects.equals(cardInfo.getPosition(), position)) {
                    cardInfo.setPosition(position);
                    updateYN = true;
                }
                if (!Objects.equals(cardInfo.getAdr(), adr)) {
                    cardInfo.setAdr(adr);
                    updateYN = true;
                }
                if (!Objects.equals(cardInfo.getEmail(), email)) {
                    cardInfo.setEmail(email);
                    updateYN = true;
                }
                if (!Objects.equals(cardInfo.getFax(), fax)) {
                    cardInfo.setFax(fax);
                    updateYN = true;
                }
                if (!Objects.equals(cardInfo.getMemo(), memo)) {
                    cardInfo.setMemo(memo);
                    updateYN = true;
                }

                // 업데이트 필요한 내용이 있는경우 업데이트
                if(updateYN) {
                    save_cardInfo = cardInfoRepository.save(cardInfo);
                }

                if (save_cardInfo != null || !updateYN) {
                    // 성공적으로 저장됨
                    return true;
                } else {
                    // 저장 실패
                    // 처리 로직 추가
                    return false;
                }
            }

        }

        return false;

    }


    @Override
    public boolean DelCardInfo(@RequestBody Map<String, Object> payload, HttpServletRequest request) {

        // payload에서 cardNo 값을 추출 (숫자 형태로 변환)
        int cardNo = Integer.parseInt(String.valueOf(payload.get("cardNo")));

        System.out.println("cardNo : " + cardNo);

        //cardNo에 해당괴는 로우 찾기
        CardInfo cardInfo = cardInfoRepository.findByNo(cardNo);
        //사용안한 N 셋팅
        cardInfo.setUseYN('N');
        //사용안함으로 업데이트
        cardInfo = cardInfoRepository.save(cardInfo);

        if(cardInfo != null) {

            return true;
        }

        return false;
    }


    @Override
    public ResponseEntity<?> getCardInfoDetail(String cardNo, Model model, HttpServletRequest request) {

        Map<String, Object> result = new HashMap<>();

        String userNo = utils.getUserNo(request);
        result.put("no", userNo);

        System.out.println("Detail cardNo : " + cardNo);

        CardInfo cardInfo = cardInfoRepository.findByNo(Integer.parseInt(cardNo));

        if (cardInfo != null) {
            // cardNo를 이용해 데이터 조회 (예제에서는 더미 데이터 사용)
//            model.addAttribute("message", "카드 정보 조회 성공");
//            model.addAttribute("cardInfo", cardInfo);
//            model.addAttribute("cardPictureUri", hostingUri + "/file/uri/");
            result.put("message", "카드 정보 조회 성공");
            result.put("cardInfo", cardInfo);
            result.put("cardPictureUri", "/file/uri/");
            result.put("status", true);

            //return "CardDetail";
            return ResponseEntity.ok(result);
        } else {

            //return "redirect:/"; // 메인 페이지로 리디렉트
            result.put("status", false);
            return ResponseEntity.ok(result);
        }

    }

    @Override
    public ResponseEntity<?> getCardInfoEdit(String cardNo, Model model) {

        Map<String, Object> result = new HashMap<>();
        
        System.out.println("Edit cardNo : " + cardNo);

        CardInfo cardInfo = cardInfoRepository.findByNo(Integer.parseInt(cardNo));

        if (cardInfo != null) {
            // cardNo를 이용해 데이터 조회 (예제에서는 더미 데이터 사용)
//            model.addAttribute("message", "카드 정보 조회 성공");
//            model.addAttribute("cardInfo", cardInfo);
//            model.addAttribute("cardPictureUri", hostingUri + "/file/uri/");
            result.put("message", "카드 정보 조회 성공");
            result.put("cardInfo", cardInfo);
            result.put("cardPictureUri", "/file/uri/");
            result.put("status", true);

            //return "CardEdit";
            return ResponseEntity.ok(result);

        } else {

            //return "redirect:/"; // 메인 페이지로 리디렉트
            result.put("status", false);
            return ResponseEntity.ok(result);

        }

    }

}
