package com.woowahan.market.forecast.order;

import com.mz.poi.mapper.annotation.CellStyle;
import com.mz.poi.mapper.annotation.ColumnWidth;
import com.mz.poi.mapper.annotation.Excel;
import com.mz.poi.mapper.annotation.Font;
import com.mz.poi.mapper.annotation.Sheet;
import com.mz.poi.mapper.structure.ColumnWidthAnnotation;
import com.mz.poi.mapper.structure.DataRowsAnnotation;
import com.mz.poi.mapper.structure.ExcelStructure;
import com.mz.poi.mapper.structure.ExcelStructure.CellStructure;
import com.mz.poi.mapper.structure.ExcelStructure.SheetStructure;
import com.mz.poi.mapper.structure.HeaderAnnotation;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.VerticalAlignment;

@Getter
@Setter
@NoArgsConstructor
@Excel(
    defaultStyle = @CellStyle(
        verticalAlignment = VerticalAlignment.CENTER,
        font = @Font(
            fontName = "Arial",
            fontHeightInPoints = 10),
        borderTop = BorderStyle.THIN,
        borderRight = BorderStyle.THIN,
        borderLeft = BorderStyle.THIN,
        borderBottom = BorderStyle.THIN
    )
)
public class OrderExcel {

  @Sheet(
      name = "PMS 발주서",
      index = 0,
      defaultColumnWidth = 15,
      columnWidths = {
          @ColumnWidth(column = 0, width = 8),
          @ColumnWidth(column = 1, width = 25),
          @ColumnWidth(column = 2, width = 15),
          @ColumnWidth(column = 3, width = 13),
          @ColumnWidth(column = 4, width = 13),
          @ColumnWidth(column = 5, width = 11),
          @ColumnWidth(column = 6, width = 11),
          @ColumnWidth(column = 7, width = 11),
          @ColumnWidth(column = 8, width = 11),
          @ColumnWidth(column = 9, width = 13),
          @ColumnWidth(column = 10, width = 15),
          @ColumnWidth(column = 11, width = 13),
          @ColumnWidth(column = 12, width = 13)
      }
  )
  private OrderSheet sheet;

  @Builder
  public OrderExcel(OrderSheet sheet) {
    this.sheet = sheet;
  }

  public static ExcelStructure getWarehousingAndClosedByCancelExcelStructure() {
    ExcelStructure excelStructure = new ExcelStructure().build(OrderExcel.class);
    SheetStructure sheet = excelStructure.getSheet("sheet");

    DataRowsAnnotation items = (DataRowsAnnotation) sheet.getRow("items").getAnnotation();
    List<HeaderAnnotation> headerAnnotations = items.getHeaders().stream()
        .peek(headerAnnotation -> {
          int column = headerAnnotation.getColumn();
          if (column == 10) {
            headerAnnotation.setName("발주 금액");
          }
        }).collect(Collectors.toList());
    items.setHeaders(headerAnnotations);
    return excelStructure;
  }

  public static ExcelStructure getBeforeClosedExcelStructure() {
    ExcelStructure excelStructure = new ExcelStructure().build(OrderExcel.class);
    SheetStructure sheet = excelStructure.getSheet("sheet");
    //reduce sheet column width
    List<ColumnWidthAnnotation> columnWidthAnnotationList = sheet.getAnnotation().getColumnWidths()
        .stream()
        .filter(columnWidthAnnotation -> columnWidthAnnotation.getColumn() != 7)
        .peek(columnWidthAnnotation -> {
          int column = columnWidthAnnotation.getColumn();
          if (column > 7) {
            columnWidthAnnotation.setColumn(column - 1);
          }
        }).collect(Collectors.toList());
    sheet.getAnnotation().setColumnWidths(columnWidthAnnotationList);

    //reduce title row column
    sheet.getRow("titleRow").getCell("title").getAnnotation().setCols(12);

    DataRowsAnnotation firstInfos = (DataRowsAnnotation) sheet.getRow("firstInfos").getAnnotation();
    firstInfos.getHeaders().get(0).setCols(6);
    firstInfos.getHeaders().get(1).setColumn(6);

    sheet.getRow("firstInfos").getCell("supplierValue").getAnnotation().setCols(4);
    sheet.getRow("firstInfos").getCell("ordererText").getAnnotation().setColumn(6);
    sheet.getRow("firstInfos").getCell("ordererValue").getAnnotation().setColumn(8);

    sheet.getRow("blankRow").getCell("blank").getAnnotation().setCols(12);

    sheet.getRow("secondInfos").getCell("supplierValue").getAnnotation().setCols(4);
    sheet.getRow("secondInfos").getCell("ordererText").getAnnotation().setColumn(6);
    sheet.getRow("secondInfos").getCell("ordererValue").getAnnotation().setColumn(8);

    sheet.getRow("summaryRow").getCell("formula").getAnnotation().setCols(10);

    DataRowsAnnotation items = (DataRowsAnnotation) sheet.getRow("items").getAnnotation();
    List<HeaderAnnotation> headerAnnotations = items.getHeaders().stream()
        .filter(headerAnnotation -> headerAnnotation.getColumn() != 7)
        .peek(headerAnnotation -> {
          int column = headerAnnotation.getColumn();
          if (column == 9) {
            headerAnnotation.setName("입고예정 박스수랑");
          }
          if (column == 10) {
            headerAnnotation.setName("발주 금액");
          }
          if (column > 7) {
            headerAnnotation.setColumn(column - 1);
          }
        }).collect(Collectors.toList());
    items.setHeaders(headerAnnotations);

    List<CellStructure> cells = sheet.getRow("items").getCells().stream()
        .filter(cellStructure -> cellStructure.getAnnotation().getColumn() != 7)
        .peek(cellStructure -> {
          int column = cellStructure.getAnnotation().getColumn();
          if (column > 7) {
            cellStructure.getAnnotation().setColumn(column - 1);
          }
        }).collect(Collectors.toList());
    sheet.getRow("items").setCells(cells);

    return excelStructure;
  }
}
