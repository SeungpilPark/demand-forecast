package com.woowahan.market.forecast.direct;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.woowahan.market.forecast.context.ContextHolder;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.DefaultTempFileCreationStrategy;
import org.apache.poi.util.TempFile;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public class DirectRedshiftClient {

  public static int SALES_DAYS = 21;
  public static int FORECAST_DAYS = 14;
  public static LocalDate SALES_START_DATE = LocalDate.of(2021, 1, 15);
  public static LocalDate SALES_END_DATE = LocalDate.of(2021, 2, 4);

  public static LocalDate FORECAST_START_DATE = LocalDate.of(2021, 2, 5);
  public static LocalDate FORECAST_END_DATE = LocalDate.of(2021, 2, 18);

  public static SXSSFWorkbook connectCluster(String dbURL, String masterUsername,
      String masterUserPassword, int limit) throws SQLException {
    LambdaLogger logger = ContextHolder.getContext().getLogger();

    Connection conn = null;
    Statement stmt = null;
    try {
      //Open a connection and define properties.
      logger.log("Connecting to database...");
      Properties props = new Properties();

      //Uncomment the following line if using a keystore.
      //props.setProperty("ssl", "true");
      props.setProperty("user", masterUsername);
      props.setProperty("password", masterUserPassword);
      conn = DriverManager.getConnection(dbURL, props);
      conn.setAutoCommit(false);

      //Try a simple query.
      logger.log("Listing system tables...");
      stmt = conn.createStatement();
      stmt.setFetchSize(1000);

      String sql;
      sql =
          "select * from spectrum.excel where jobid='28921229-f194-4d03-97b8-266f152def8f' order by sku_code,center_code limit "
              + limit;
      ResultSet rs = stmt.executeQuery(sql);

      //edit temp path
      File dir = new File("/mnt/efs/temp");
      if (!dir.mkdirs()) {
        throw new RuntimeException("Failed to create efs temp dir");
      }
      TempFile.setTempFileCreationStrategy(new DefaultTempFileCreationStrategy(dir));

      //Get the data from the result set.
      SXSSFWorkbook workbook = new SXSSFWorkbook();
      Font font = workbook.createFont();
      CellStyle cellStyle = workbook.createCellStyle();
      cellStyle.setFont(font);
      SXSSFSheet sheet = workbook.createSheet("수요예측");

      headerRow1(sheet, cellStyle);
      headerRow2(sheet, cellStyle);

      AtomicInteger index = new AtomicInteger(2);
      while (rs.next()) {
        dateRow(sheet, cellStyle, index.get(), rs);
        index.incrementAndGet();
      }
      rs.close();
      stmt.close();
      conn.close();
      return workbook;
    } finally {
      if (stmt != null) {
        stmt.close();
      }
      if (conn != null) {
        conn.close();
      }
    }
  }

  private static void headerRow1(SXSSFSheet sheet, CellStyle cellStyle) {
    int offset = 0;

    SXSSFRow header = sheet.createRow(0);
    SXSSFCell cell1 = header.createCell(offset, CellType.STRING);
    cell1.setCellValue("sku 정보");
    cell1.setCellStyle(cellStyle);
    mergeCell(cell1, 7);
    offset = offset + 7;

    SXSSFCell cell2 = header.createCell(offset, CellType.STRING);
    cell2.setCellValue("판매수량");
    cell2.setCellStyle(cellStyle);
    mergeCell(cell2, SALES_DAYS);
    offset = offset + SALES_DAYS;

    SXSSFCell cell3 = header.createCell(offset, CellType.STRING);
    cell3.setCellValue("완전품절 적용");
    cell3.setCellStyle(cellStyle);
    mergeCell(cell3, SALES_DAYS);
    offset = offset + SALES_DAYS;

    SXSSFCell cell4 = header.createCell(offset, CellType.STRING);
    cell4.setCellValue("과거 기획전 판매량 반영");
    cell4.setCellStyle(cellStyle);
    mergeCell(cell4, SALES_DAYS);
    offset = offset + SALES_DAYS;

    SXSSFCell cell5 = header.createCell(offset, CellType.STRING);
    cell5.setCellValue("최근 주문수 기준 과거 주문수 보정");
    cell5.setCellStyle(cellStyle);
    mergeCell(cell5, SALES_DAYS);
    offset = offset + SALES_DAYS;

    SXSSFCell cell6 = header.createCell(offset, CellType.STRING);
    cell6.setCellValue("최대최소 치 제거 예측 수량");
    cell6.setCellStyle(cellStyle);
    mergeCell(cell6, FORECAST_DAYS);
    offset = offset + FORECAST_DAYS;

    SXSSFCell cell7 = header.createCell(offset, CellType.STRING);
    cell7.setCellValue("사업팀 예상 주문수 반영");
    cell7.setCellStyle(cellStyle);
    mergeCell(cell7, FORECAST_DAYS);
    offset = offset + FORECAST_DAYS;

    SXSSFCell cell8 = header.createCell(offset, CellType.STRING);
    cell8.setCellValue("향후 기획전 MD 의지치 반영");
    cell8.setCellStyle(cellStyle);
    mergeCell(cell8, FORECAST_DAYS);
  }

  private static void headerRow2(SXSSFSheet sheet, CellStyle cellStyle) {
    SXSSFRow header = sheet.createRow(1);

    AtomicInteger index = new AtomicInteger(0);
    String[] skuInfos = new String[]{
        "sku 코드", "sku 이름", "보관방법", "거래처", "거래처MD", "센터코드", "센터이름"
    };
    Arrays.asList(skuInfos).forEach(name -> {
      SXSSFCell cell = header.createCell(index.get(), CellType.STRING);
      cell.setCellValue(name);
      cell.setCellStyle(cellStyle);
      index.incrementAndGet();
    });

    IntStream.range(0, 4).forEach(step_index -> {
      IntStream.range(0, SALES_DAYS).forEach(i -> {
            SXSSFCell cell = header.createCell(index.get(), CellType.STRING);
            cell.setCellValue(SALES_START_DATE.plusDays(i)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            cell.setCellStyle(cellStyle);
            index.incrementAndGet();
          }
      );
    });

    IntStream.range(0, 3).forEach(step_index -> {
      IntStream.range(0, FORECAST_DAYS).forEach(i -> {
            SXSSFCell cell = header.createCell(index.get(), CellType.STRING);
            cell.setCellValue(FORECAST_START_DATE.plusDays(i)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            cell.setCellStyle(cellStyle);
            index.incrementAndGet();
          }
      );
    });
  }

  private static void dateRow(SXSSFSheet sheet, CellStyle cellStyle, int rowIndex, ResultSet rs)
      throws SQLException {
    SXSSFRow row = sheet.createRow(rowIndex);

    String[] skuInfoColumns = new String[]{
        "sku_code", "sku_name", "storage_method", "supplier_name", "supplier_md_name",
        "center_code", "center_name"
    };

    for (int i = 0; i < skuInfoColumns.length; i++) {
      SXSSFCell cell = row.createCell(i, CellType.STRING);
      cell.setCellValue(rs.getString(skuInfoColumns[i]));
      cell.setCellStyle(cellStyle);
    }

    String[] salesStepColumns = new String[]{
        "step10_qty", "step30_qty", "step40_qty", "step50_qty"
    };
    IntStream.range(0, salesStepColumns.length).forEach(salesStepColumnIndex -> {
      int offset = 7 + (salesStepColumnIndex * SALES_DAYS);
      String stepData = null;
      try {
        stepData = rs.getString(salesStepColumns[salesStepColumnIndex]);
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }

      String[] dateAndQtyList = stepData.split(";");
      Arrays.asList(dateAndQtyList).forEach(dateAndQty -> {

        String[] split = dateAndQty.split(",");
        String date = split[0];
        int days = SALES_START_DATE
            .until(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))).getDays();

        SXSSFCell cell = row.createCell(offset + days, CellType.NUMERIC);
        cell.setCellValue(split.length > 1 ? Integer.parseInt(split[1]) : 0);
        cell.setCellStyle(cellStyle);

      });
    });

    String[] forecastStepColumns = new String[]{
        "step60_qty", "step70_qty", "step80_qty"
    };
    IntStream.range(0, forecastStepColumns.length).forEach(forecastStepColumnIndex -> {
      int offset = 7 + (4 * SALES_DAYS) + (forecastStepColumnIndex * FORECAST_DAYS);
      String stepData = null;
      try {
        stepData = rs.getString(salesStepColumns[forecastStepColumnIndex]);
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }

      String[] dateAndQtyList = stepData.split(";");
      Arrays.asList(dateAndQtyList).forEach(dateAndQty -> {

        String[] split = dateAndQty.split(",");
        String date = split[0];
        int days = FORECAST_START_DATE
            .until(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))).getDays();

        SXSSFCell cell = row.createCell(offset + days, CellType.NUMERIC);
        cell.setCellValue(split.length > 1 ? Integer.parseInt(split[1]) : 0);
        cell.setCellStyle(cellStyle);
      });
    });
  }

  private static void mergeCell(Cell cell, int cols) {
    CellRangeAddress cellRangeAddress = new CellRangeAddress(
        cell.getRow().getRowNum(), cell.getRow().getRowNum(),
        cell.getColumnIndex(), (cell.getColumnIndex() + cols - 1));
    //merge cell
    cell.getSheet().addMergedRegion(cellRangeAddress);
  }
}
