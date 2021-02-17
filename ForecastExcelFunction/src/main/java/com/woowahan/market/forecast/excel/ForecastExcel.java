package com.woowahan.market.forecast.excel;

import com.mz.poi.mapper.annotation.CellStyle;
import com.mz.poi.mapper.annotation.Excel;
import com.mz.poi.mapper.annotation.Font;
import com.mz.poi.mapper.annotation.Sheet;
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
public class ForecastExcel {

  @Sheet(
      name = "수요예측",
      index = 0,
      defaultColumnWidth = 20
  )
  private ForecastSheet sheet;

  @Builder
  public ForecastExcel(ForecastSheet sheet) {
    this.sheet = sheet;
  }
}
