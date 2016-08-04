package com.bugsnag.integration;

import com.bugsnag.Client;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FullTest {

  @Test
  public void testException() throws Exception {
    Client client = new Client("XXX");
    try {
      throw new RuntimeException("from testException");
    } catch (Exception e) {
      client.notify(e);
    }

    // Should notify
  }

  @Test
  public void testLogbackError() throws Exception {
    Logger logger = LoggerFactory.getLogger(FullTest.class);
    logger.error("from testLogbackError");

    // Should not notify
  }

  @Test
  public void testLogbackException() throws Exception {
    Logger logger = LoggerFactory.getLogger(FullTest.class);
    try {
      throw new RuntimeException("from testLogbackException");
    }
    catch (Exception e) {
      logger.error("Got an error", e);
    }

    // Should notify
  }

}
