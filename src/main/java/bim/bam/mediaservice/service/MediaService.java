package bim.bam.mediaservice.service;

import bim.bam.mediaservice.config.MinioConfig;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    @SneakyThrows
    public String uploadFile(MultipartFile file) {
        String bucket = minioConfig.getBucket();
        String fileName = generateFileName(file);

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder()
                    .stream(inputStream, inputStream.available(), -1)
                    .bucket(bucket)
                    .object(fileName)
                    .build());
        }
        return fileName;
    }

    @SneakyThrows
    public String getFileUrl(String fileName) {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket(minioConfig.getBucket())
                        .method(Method.GET)
                        .object(fileName)
                        .expiry(2, TimeUnit.DAYS)
                        .build()
        );
    }

    @SneakyThrows
    public ByteArrayResource downloadFile(String fileName) {
        return new ByteArrayResource(IOUtils.toByteArray(minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minioConfig.getBucket())
                        .object(fileName)
                        .build()
        )));
    }

    private String generateFileName(MultipartFile file) {
        String extension = getExtension(file);
        return UUID.randomUUID() + "." + extension;
    }

    private String getExtension(MultipartFile file) {

        return Objects.requireNonNull(file.getOriginalFilename())
                .substring(file.getOriginalFilename().lastIndexOf(".") + 1);
    }
}
