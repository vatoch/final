package org.example.minio;

import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import io.minio.http.Method;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

public class MinioService {

    private static String BUCKET_NAME = "music";
    private static final Properties properties = new Properties();
    public static MinioClient getMinioClient() {

        return MinioClient.builder()
                .endpoint(properties.getProperty("minio.url"))
                .credentials(properties.getProperty("minio.access"),properties.getProperty("minio.secret"))
                .build();

    }

    static {
        try(InputStream input = MinioService.class.getClassLoader().getResourceAsStream("config.properties")) {
            properties.load(input);
        }catch (Exception e) {
            throw new RuntimeException();
        }
    }
    public  String getMinioUrl(String bucketName,String objectName) throws InternalException, ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException {
        return getMinioClient().getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().method(Method.GET).bucket(bucketName).object(objectName).build());
    }

    public static void prepareBucket()  {
        MinioClient minioClient = getMinioClient();
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(BUCKET_NAME).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(BUCKET_NAME).build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
