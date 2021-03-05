package com.woowahan.market.forecast;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowahan.market.forecast.context.ContextHolder;
import com.woowahan.market.forecast.direct.DirectAwsS3File;
import com.woowahan.market.forecast.direct.DirectRedshiftClient;
import com.woowahan.market.forecast.direct.ExcelRequest;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<ExcelRequest, Object> {


  public Object handleRequest(ExcelRequest excelRequest, Context context) {
    ContextHolder.setContext(context);

    LambdaLogger logger = context.getLogger();
    try {
      logger.log(new ObjectMapper().writeValueAsString(excelRequest));

      DirectAwsS3File awsS3File = new DirectAwsS3File(System.getenv("ForecastBucket"));
      awsS3File.uploadExcel(
          new DirectRedshiftClient(
              excelRequest,
              System.getenv("RedshiftJdbcUrl"),
              System.getenv("RedshiftUser"),
              System.getenv("RedshiftPassword")
          ).generateExcel(),
          excelRequest.getJobId()
      );
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
    return excelRequest;
  }
}
