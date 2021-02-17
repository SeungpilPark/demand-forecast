package com.woowahan.market.forecast;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowahan.market.forecast.context.ContextHolder;
import com.woowahan.market.forecast.excel.ItemRow;
import com.woowahan.market.forecast.file.AwsS3File;
import com.woowahan.market.forecast.normal.NormalFile;
import com.woowahan.market.forecast.redshift.RedshiftClient;
import java.util.ArrayList;
import java.util.Map;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<Map, Object> {


  public Object handleRequest(Map input, Context context) {
    ContextHolder.setContext(context);

    LambdaLogger logger = context.getLogger();
    try {
      ArrayList<ItemRow> itemRows = RedshiftClient.connectCluster(
          System.getenv("RedshiftJdbcUrl"),
          System.getenv("RedshiftUser"),
          System.getenv("RedshiftPassword"),
          Integer.parseInt(System.getenv("RedshiftLimit"))
      );
      logger.log(new ObjectMapper().writeValueAsString(input));
      logger.log("itemRows: " + itemRows.size());

      AwsS3File awsS3File = new AwsS3File("mz.spectrumdb");
      awsS3File.createExcel(itemRows);
//      NormalFile normalFile = new NormalFile("mz.spectrumdb");
//      normalFile.createExcel(itemRows);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
    return input;
  }
}
