package com.woowahan.market.forecast;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.woowahan.market.forecast.context.ContextHolder;
import com.woowahan.market.forecast.file.AwsS3File;
import java.io.IOException;
import java.util.Map;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<Map, Object> {


  public Object handleRequest(Map input, Context context) {
    ContextHolder.setContext(context);

    LambdaLogger logger = context.getLogger();
//    try {
//      RedshiftClient.connectCluster(
//          System.getenv("RedshiftJdbcUrl"),
//          System.getenv("RedshiftUser"),
//          System.getenv("RedshiftPassword")
//      );
//      logger.log(new ObjectMapper().writeValueAsString(input));
//
//    } catch (IOException | SQLException ex) {
//      throw new RuntimeException(ex);
//    }
    try {
      AwsS3File awsS3File = new AwsS3File("mz.spectrumdb");
      awsS3File.createExcel();
      awsS3File.saveExcel();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    return input;
  }
}
