package com.woowahan.market.forecast;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowahan.market.forecast.context.ContextHolder;
import com.woowahan.market.forecast.direct.DirectAwsS3File;
import com.woowahan.market.forecast.direct.DirectRedshiftClient;
import java.util.Map;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<Map, Object> {


  public Object handleRequest(Map input, Context context) {
    ContextHolder.setContext(context);

    LambdaLogger logger = context.getLogger();
    try {
      logger.log(new ObjectMapper().writeValueAsString(input));

      DirectAwsS3File awsS3File = new DirectAwsS3File("mz.spectrumdb");
      awsS3File.createExcel(
          DirectRedshiftClient.connectCluster(
              System.getenv("RedshiftJdbcUrl"),
              System.getenv("RedshiftUser"),
              System.getenv("RedshiftPassword"),
              Integer.parseInt(System.getenv("RedshiftLimit"))
          )
      );
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
    return input;
  }
}
