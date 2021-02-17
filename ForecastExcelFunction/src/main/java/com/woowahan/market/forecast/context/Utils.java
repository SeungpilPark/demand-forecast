package com.woowahan.market.forecast.context;

import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class Utils {

  public static void printMemory() {
    LambdaLogger logger = ContextHolder.getContext().getLogger();

    long totalMemory = Runtime.getRuntime().totalMemory();
    long freeMemory = Runtime.getRuntime().freeMemory();
    long usage = totalMemory - freeMemory;

    logger.log(String.format("Total: %sMB, Free: %sMB, Usage: %sMB",
        Math.round(totalMemory / (1024 * 1024)), Math.round(freeMemory / (1024 * 1024)),
        Math.round(usage / (1024 * 1024))));
  }
}
