package com.woowahan.market.forecast.order;

import com.mz.poi.mapper.annotation.CellStyle;
import com.mz.poi.mapper.annotation.DataRows;
import com.mz.poi.mapper.annotation.Font;
import com.mz.poi.mapper.annotation.Header;
import com.mz.poi.mapper.annotation.Match;
import com.mz.poi.mapper.annotation.Row;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

@Getter
@Setter
@NoArgsConstructor
public class OrderSheet {

  @Row(
      row = 0,
      heightInPoints = 40,
      defaultStyle = @CellStyle(
          borderTop = BorderStyle.NONE,
          borderRight = BorderStyle.NONE,
          borderLeft = BorderStyle.NONE,
          borderBottom = BorderStyle.NONE
      )
  )
  TitleRow titleRow;

  @DataRows(
      row = 1,
      match = Match.REQUIRED,
      headerStyle = @CellStyle(
          font = @Font(bold = true),
          alignment = HorizontalAlignment.CENTER
      ),
      headers = {
          @Header(column = 0, cols = 7, name = "거래처 정보"),
          @Header(column = 7, cols = 6, name = "발주처 정보")
      }
  )
  List<FirstInfoRow> firstInfos;

  @Row(
      rowAfter = "firstInfos"
  )
  BlankRow blankRow = new BlankRow();

  @DataRows(
      rowAfter = "blankRow",
      match = Match.REQUIRED,
      hideHeader = true
  )
  List<SecondInfoRow> secondInfos;
  @Row(
      rowAfter = "secondInfos"
  )
  SummaryRow summaryRow = new SummaryRow();

  @DataRows(
      rowAfter = "summaryRow",
      match = Match.REQUIRED,
      headerStyle = @CellStyle(
          font = @Font(bold = true),
          alignment = HorizontalAlignment.CENTER
      ),
      headers = {
          @Header(column = 0, name = "순서"),
          @Header(column = 1, name = "상품명"),
          @Header(column = 2, name = "바코드"),
          @Header(column = 3, name = "B마트코드"),
          @Header(column = 4, name = "거래처 상품 코드"),
          @Header(column = 5, name = "매입단가"),
          @Header(column = 6, name = "총 발주 수량"),
          @Header(column = 7, name = "총 입고 수량"),
          @Header(column = 8, name = "박스 입수량"),
          @Header(column = 9, name = "입고 박스 수량"),
          @Header(column = 10, name = "발주 금액 (입고 기준)"),
          @Header(column = 11, name = "유통기한 가이드"),
          @Header(column = 12, name = "제조일 가이드"),
      }
  )
  List<ItemRow> items;

  @Builder
  public OrderSheet(
      TitleRow titleRow,
      List<FirstInfoRow> firstInfos,
      List<SecondInfoRow> secondInfos,
      SummaryRow summaryRow,
      List<ItemRow> items) {
    this.titleRow = titleRow;
    this.firstInfos = firstInfos;
    this.secondInfos = secondInfos;
    this.summaryRow = summaryRow;
    this.items = items;
  }
}
