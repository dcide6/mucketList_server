package com.siksaurus.yamstack.review.s3upload;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Uploader {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile multipartFile, String dirName) throws IOException {
        if (multipartFile.isEmpty()) {
            return "NO FILE";
        }else {
            File uploadFile = convert(multipartFile)
                    .orElseThrow(()-> new IllegalArgumentException("MultipartFile -> File로 전환이 실패했습니다."));
            return upload(uploadFile, dirName);
        }
    }

    public String upload(File uploadFile, String dirName) {
        String fileNmae = dirName + "/"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyHHmmss"))
                + ((int) (Math.random() * 900000) + 100000);
        String uploadImageUrl = putS3(uploadFile, fileNmae);
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }

    public boolean delete(String uploadImagePath) {
        boolean deleted = false;
        boolean imageExist = amazonS3Client.doesObjectExist(bucket, uploadImagePath);
        if (imageExist){
            amazonS3Client.deleteObject(bucket, uploadImagePath);
            deleted = true;
        }else{
            deleted = false;
        }
        return deleted;
    }

    private String putS3(File uploadFile,String fileNname) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileNname, uploadFile)
                                    .withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileNname).toString();
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("파일이 삭제되었습니다.");
        } else {
            log.info("파일이 삭제되지 못했습니다.");
        }
    }
    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(file.getOriginalFilename());
        if(convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }

        return Optional.empty();
    }
}
