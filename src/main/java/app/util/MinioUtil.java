package app.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.FileNotFoundException;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;

public class MinioUtil {
    private static MinioClient getMinioClient() {
        String minioUrl = ConfigUtil.getProperty("minio.host");
        String accessKey = ConfigUtil.getProperty("minio.accessKey");
        String secretKey = ConfigUtil.getProperty("minio.secretKey");

        return MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(accessKey, secretKey)
                .build();
    }

    public static File downloadFile(String fileName) {
        MinioClient minioClient = getMinioClient();
        String bucketName = ConfigUtil.getProperty("minio.bucket");
        File file = new File("/data/" + fileName);

        try {
            // Verifique se o bucket existe, se não, crie-o
            boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!isExist) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            try (InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build());
                    FileOutputStream outputStream = new FileOutputStream(file)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }

    public static InputStream downloadFileAsStream(String fileName) throws Exception {
        MinioClient minioClient = getMinioClient();
        String bucketName = ConfigUtil.getProperty("minio.bucketName");

        // Verifique se o bucket existe, se não, crie-o
        boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!isExist) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }

        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object("data/" + fileName)
                            .build());
        } catch (Exception e) {
            throw new FileNotFoundException("File not found in MinIO: " + fileName);
        }
    }

    public static void uploadFile(String fileName, InputStream inputStream, long size, String contentType) {
        MinioClient minioClient = getMinioClient();
        String bucketName = ConfigUtil.getProperty("minio.bucketName");

        try {
            // Verifique se o bucket existe, se não, crie-o
            boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!isExist) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            // Verificar se o arquivo já existe
            try {
                minioClient.statObject(
                        StatObjectArgs.builder()
                                .bucket(bucketName)
                                .object(fileName)
                                .build());
                System.out.println("File already exists: " + bucketName + "/" + fileName);
                return; // Não fazer o upload
            } catch (Exception e) {
                // Arquivo não existe
            }

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(inputStream, size, -1)
                            .contentType(contentType)
                            .build());
            System.out.println("File uploaded successfully: " + bucketName + "/" + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteFile(String fileName) {
        MinioClient minioClient = getMinioClient();
        String bucketName = ConfigUtil.getProperty("minio.bucketName");

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build());
            System.out.println("File deleted successfully: " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}