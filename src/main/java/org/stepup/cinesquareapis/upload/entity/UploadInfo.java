package org.stepup.cinesquareapis.upload.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="tb_upload_info")
public class UploadInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer fileId;

    @Comment("파일 전체 경로")
    @Column(name = "file_path", length = 500)
    private String filePath;

    @Comment("서버 파일 경로")
    @Column(name = "file_key", length = 500)
    private String fileKey;

    @Comment("파일명")
    @Column(name = "file_name", length = 300)
    private String fileName;

    @Comment("파일 타입")
    @Column(name = "file_type", length = 10)
    private String fileType;

    @ColumnDefault("0")
    @Comment("파일 사이즈")
    @Column(name = "file_size", length = 10)
    private String fileSize;

    @Comment("생성 일시")
    @CreationTimestamp
    @Column(name = "created", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime created;
}
