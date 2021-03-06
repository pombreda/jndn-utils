/*
 * jndn-utils
 * Copyright (c) 2015, Intel Corporation.
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms and conditions of the GNU Lesser General Public License,
 * version 3, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 */
package com.intel.jndn.utils;

import org.junit.Test;
import static org.junit.Assert.*;
import com.intel.jndn.mock.MockTransport;
import com.intel.jndn.utils.client.FutureData;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;
import net.named_data.jndn.Data;
import net.named_data.jndn.Face;
import net.named_data.jndn.Name;
import net.named_data.jndn.util.Blob;
import org.junit.rules.ExpectedException;

/**
 * Test Client.java
 *
 * @author Andrew Brown <andrew.brown@intel.com>
 */
public class SimpleClientTest {

  private static final Logger logger = Logger.getLogger(SimpleClient.class.getName());
  public ExpectedException thrown = ExpectedException.none();

  /**
   * Test retrieving data synchronously
   *
   * @throws java.io.IOException
   */
  @Test
  public void testGetSync() throws IOException {
    // setup face
    MockTransport transport = new MockTransport();
    Face face = new Face(transport, null);

    // setup return data
    Data response = new Data(new Name("/test/sync"));
    response.setContent(new Blob("..."));
    transport.respondWith(response);

    // retrieve data
    logger.info("Client expressing interest synchronously: /test/sync");
    SimpleClient client = new SimpleClient();
    Data data = client.getSync(face, new Name("/test/sync"));
    assertEquals(new Blob("...").buf(), data.getContent().buf());
  }

  /**
   * Test retrieving data asynchronously
   *
   * @throws InterruptedException
   */
  @Test
  public void testGetAsync() throws InterruptedException, ExecutionException {
    // setup face
    MockTransport transport = new MockTransport();
    Face face = new Face(transport, null);

    // setup return data
    Data response = new Data(new Name("/test/async"));
    response.setContent(new Blob("..."));
    transport.respondWith(response);

    // retrieve data
    logger.info("Client expressing interest asynchronously: /test/async");
    SimpleClient client = new SimpleClient();
    Future<Data> futureData = client.getAsync(face, new Name("/test/async"));

    assertTrue(!futureData.isDone());
    futureData.get();
    assertTrue(futureData.isDone());
    assertEquals(new Blob("...").toString(), futureData.get().getContent().toString());
  }

  /**
   * Test that asynchronous client times out correctly
   *
   * @throws InterruptedException
   */
  @Test(expected = TimeoutException.class)
  public void testTimeout() throws InterruptedException, ExecutionException, TimeoutException {
    // setup face
    MockTransport transport = new MockTransport();
    Face face = new Face(transport, null);

    // retrieve non-existent data, should timeout
    logger.info("Client expressing interest asynchronously: /test/timeout");
    Future<Data> futureData = SimpleClient.getDefault().getAsync(face, new Name("/test/timeout"));

    // expect an exception
    futureData.get(50, TimeUnit.MILLISECONDS);
  }

  /**
   * Test that a sync failed request fails with an exception.
   */
  @Test(expected = ExecutionException.class)
  public void testAsyncFailureToRetrieve() throws InterruptedException, ExecutionException {
    Future future = SimpleClient.getDefault().getAsync(new Face(), new Name("/test/no-data"));
    assertTrue(future.isDone());
    future.get();
  }

  /**
   * Test that a sync failed request fails with an exception.
   */
  @Test(expected = IOException.class)
  public void testSyncFailureToRetrieve() throws IOException {
    SimpleClient.getDefault().getSync(new Face(), new Name("/test/no-data"));
  }
}
