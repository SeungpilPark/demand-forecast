package com.woowahan.market.forecast.order;

import com.mz.poi.mapper.annotation.Cell;
import com.mz.poi.mapper.annotation.CellStyle;
import com.mz.poi.mapper.structure.CellType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
      cellType = CellType.NUMERIC,
      style = @CellStyle(
          dataFormat = "0"
      ),
      required = true
  )
  private int number;

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
  private String skuBarcode;

  @Cell(
      column = 3,
      cellType = CellType.STRING,
      style = @CellStyle(
          dataFormat = "@"
      )
  )
  private String skuCode;

  @Cell(
      column = 4,
      cellType = CellType.STRING,
      style = @CellStyle(
          dataFormat = "@"
      )
  )
  private String skuSupplierItemCode;

  @Cell(
      column = 5,
      cellType = CellType.NUMERIC,
      style = @CellStyle(
          dataFormat = "#,##0"
      )
  )
  private BigDecimal skuPrice;

  @Cell(
      column = 6,
      cellType = CellType.NUMERIC,
      style = @CellStyle(
          dataFormat = "0"
      )
  )
  private int orderedQuantity;

  @Cell(
      column = 7,
      cellType = CellType.NUMERIC,
      style = @CellStyle(
          dataFormat = "0"
      )
  )
  private int receivedQuantity;

  @Cell(
      column = 8,
      cellType = CellType.NUMERIC,
      style = @CellStyle(
          dataFormat = "0"
      )
  )
  private int skuQuantityOfBox;

  @Cell(
      column = 9,
      cellType = CellType.NUMERIC,
      style = @CellStyle(
          dataFormat = "0"
      )
  )
  private int receivedBoxQuantity;

  @Cell(
      column = 10,
      cellType = CellType.FORMULA,
      style = @CellStyle(
          dataFormat = "#,##0"
      )
  )
  private String totalAmountFormula = "product({{this.skuPrice}},{{this.receivedQuantity}})";

  @Cell(
      column = 11,
      cellType = CellType.DATE,
      style = @CellStyle(
          dataFormat = "yyyy.MM.dd"
      )
  )
  private LocalDateTime skuExpirationGuide;

  @Cell(
      column = 12,
      cellType = CellType.DATE,
      style = @CellStyle(
          dataFormat = "yyyy.MM.dd"
      )
  )
  private LocalDateTime skuManufacturedGuide;

  @Builder
  public ItemRow(int number, String skuName, String skuBarcode, String skuCode,
      String skuSupplierItemCode, BigDecimal skuPrice, int orderedQuantity, int receivedQuantity,
      int skuQuantityOfBox, int receivedBoxQuantity,
      LocalDateTime skuExpirationGuide, LocalDateTime skuManufacturedGuide) {
    this.number = number;
    this.skuName = skuName;
    this.skuBarcode = skuBarcode;
    this.skuCode = skuCode;
    this.skuSupplierItemCode = skuSupplierItemCode;
    this.skuPrice = skuPrice;
    this.orderedQuantity = orderedQuantity;
    this.receivedQuantity = receivedQuantity;
    this.skuQuantityOfBox = skuQuantityOfBox;
    this.receivedBoxQuantity = receivedBoxQuantity;
    this.skuExpirationGuide = skuExpirationGuide;
    this.skuManufacturedGuide = skuManufacturedGuide;
  }
}
