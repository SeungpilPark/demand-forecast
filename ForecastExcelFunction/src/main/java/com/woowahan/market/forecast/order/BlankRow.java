package com.woowahan.market.forecast.order;

import com.mz.poi.mapper.annotation.Cell;
import com.mz.poi.mapper.structure.CellType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BlankRow {

  @Cell(
      column = 0,
      cols = 13,
      cellType = CellType.BLANK,
      ignoreParse = true
  )
  private String blank;
}
