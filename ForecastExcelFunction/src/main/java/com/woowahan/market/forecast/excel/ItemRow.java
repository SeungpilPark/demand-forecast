package com.woowahan.market.forecast.excel;

import com.mz.poi.mapper.annotation.Cell;
import com.mz.poi.mapper.annotation.CellStyle;
import com.mz.poi.mapper.structure.CellType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ItemRow {

  @Cell(
      column = 0,
      cellType = CellType.STRING,
      style = @CellStyle(
          dataFormat = "@"
      )
  )
  private String skuCode;

  @Cell(
      column = 1,
      cellType = CellType.STRING,
      style = @CellStyle(
          dataFormat = "@"
      )
  )
  private String skuName;

  @Cell(
      column = 2,
      cellType = CellType.STRING,
      style = @CellStyle(
          dataFormat = "@"
      )
  )
  private String storageMethod;

  @Cell(
      column = 3,
      cellType = CellType.STRING,
      style = @CellStyle(
          dataFormat = "@"
      )
  )
  private String supplierName;

  @Cell(
      column = 4,
      cellType = CellType.STRING,
      style = @CellStyle(
          dataFormat = "@"
      )
  )
  private String supplierMdName;

  @Cell(
      column = 5,
      cellType = CellType.STRING,
      style = @CellStyle(
          dataFormat = "@"
      )
  )
  private String centerCode;

  @Cell(
      column = 6,
      cellType = CellType.STRING,
      style = @CellStyle(
          dataFormat = "@"
      )
  )
  private String centerName;

  @Cell(
      column = 7,
      cellType = CellType.STRING,
      style = @CellStyle(
          dataFormat = "@"
      )
  )
  private String data;

  @Builder
  public ItemRow(String skuCode, String skuName, String storageMethod, String supplierName,
      String supplierMdName, String centerCode, String centerName, String data) {
    this.skuCode = skuCode;
    this.skuName = skuName;
    this.storageMethod = storageMethod;
    this.supplierName = supplierName;
    this.supplierMdName = supplierMdName;
    this.centerCode = centerCode;
    this.centerName = centerName;
    this.data = data;
  }
}
