package com.bugsnag;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.AppenderBase;

import static java.lang.String.format;

public class LogbackAppender extends AppenderBase<ILoggingEvent> {
  private String apiKey;

  private Client client;

  public String getApiKey() {
    return apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  @Override
  public void start() {
    super.start();
    if (apiKey == null) {
      throw new IllegalStateException("apiKey required");
    }

    client = new Client(apiKey);
  }

  @Override
  protected void append(ILoggingEvent event) {
    IThrowableProxy throwableProxy = event.getThrowableProxy();

    if (throwableProxy != null) {
      notify(event.getLevel(), event.getFormattedMessage(), throwableProxy);
    }
  }

  private void notify(Level level, String message, IThrowableProxy throwableProxy) {
    Throwable throwable = new Throwable(format("%s; %s", message, throwableProxy.getMessage()));

    throwable.setStackTrace(getCallData(throwableProxy));

    client.notify(throwable, level.toString());
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
