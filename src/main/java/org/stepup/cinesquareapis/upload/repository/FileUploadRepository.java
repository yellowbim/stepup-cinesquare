package org.stepup.cinesquareapis.upload.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.stepup.cinesquareapis.upload.entity.UploadInfo;

import java.util.List;

@Repository
public interface FileUploadRepository extends JpaRepository<UploadInfo, Integer> {

    @Query("SELECT u.fileKey FROM UploadInfo u WHERE u.fileKey IN :movieIds")
    List<String> findAllByFileId(List<Integer> movieIds);
}
