package com.yashny.realestate_backend.services;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.messages.Part;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final MinioClient minioClient;

    public List<String> uploadImages(List<InputStream> imageStreams, List<String> imageNames) throws Exception {
        List<String> imageUrls = new ArrayList<>();
        String bucketName = "realestate";

        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }

        for (int i = 0; i < imageStreams.size(); i++) {
            InputStream stream = imageStreams.get(i);
            String objectName = imageNames.get(i);

            long streamSize = stream.available();

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(stream, streamSize, -1)
                    .contentType("image/jpeg")
                    .build());

            String imageUrl = String.format("http://localhost:9000/%s/%s", bucketName, objectName);
            imageUrls.add(imageUrl);
        }

        return imageUrls;
    }

    public String uploadImage(MultipartFile file) throws Exception {
        InputStream imageStream = file.getInputStream();
        String imageName = file.getOriginalFilename();
        String bucketName = "realestate";

        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }

        long streamSize = imageStream.available();

        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(imageName)
                .stream(imageStream, streamSize, -1)
                .contentType("image/jpeg")
                .build());

        return String.format("http://localhost:9000/%s/%s", bucketName, imageName);
    }
}
