package org.stepup.cinesquareapis.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.stepup.cinesquareapis.common.exception.enums.CustomErrorCode;
import org.stepup.cinesquareapis.common.exception.exception.RestApiException;
import org.stepup.cinesquareapis.upload.service.FileUploadService;
import org.stepup.cinesquareapis.user.entity.User;
import org.stepup.cinesquareapis.user.model.UpdateUserRequest;
import org.stepup.cinesquareapis.user.model.UserResponse;
import org.stepup.cinesquareapis.user.repository.UserRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;
    private final PasswordEncoder encoder;

    @Transactional
    public void deleteUser(Integer userId) {
        userRepository.findById(userId).orElseThrow(() -> new RestApiException(CustomErrorCode.NOT_FOUND_USER));

        userRepository.deleteById(userId);
    }

    /**
     * account 존재 여부 확인
     *
     * @param account
     * @return result
     */
    public boolean checkAccount(String account) {
        Boolean result = userRepository.existsByAccount(account);

        return result;
    }

    /**
     *  User 조회
     *
     * @param userId
     * @return
     */
    public UserResponse getUser(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(CustomErrorCode.NOT_FOUND_USER));

        return new UserResponse(user);
    }

    /**
     * User 수정
     *
     * @param userId, request
     * @return
     */
    @Transactional
    public UserResponse updateUser(int userId, UpdateUserRequest request) {
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(CustomErrorCode.NOT_FOUND_USER));

        // 요청에서 변경된 정보만 업데이트
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(encoder.encode(request.getPassword()));
        }
        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }
        if (request.getNickname() != null && !request.getNickname().isBlank()) {
            user.setNickname(request.getNickname());
        }
        // TODO: 모두 null 이면 오류 처리

        // 회원 정보 저장
        User updatedUser = userRepository.save(user);

        return new UserResponse(updatedUser);
    }

    /**
     * 사용자 프로필 사진 업로드
     * @return
     */
    @Transactional
    public UserResponse profileUpload(MultipartFile multipartFile, Integer userId) throws Exception {
        try {
            // MultipartFile을 BufferedImage로 변환
            BufferedImage bufferedImage = ImageIO.read(multipartFile.getInputStream());
            if (bufferedImage == null) {
                throw new IOException("이미지를 읽을 수 없습니다.");
            }
            if (bufferedImage.getColorModel().hasAlpha()) {
                BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
                newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);
                bufferedImage = newBufferedImage; // 투명도 제거
            }

            // jpg로 변환
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                ImageIO.write(bufferedImage, "jpg", outputStream);
            } finally {
                outputStream.close();
            }

            // 새 파일로 변환
            MultipartFile convertedMultipartFile = fileUploadService.createMultipartFile(outputStream.toByteArray(), "thumbnail.jpg");

            // 새 프로필 이미지 디렉토리
            String directory = "users/" + userId + "/images/";

            // S3 이미지 업로드
            fileUploadService.uploadFileV2(directory, convertedMultipartFile);

            // S3 이미지 정보 DB 저장
            fileUploadService.setUploadInfo(convertedMultipartFile, directory+"thumbnail.jpg");

            // DB 저장
            userRepository.updateImageByUserId(userId, directory+"thumbnail.jpg");

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RestApiException(CustomErrorCode.NOT_FOUND_USER));

            return new UserResponse(user);

        }
        catch (IOException e) {
            throw new Exception("이미지 저장 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
