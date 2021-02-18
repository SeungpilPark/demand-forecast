package com.woowahan.market.forecast.direct;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExcelRequest {

  private int limit;
  private String jobId;
  private int salesDays;
  private int forecastDays;
  private String salesStartDate;
  private String forecastStartDate;
}
