package com.woowahan.market.forecast.direct;

import static com.woowahan.market.forecast.context.Utils.printMemory;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

  private int MAX_ROW_PER_SHEET = 500000;
  private int MAX_WINDOW_SIZE = 100;
  private String dbURL;
  private String masterUsername;
  private String masterUserPassword;
  private int limit;
  private String jobId;
  private int salesDays;
  private int forecastDays;
  private LocalDate salesStartDate;
  private LocalDate forecastStartDate;

  public DirectRedshiftClient(
      ExcelRequest excelRequest,
      String dbURL,
      String masterUsername,
      String masterUserPassword) {
    this.limit = excelRequest.getLimit();
    this.jobId = excelRequest.getJobId();
    this.salesDays = excelRequest.getSalesDays();
    this.forecastDays = excelRequest.getForecastDays();
    this.salesStartDate = LocalDate
        .parse(excelRequest.getSalesStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    this.forecastStartDate = LocalDate
        .parse(excelRequest.getForecastStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    this.dbURL = dbURL;
    this.masterUsername = masterUsername;
    this.masterUserPassword = masterUserPassword;
  }

  public SXSSFWorkbook generateExcel() throws SQLException {
    LambdaLogger logger = ContextHolder.getContext().getLogger();

    Connection conn = null;
    Statement stmt = null;
    try {
      logger.log("Connecting to database...");
      Properties props = new Properties();
      props.setProperty("user", masterUsername);
      props.setProperty("password", masterUserPassword);
      conn = DriverManager.getConnection(dbURL, props);
      stmt = conn.createStatement();

      String sql;
      sql =
          "select * from spectrum.excel where jobid='" + jobId
              + "' order by sku_code,center_code limit "
              + limit;
      ResultSet rs = stmt.executeQuery(sql);

      //edit temp path
      File dir = new File("/mnt/efs/temp");
      if (!dir.exists()) {
        dir.mkdir();
      }
      TempFile.setTempFileCreationStrategy(new DefaultTempFileCreationStrategy(dir));

      //Get the data from the result set.
      SXSSFWorkbook workbook = new SXSSFWorkbook(MAX_WINDOW_SIZE);
      Font font = workbook.createFont();
      CellStyle cellStyle = workbook.createCellStyle();
      cellStyle.setFont(font);

      AtomicInteger rowCount = new AtomicInteger(0);
      AtomicInteger sheetIndex = new AtomicInteger(0);

      List<SXSSFSheet> sheetList = new ArrayList<>();
      while (rs.next()) {
        if (rowCount.get() % MAX_ROW_PER_SHEET == 0) {
          sheetIndex.incrementAndGet();
          sheetList.add(generateNewSheet(workbook, sheetIndex.get(), cellStyle));
          rowCount.set(0);
        }

        if (rowCount.get() % 10000 == 0) {
          logger.log(String.format("Sheet %s, Rows %s", sheetIndex.get(), rowCount.get()));
          printMemory();
        }

        dateRow(sheetList.get((sheetIndex.get() - 1)), cellStyle, rowCount.get() + 2, rs);
        rowCount.incrementAndGet();
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

  private SXSSFSheet generateNewSheet(SXSSFWorkbook workbook, int sheetIndex,
      CellStyle cellStyle) {
    SXSSFSheet sheet = workbook.createSheet("수요예측" + sheetIndex);
    headerRow1(sheet, cellStyle);
    headerRow2(sheet, cellStyle);
    return sheet;
  }

  private void headerRow1(SXSSFSheet sheet, CellStyle cellStyle) {
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
    mergeCell(cell2, salesDays);
    offset = offset + salesDays;

    SXSSFCell cell3 = header.createCell(offset, CellType.STRING);
    cell3.setCellValue("완전품절 적용");
    cell3.setCellStyle(cellStyle);
    mergeCell(cell3, salesDays);
    offset = offset + salesDays;

    SXSSFCell cell4 = header.createCell(offset, CellType.STRING);
    cell4.setCellValue("과거 기획전 판매량 반영");
    cell4.setCellStyle(cellStyle);
    mergeCell(cell4, salesDays);
    offset = offset + salesDays;

    SXSSFCell cell5 = header.createCell(offset, CellType.STRING);
    cell5.setCellValue("최근 주문수 기준 과거 주문수 보정");
    cell5.setCellStyle(cellStyle);
    mergeCell(cell5, salesDays);
    offset = offset + salesDays;

    SXSSFCell cell6 = header.createCell(offset, CellType.STRING);
    cell6.setCellValue("최대최소 치 제거 예측 수량");
    cell6.setCellStyle(cellStyle);
    mergeCell(cell6, forecastDays);
    offset = offset + forecastDays;

    SXSSFCell cell7 = header.createCell(offset, CellType.STRING);
    cell7.setCellValue("사업팀 예상 주문수 반영");
    cell7.setCellStyle(cellStyle);
    mergeCell(cell7, forecastDays);
    offset = offset + forecastDays;

    SXSSFCell cell8 = header.createCell(offset, CellType.STRING);
    cell8.setCellValue("향후 기획전 MD 의지치 반영");
    cell8.setCellStyle(cellStyle);
    mergeCell(cell8, forecastDays);
  }

  private void headerRow2(SXSSFSheet sheet, CellStyle cellStyle) {
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
      IntStream.range(0, salesDays).forEach(i -> {
            SXSSFCell cell = header.createCell(index.get(), CellType.STRING);
            cell.setCellValue(salesStartDate.plusDays(i)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            cell.setCellStyle(cellStyle);
            index.incrementAndGet();
          }
      );
    });

    IntStream.range(0, 3).forEach(step_index -> {
      IntStream.range(0, forecastDays).forEach(i -> {
            SXSSFCell cell = header.createCell(index.get(), CellType.STRING);
            cell.setCellValue(forecastStartDate.plusDays(i)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            cell.setCellStyle(cellStyle);
            index.incrementAndGet();
          }
      );
    });
  }

  private void dateRow(SXSSFSheet sheet, CellStyle cellStyle, int rowIndex, ResultSet rs)
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
      int offset = 7 + (salesStepColumnIndex * salesDays);
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
        int days = salesStartDate
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
      int offset = 7 + (4 * salesDays) + (forecastStepColumnIndex * forecastDays);
      String stepData;
      try {
        stepData = rs.getString(forecastStepColumns[forecastStepColumnIndex]);
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }

      String[] dateAndQtyList = stepData.split(";");
      Arrays.asList(dateAndQtyList).forEach(dateAndQty -> {

        String[] split = dateAndQty.split(",");
        String date = split[0];
        int days = forecastStartDate
            .until(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))).getDays();

        SXSSFCell cell = row.createCell(offset + days, CellType.NUMERIC);
        cell.setCellValue(split.length > 1 ? Integer.parseInt(split[1]) : 0);
        cell.setCellStyle(cellStyle);
      });
    });
  }

  private void mergeCell(Cell cell, int cols) {
    CellRangeAddress cellRangeAddress = new CellRangeAddress(
        cell.getRow().getRowNum(), cell.getRow().getRowNum(),
        cell.getColumnIndex(), (cell.getColumnIndex() + cols - 1));
    cell.getSheet().addMergedRegion(cellRangeAddress);
  }
}
