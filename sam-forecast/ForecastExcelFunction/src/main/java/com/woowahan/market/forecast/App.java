package com.woowahan.market.forecast;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<Map, Object> {


  public Object handleRequest(Map input, Context context) {
    LambdaLogger logger = context.getLogger();
    try {
      logger.log(new ObjectMapper().writeValueAsString(input));
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return input;
  }
}
