package com.woowahan.market.forecast.context;

import com.amazonaws.services.lambda.runtime.Context;
import java.util.Objects;
import org.springframework.util.Assert;

public class ContextHolder {

  private ContextHolder() {
  }

  private static final ThreadLocal<Context> threadLocal = new ThreadLocal<>();

  public static void setContext(Context context) {
    Assert.notNull(context, "context cannot be null");
    threadLocal.set(context);
  }

  public static Context getContext() {
    return threadLocal.get();
  }

  public static void clearSupplierContext() {
    threadLocal.remove();
  }

  public static boolean hasSupplierContext() {
    return Objects.nonNull(getContext());
  }
}
