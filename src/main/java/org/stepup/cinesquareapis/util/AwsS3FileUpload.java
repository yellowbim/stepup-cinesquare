package org.stepup.cinesquareapis.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
public class AwsS3FileUpload {

    @Autowired
    private AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    /**
     * 특정 카테고리 구분
     * @param category (UP: user profile, MA : movie actor)
     * @param multipartFile
     * @return String (500, url)
     */
    public String uploadFileV1(String category, MultipartFile multipartFile) {
        // 파일 존재 여부 확인
        if (multipartFile.isEmpty()) {
            return "500";
        }

        // 파일명 생성
        String fileName = CommonUtils.buildFileName(category, multipartFile.getOriginalFilename());

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            log.error("FileUploadFailedException");
            return "500";
        }

        return amazonS3Client.getUrl(bucketName, fileName).toString();
    }

    /**
     * 특정 카테고리 구분
     * @param category (UP: user profile, MA : movie actor)
     * @param multipartFile
     * @return String (500, url)
     */
    public void deleteFileV1(String profilePath) {
        try {
            boolean isObjectExist = amazonS3Client.doesObjectExist(bucketName, profilePath);
            if (isObjectExist) {
                amazonS3Client.deleteObject(bucketName, profilePath);
            } else {
                log.error("기존 사용자 프로필 삭제 실패");
            }
        } catch (Exception e) {
            log.error("Delete File failed", e);
        }
    }
}
