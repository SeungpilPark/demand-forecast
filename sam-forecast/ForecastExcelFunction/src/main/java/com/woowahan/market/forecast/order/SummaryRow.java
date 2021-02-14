package com.woowahan.market.forecast.order;

import com.mz.poi.mapper.annotation.Cell;
import com.mz.poi.mapper.annotation.CellStyle;
import com.mz.poi.mapper.annotation.Font;
import com.mz.poi.mapper.structure.CellType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

@Getter
@Setter
@NoArgsConstructor
public class SummaryRow {

  @Cell(
      column = 0,
      cols = 2,
      cellType = CellType.STRING,
      style = @CellStyle(
          font = @Font(bold = true),
          alignment = HorizontalAlignment.CENTER
      ),
      ignoreParse = true
  )
  private String summaryTitle = "총 발주 금액";

  @Cell(
      column = 2,
      cols = 11,
      cellType = CellType.FORMULA,
      style = @CellStyle(
          font = @Font(bold = true),
          dataFormat = "#,##0",
          alignment = HorizontalAlignment.LEFT
      ),
      ignoreParse = true
  )
  private String formula = "SUM({{items.at(0).totalAmountFormula}}:{{items.last.totalAmountFormula}})";

  @Builder
  public SummaryRow(String summaryTitle) {
    this.summaryTitle = summaryTitle;
  }
}
