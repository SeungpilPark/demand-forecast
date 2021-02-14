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
public class SecondInfoRow {

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
  private String supplierText;

  @Cell(
      column = 2,
      cols = 5,
      cellType = CellType.STRING,
      required = true
  )
  private String supplierValue;

  @Cell(
      column = 7,
      cols = 2,
      cellType = CellType.STRING,
      style = @CellStyle(
          font = @Font(bold = true),
          alignment = HorizontalAlignment.CENTER
      ),
      ignoreParse = true
  )
  private String ordererText;

  @Cell(
      column = 9,
      cols = 4,
      cellType = CellType.STRING,
      required = true
  )
  private String ordererValue;

  @Builder
  public SecondInfoRow(String supplierText, String supplierValue, String ordererText,
      String ordererValue) {
    this.supplierText = supplierText;
    this.supplierValue = supplierValue;
    this.ordererText = ordererText;
    this.ordererValue = ordererValue;
  }
}
