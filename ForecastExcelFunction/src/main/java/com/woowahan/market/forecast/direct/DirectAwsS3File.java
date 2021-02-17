package com.woowahan.market.forecast.direct;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class DirectAwsS3File {

  private final S3Client s3Client;
  private final String bucketName;

  public DirectAwsS3File(String bucketName) {
    this.s3Client =
        S3Client.builder()
            .httpClient(
                ApacheHttpClient.builder()
                    .maxConnections(100)
                    .build()
            )
            .region(Region.AP_NORTHEAST_2).build();
    this.bucketName = bucketName;
  }

  public void createExcel(SXSSFWorkbook workbook)
      throws IOException {

    String randomFileName = UUID.randomUUID().toString() + ".xlsx";
    File file = new File("/mnt/efs/temp/" + randomFileName);
    FileOutputStream fos = new FileOutputStream(file);
    workbook.write(fos);
    fos.close();

    String fileName = "forecast.xlsx";
    //create s3 path
    String path = "test/" + fileName;
    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucketName)
        .key(path)
        .contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sh")
        .contentDisposition("attachment; filename=" + fileName)
        .build();
    s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));
    file.deleteOnExit();
  }
}
