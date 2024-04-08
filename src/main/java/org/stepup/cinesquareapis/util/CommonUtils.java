package org.stepup.cinesquareapis.util;

import org.joda.time.LocalDate;
import org.springframework.http.ContentDisposition;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class CommonUtils {

    private static final String CATEGORY_PREFIX = "/";
    private static final String TIME_SEPARATOR = "_";
    private static final int UNDER_BAR_INDEX = 1;
    private static final String FILE_EXTENSION_SEPARATOR = ".";

    // category > year > month > day > fileName
    public static String buildFileName(String category, String originalFileName) {
        int fileExtensionIndex = originalFileName.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        String fileExtension = originalFileName.substring(fileExtensionIndex);
        String fileName = UUID.randomUUID().toString();
        String now = String.valueOf(System.currentTimeMillis());

        // 파일 업로드 날짜 생성 (년/월/일 폴더로 구분하여 넣기 위함)
        LocalDate uploadDate = LocalDate.now();

        return category + CATEGORY_PREFIX + uploadDate.getYear() + CATEGORY_PREFIX + uploadDate.getMonthOfYear() + CATEGORY_PREFIX + uploadDate.getDayOfMonth() + CATEGORY_PREFIX + fileName + TIME_SEPARATOR + now + fileExtension;
    }

    public static ContentDisposition createContentDisposition(String categoryWithFileName) {
        String fileName = categoryWithFileName.substring(
                categoryWithFileName.lastIndexOf(CATEGORY_PREFIX) + UNDER_BAR_INDEX);
        return ContentDisposition.builder("attachment")
                .filename(fileName, StandardCharsets.UTF_8)
                .build();
    }

    /**
     * UUID 생성
     */
    public String makeUuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * UUID 파일명 반환
     */
    public static String getUuidFileName(String fileName) {
        return UUID.randomUUID().toString();
    }
}
