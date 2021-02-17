package com.woowahan.market.forecast.file;

import com.mz.poi.mapper.ExcelMapper;
import com.woowahan.market.forecast.excel.ForecastExcel;
import com.woowahan.market.forecast.excel.ForecastSheet;
import com.woowahan.market.forecast.excel.ItemRow;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class AwsS3File {

  public static final String FILE_NAME = "fileName";
  private final S3Client s3Client;
  private final String bucketName;

  public AwsS3File(String bucketName) {
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

  public void createExcel(ArrayList<ItemRow> items)
      throws IOException {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    ForecastExcel excelDto = ForecastExcel.builder()
        .sheet(
            ForecastSheet.builder()
                .items(items)
                .build()
        )
        .build();

    XSSFWorkbook workbook = ExcelMapper.toExcel(excelDto);
    workbook.write(out);
    out.close();

    String fileName = "com.woowahan.market.forecast.xlsx";
    //create s3 path
    String path = "test/" + fileName;
    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucketName)
        .key(path)
        .contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sh")
        .contentDisposition("attachment; filename=" + fileName)
        .build();
    s3Client.putObject(putObjectRequest, RequestBody.fromBytes(out.toByteArray()));
  }
}
