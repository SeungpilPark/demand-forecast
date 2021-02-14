package com.woowahan.market.forecast.file;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.mz.poi.mapper.ExcelMapper;
import com.woowahan.market.forecast.context.ContextHolder;
import com.woowahan.market.forecast.order.FirstInfoRow;
import com.woowahan.market.forecast.order.ItemRow;
import com.woowahan.market.forecast.order.OrderExcel;
import com.woowahan.market.forecast.order.OrderSheet;
import com.woowahan.market.forecast.order.SecondInfoRow;
import com.woowahan.market.forecast.order.SummaryRow;
import com.woowahan.market.forecast.order.TitleRow;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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

  public void saveExcel() {
    String fileName = "after_closed_order.xlsx";
    LambdaLogger logger = ContextHolder.getContext().getLogger();

    //create s3 path
    String path = "test/" + fileName;
    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucketName)
        .key(path)
        .contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sh")
        .contentDisposition("attachment; filename=" + fileName)
        .build();

    File file = new File("after_closed_order.xlsx");
    s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));
  }

  public void createExcel() throws IOException {
    OrderExcel excelDto = OrderExcel.builder()
        .sheet(
            OrderSheet.builder()
                .titleRow(TitleRow.builder()
                    .title("마감 발주서(발주번호)")
                    .build())
                .firstInfos(
                    Stream.of(
                        FirstInfoRow.builder()
                            .supplierText("거래처명")
                            .supplierValue("A")
                            .ordererText("발주처명")
                            .ordererValue("B")
                            .build(),
                        FirstInfoRow.builder()
                            .supplierText("담당자")
                            .supplierValue("A")
                            .ordererText("담당MD")
                            .ordererValue("B")
                            .build(),
                        FirstInfoRow.builder()
                            .supplierText("담당자 이메일")
                            .supplierValue("A")
                            .ordererText("담당 MD 이메일")
                            .ordererValue("B")
                            .build(),
                        FirstInfoRow.builder()
                            .supplierText("담당자 연락처")
                            .supplierValue("A")
                            .ordererText("담당 MD 연락처")
                            .ordererValue("B")
                            .build()
                    ).collect(Collectors.toList())
                )
                .secondInfos(
                    Stream.of(
                        SecondInfoRow.builder()
                            .supplierText("발주 번호")
                            .supplierValue("A")
                            .ordererText("입고지 명 (입고지 주소)")
                            .ordererValue("B")
                            .build(),
                        SecondInfoRow.builder()
                            .supplierText("발주 등록일")
                            .supplierValue("2021-01-26")
                            .ordererText("입고지 연락처 (층)")
                            .ordererValue("B")
                            .build(),
                        SecondInfoRow.builder()
                            .supplierText("입고 예정일")
                            .supplierValue("2021-01-26")
                            .ordererText("보관 온도")
                            .ordererValue("C")
                            .build()
                    ).collect(Collectors.toList())
                )
                .summaryRow(
                    SummaryRow.builder()
                        .summaryTitle("총 발주 금액")
                        .build()
                )
                .items(
                    Stream.of(
                        ItemRow.builder()
                            .number(1)
                            .skuName("A")
                            .skuBarcode("A")
                            .skuCode("")
                            .skuSupplierItemCode("")
                            .skuPrice(BigDecimal.valueOf(1200))
                            .orderedQuantity(30)
                            .skuQuantityOfBox(10)
                            .receivedBoxQuantity(3)
                            .skuExpirationGuide(LocalDateTime.now())
                            .skuManufacturedGuide(LocalDateTime.now())
                            .build(),
                        ItemRow.builder()
                            .number(2)
                            .skuName("A")
                            .skuBarcode("A")
                            .skuCode("")
                            .skuSupplierItemCode("")
                            .skuPrice(BigDecimal.valueOf(2100))
                            .orderedQuantity(40)
                            .skuQuantityOfBox(10)
                            .receivedBoxQuantity(4)
                            .skuExpirationGuide(LocalDateTime.now())
                            .skuManufacturedGuide(LocalDateTime.now())
                            .build()
                    ).collect(Collectors.toList())
                )
                .build()
        )
        .build();

    XSSFWorkbook workbook = ExcelMapper.toExcel(excelDto);
    File file = new File("after_closed_order.xlsx");
    FileOutputStream fos = new FileOutputStream(file);
    workbook.write(fos);
    fos.close();
  }
}
