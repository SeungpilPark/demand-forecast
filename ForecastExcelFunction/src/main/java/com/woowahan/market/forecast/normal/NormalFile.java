package com.woowahan.market.forecast.normal;

import com.woowahan.market.forecast.excel.ItemRow;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.util.DefaultTempFileCreationStrategy;
import org.apache.poi.util.TempFile;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class NormalFile {

  public static final String FILE_NAME = "fileName";
  private final S3Client s3Client;
  private final String bucketName;

  public NormalFile(String bucketName) {
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

    File dir = new File("/mnt/efs/temp");
    dir.mkdir();
    TempFile.setTempFileCreationStrategy(new DefaultTempFileCreationStrategy(dir));

    SXSSFWorkbook workbook = new SXSSFWorkbook(100);
    SXSSFSheet sheet = workbook.createSheet("sheet1");

    Font font = workbook.createFont();
    font.setFontName("Arial");
    CellStyle cellStyle = workbook.createCellStyle();
    cellStyle.setFont(font);

    AtomicInteger index = new AtomicInteger(0);
    items.forEach(item -> {
      SXSSFRow row = sheet.createRow(index.get());
      SXSSFCell cell0 = row.createCell(0, CellType.STRING);
      SXSSFCell cell1 = row.createCell(1, CellType.STRING);
      SXSSFCell cell2 = row.createCell(2, CellType.STRING);
      SXSSFCell cell3 = row.createCell(3, CellType.STRING);
      SXSSFCell cell4 = row.createCell(4, CellType.STRING);
      SXSSFCell cell5 = row.createCell(5, CellType.STRING);
      SXSSFCell cell6 = row.createCell(6, CellType.STRING);
      SXSSFCell cell7 = row.createCell(7, CellType.STRING);

      cell0.setCellStyle(cellStyle);
      cell0.setCellValue(item.getSkuCode());

      cell1.setCellStyle(cellStyle);
      cell1.setCellValue(item.getSkuName());

      cell2.setCellStyle(cellStyle);
      cell2.setCellValue(item.getStorageMethod());

      cell3.setCellStyle(cellStyle);
      cell3.setCellValue(item.getSupplierName());

      cell4.setCellStyle(cellStyle);
      cell4.setCellValue(item.getSupplierMdName());

      cell5.setCellStyle(cellStyle);
      cell5.setCellValue(item.getCenterCode());

      cell6.setCellStyle(cellStyle);
      cell6.setCellValue(item.getCenterName());

      cell7.setCellStyle(cellStyle);
      cell7.setCellValue(item.getData());

      index.incrementAndGet();
    });

    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    workbook.write(out);
    out.close();

    String fileName = "forecast.xlsx";
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
