package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.config.AwsProperties;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {
    @Value("${discodeit.storage.s3.access-key}")
    private String accessKey;

    @Value("${discodeit.storage.s3.secret-key}")
    private String secretKey;

    @Value("${discodeit.storage.s3.region}")
    private String region;

    @Value("${discodeit.storage.s3.bucket}")
    private String bucket;

    private final S3Client s3Client;
    private final AwsProperties props;

    public S3BinaryContentStorage(S3Client s3Client, AwsProperties props) {
        this.s3Client = s3Client;
        this.props = props;
    }

    private S3Client getS3Client(){
        return s3Client;
    }

    @Override
    public UUID put(UUID binaryContentId, byte[] bytes, String fileName, String contentType) {
        try{
            String key = binaryContentId + getExtension(fileName);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(contentType)
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));

            return binaryContentId;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream get(UUID binaryContentId) {
        return null;
    }

    @Override
    public ResponseEntity<?> download(BinaryContentDto metaData) {
        return null;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    private String generatePresignedUrl(String key, String contentType){
        return null;
    }
}
