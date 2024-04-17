package org.stepup.cinesquareapis.upload.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.stepup.cinesquareapis.movie.repository.MovieRepository;
import org.stepup.cinesquareapis.upload.entity.UploadInfo;
import org.stepup.cinesquareapis.upload.repository.FileUploadRepository;
import org.stepup.cinesquareapis.user.repository.UserRepository;
import org.stepup.cinesquareapis.util.AwsS3FileUpload;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadService {

    @Autowired
    private AwsS3FileUpload awsS3FileUpload;

    @Value("${cloud.aws.s3.url}")
    private String awsUrl;

    private final FileUploadRepository fileUploadRepository;

    private final UserRepository userRepository;

    private final MovieRepository movieRepository;

    /**
     * 사용자 프로필 사진 업로드
     * @return
     */
    @Transactional
    public Boolean userProfileUpload(String category, MultipartFile multipartFile, Integer userId) {

        // 기존 사용자 profile 정보 조회
//        String profilePath = userRepository.findUserProfile(userId);
//
//        String uploadResult = awsS3FileUpload.uploadFileV1(category, multipartFile);
//
//        if ("500".equals(uploadResult)) {
//            return false;
//        }
//
//        // 단건 DB Insert
//        Integer uResult = setUploadInfo(multipartFile, uploadResult);
//
//        // 기존 사용자 이미지 삭제
//        if (profilePath != null) {
//            awsS3FileUpload.deleteFileV1(profilePath);
//        }
//
//        // 사용자 테이블 upload
//        userRepository.updateProfileByUserId(userId, uResult);

        return true;
    }

    // updatePosterIds 사용하지 않으므로 에러 발생 -> 주석
//    /**
//     * 영화 포스터 업로드(다중)
//     * @return
//     */
//    @Transactional
//    public Boolean moviePosterUpload(String category, MultipartFile[] multipartFile, Integer movieId) throws JsonProcessingException {
//
//        List<Integer> newPosterIds = new ArrayList<>();
//
//        // 기존 영화 포스터 url 확인
//        String curPosterIds = movieRepository.findPosterUrlByMovieId(movieId);
//
//        // 영화 포스터 업로드
//        for (int i = 0; i < multipartFile.length; i++) {
//            String uploadResult = awsS3FileUpload.uploadFileV1(category, multipartFile[i]);
//            if ("500".equals(uploadResult)) {
//                return false;
//            }
//
//            // 단건 DB insert
//            Integer uResult = setUploadInfo(multipartFile[i], uploadResult);
//
//            newPosterIds.add(uResult);
//        }
//
//        // 업로드 이력이 있으면 수정
//        if (curPosterIds.length()> 1) {
//            // JSON 문자열을 List<Integer>로 파싱
//            ObjectMapper mapper = new ObjectMapper();
//            List<Integer> integerList = mapper.readValue(curPosterIds, new TypeReference<List<Integer>>() {});
//            List<String> fileKeyList = fileUploadRepository.findAllByFileId(integerList);
//
//            for (String key : fileKeyList) {
//                awsS3FileUpload.deleteFileV1(key);
//            }
//        }
//
//        // 영화테이블 upload
//        movieRepository.updatePosterIds(movieId, newPosterIds.toString());
//
//        return true;
//    }

    /**
     * 파일 업로드 정보 DB Insert (단건 return)
     */
    private Integer setUploadInfo(MultipartFile multipartFile, String uploadResult) {
        UploadInfo uploadInfo = new UploadInfo();
        uploadInfo.setFileName(multipartFile.getOriginalFilename());
        uploadInfo.setFilePath(uploadResult);

        String fileKey = uploadResult.replace(awsUrl, "");
        uploadInfo.setFileKey(fileKey.substring(1, fileKey.length()));
        Long bytes = multipartFile.getSize();
        String fileSize = "";
        if (bytes < 1024) {
            fileSize = bytes+"byte";
        } else if (bytes < 1048576) {
            fileSize = (bytes/1024)+"kb";
        } else {
            fileSize = (bytes/1024/1024)+"mb";
        }
        uploadInfo.setFileSize(fileSize);
        uploadInfo.setFileType(multipartFile.getName().substring(multipartFile.getName().lastIndexOf(".") + 1));

        return fileUploadRepository.save(uploadInfo).getFileId();
    }
}
