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
import org.apache.poi.ss.usermodel.VerticalAlignment;

@Getter
@Setter
@NoArgsConstructor
public class TitleRow {

  @Cell(
      column = 0,
      cols = 13,
      cellType = CellType.STRING,
      style = @CellStyle(
          font = @Font(
              fontHeightInPoints = 20,
              bold = true
          ),
          alignment = HorizontalAlignment.CENTER,
          verticalAlignment = VerticalAlignment.CENTER
      ),
      ignoreParse = true
  )
  private String title;

  @Builder
  public TitleRow(String title) {
    this.title = title;
  }
}
