package com.bugsnag;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

//import javax.annotation.Nonnull;

//import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;

public class LogbackAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

  private Client client;

  private String apiKey;

  private String releaseStage;

  private boolean installHandler = true;

  public String getApiKey() {
    return apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  public String getReleaseStage() {
    return releaseStage;
  }

  public void setReleaseStage(String releaseStage) {
    this.releaseStage = releaseStage;
  }

  public boolean getInstallHandler() {
    return installHandler;
  }

  public void setInstallHandler(boolean installHandler) {
    this.installHandler = installHandler;
  }

  @Override
  public void start() {
//    checkState(apiKey != null, "apiKey required");
//    checkState(releaseStage != null, "releaseStage required");

    super.start();

    // FIXME: once bugsnag is fixed we don't need this: https://github.com/bugsnag/bugsnag-java/issues/10
    if (Thread.getDefaultUncaughtExceptionHandler() == null) {
      Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
          System.err.printf("Exception in thread \"%s\" ", t.getName());
          e.printStackTrace(System.err);
        }
      });
    }

    client = new Client(apiKey, installHandler);
    client.setReleaseStage(releaseStage);
    client.setUseSSL(true);
  }

  @Override
  protected void append(ILoggingEvent event) {
    IThrowableProxy throwableProxy = event.getThrowableProxy();

    if (throwableProxy != null) {
      notify(event.getLevel(), event.getFormattedMessage(), throwableProxy);
    }
  }

  private void notify(Level level, String message, IThrowableProxy throwableProxy) {
    MetaData metaData = new MetaData();
    metaData.addToTab("logging", "level", level.toString());
    metaData.addToTab("logging", "message", message);

    client.notify(rebuildThrowable(throwableProxy), level.toString(), metaData);
  }

  private Throwable rebuildThrowable(/*@Nonnull*/ IThrowableProxy proxy) {
    if (proxy instanceof ThrowableProxy) {
      return ((ThrowableProxy) proxy).getThrowable();
    }

    Throwable cause = null;

    if (proxy.getCause() != null) {
      cause = rebuildThrowable(proxy.getCause());
    }

    String message = format("%s: %s", proxy.getClassName(), proxy.getMessage());
    Throwable throwable = new Throwable(message, cause);
    throwable.setStackTrace(getCallData(proxy));

    return throwable;
  }

  private static StackTraceElement[] getCallData(IThrowableProxy proxy) {
    StackTraceElementProxy[] elements = proxy.getStackTraceElementProxyArray();
    StackTraceElement[] callData = new StackTraceElement[elements.length];

    for (int i = 0; i < elements.length; i++) {
      StackTraceElementProxy element = elements[i];
      callData[i] = element.getStackTraceElement();
    }

    return callData;
  }
}
