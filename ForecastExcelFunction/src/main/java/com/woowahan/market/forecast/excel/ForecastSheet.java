package com.woowahan.market.forecast.excel;

import com.mz.poi.mapper.annotation.CellStyle;
import com.mz.poi.mapper.annotation.DataRows;
import com.mz.poi.mapper.annotation.Font;
import com.mz.poi.mapper.annotation.Header;
import com.mz.poi.mapper.annotation.Match;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

@Getter
@Setter
@NoArgsConstructor
public class ForecastSheet {

  @DataRows(
      row = 0,
      match = Match.STOP_ON_BLANK,
      headerStyle = @CellStyle(
          font = @Font(bold = true),
          alignment = HorizontalAlignment.CENTER
      ),
      headers = {
          @Header(column = 0, name = "sku 코드"),
          @Header(column = 1, name = "sku 이름"),
          @Header(column = 2, name = "보관방법"),
          @Header(column = 3, name = "거래처"),
          @Header(column = 4, name = "거래처MD"),
          @Header(column = 5, name = "센터코드"),
          @Header(column = 6, name = "센터이름"),
          @Header(column = 7, name = "예측데이터")
      }
  )
  List<ItemRow> items;

  @Builder
  public ForecastSheet(List<ItemRow> items) {
    this.items = items;
  }
}
