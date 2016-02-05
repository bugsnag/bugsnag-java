package com.bugsnag.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.bugsnag.Configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class HttpClientTest {

  Configuration configuration = new Configuration();
  HttpClient httpClient = new HttpClient(configuration);

  ServerSocket serverSocket;

  @Before
  public void setUp() throws IOException {
    // create server with random port and room for single connection
    serverSocket = new ServerSocket(0, 1);
  }

  @After
  public void tearDown() throws Exception {
    serverSocket.close();
  }

  @Ignore("behavior simulating read timeout isn't consistent across platforms")
  @Test
  public void testConnectionTimeout() throws Exception {
    configuration.setConnectionTimeout(10);

    // reserve the only available connection
    new Socket().connect(serverSocket.getLocalSocketAddress());

    try {
      httpClient.post("http://localhost:" + serverSocket.getLocalPort(), new ByteArrayInputStream("foo".getBytes()));
    } catch (NetworkException e) {
      assertEquals("connect timed out", e.getCause().getMessage());
    }
  }

  @Test
  public void testReadTimeout() throws Exception {
    configuration.setReadTimeout(10);

    try {
      httpClient.post("http://localhost:" + serverSocket.getLocalPort(), new ByteArrayInputStream("foo".getBytes()));
    } catch (NetworkException e) {
      assertEquals("Read timed out", e.getCause().getMessage());
    }
  }

  @Test
  public void testProxyConnection() throws Exception {
    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("notSoLocalHost", 8080));
    configuration.setProxy(proxy);

    try{
      httpClient.post("http://localhost:" + serverSocket.getLocalPort(), new ByteArrayInputStream("foo".getBytes()));
      fail("Should throw Exception because proxy host is unknown.");
    } catch (NetworkException e){
      assertEquals("notSoLocalHost", e.getCause().getMessage());
    }
  }
}
