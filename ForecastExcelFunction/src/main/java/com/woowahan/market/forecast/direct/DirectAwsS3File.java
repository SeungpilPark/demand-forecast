package com.woowahan.market.forecast.direct;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
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

  public void uploadExcel(SXSSFWorkbook workbook, String jobId)
      throws IOException {

    String fileName = jobId + ".xlsx";
    File file = new File("/mnt/efs/temp/" + fileName);
    FileOutputStream fos = new FileOutputStream(file);
    workbook.write(fos);
    fos.close();

    //create s3 path
    String path = "forecast/excel/" + fileName;
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
