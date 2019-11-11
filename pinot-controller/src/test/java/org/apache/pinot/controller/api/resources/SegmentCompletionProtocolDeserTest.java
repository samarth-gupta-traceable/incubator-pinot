/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.pinot.controller.api.resources;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.pinot.common.protocols.SegmentCompletionProtocol;
import org.apache.pinot.common.utils.JsonUtils;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;


public class SegmentCompletionProtocolDeserTest {
  private final int OFFSET = 1;
  private final long BUILD_TIME_MILLIS = 123;
  private final String SEGMENT_LOCATION = "file.tmp";
  private final String CONTROLLER_VIP_URL = "http://localhost:8998";

  @Test
  public void testCompleteResponseParams() {
    // Test with all params
    SegmentCompletionProtocol.Response.Params params =
        new SegmentCompletionProtocol.Response.Params().withBuildTimeSeconds(BUILD_TIME_MILLIS).withOffset(OFFSET)
            .withSegmentLocation(SEGMENT_LOCATION).withSplitCommit(true)
            .withStatus(SegmentCompletionProtocol.ControllerResponseStatus.COMMIT);

    SegmentCompletionProtocol.Response response = new SegmentCompletionProtocol.Response(params);
    assertEquals(response.getBuildTimeSeconds(), BUILD_TIME_MILLIS);
    assertEquals(response.getOffset(), OFFSET);
    assertEquals(response.getSegmentLocation(), SEGMENT_LOCATION);
    assertTrue(response.isSplitCommit());
    assertEquals(response.getStatus(), SegmentCompletionProtocol.ControllerResponseStatus.COMMIT);
  }

  @Test
  public void testIncompleteResponseParams() {
    // Test with reduced params
    SegmentCompletionProtocol.Response.Params params =
        new SegmentCompletionProtocol.Response.Params().withBuildTimeSeconds(BUILD_TIME_MILLIS).withOffset(OFFSET)
            .withStatus(SegmentCompletionProtocol.ControllerResponseStatus.COMMIT);

    SegmentCompletionProtocol.Response response = new SegmentCompletionProtocol.Response(params);
    assertEquals(response.getBuildTimeSeconds(), BUILD_TIME_MILLIS);
    assertEquals(response.getOffset(), OFFSET);
    assertNull(response.getSegmentLocation());
    assertFalse(response.isSplitCommit());
    assertEquals(response.getStatus(), SegmentCompletionProtocol.ControllerResponseStatus.COMMIT);
  }

  @Test
  public void testJsonResponseWithAllParams() {
    // Test with all params
    SegmentCompletionProtocol.Response.Params params =
        new SegmentCompletionProtocol.Response.Params().withBuildTimeSeconds(BUILD_TIME_MILLIS).withOffset(OFFSET)
            .withSegmentLocation(SEGMENT_LOCATION).withSplitCommit(true).withControllerVipUrl(CONTROLLER_VIP_URL)
            .withStatus(SegmentCompletionProtocol.ControllerResponseStatus.COMMIT);

    SegmentCompletionProtocol.Response response = new SegmentCompletionProtocol.Response(params);
    JsonNode jsonNode = JsonUtils.objectToJsonNode(response);

    assertEquals(jsonNode.get("offset").asInt(), OFFSET);
    assertEquals(jsonNode.get("segmentLocation").asText(), SEGMENT_LOCATION);
    assertTrue(jsonNode.get("isSplitCommitType").asBoolean());
    assertEquals(jsonNode.get("status").asText(), SegmentCompletionProtocol.ControllerResponseStatus.COMMIT.toString());
    assertEquals(jsonNode.get("controllerVipUrl").asText(), CONTROLLER_VIP_URL);
  }

  @Test
  public void testJsonNullSegmentLocationAndVip() {
    SegmentCompletionProtocol.Response.Params params =
        new SegmentCompletionProtocol.Response.Params().withBuildTimeSeconds(BUILD_TIME_MILLIS).withOffset(OFFSET)
            .withSplitCommit(false).withStatus(SegmentCompletionProtocol.ControllerResponseStatus.COMMIT);

    SegmentCompletionProtocol.Response response = new SegmentCompletionProtocol.Response(params);
    JsonNode jsonNode = JsonUtils.objectToJsonNode(response);

    assertEquals(jsonNode.get("offset").asInt(), OFFSET);
    assertNull(jsonNode.get("segmentLocation"));
    assertFalse(jsonNode.get("isSplitCommitType").asBoolean());
    assertEquals(jsonNode.get("status").asText(), SegmentCompletionProtocol.ControllerResponseStatus.COMMIT.toString());
    assertNull(jsonNode.get("controllerVipUrl"));
  }

  @Test
  public void testJsonResponseWithoutSplitCommit() {
    SegmentCompletionProtocol.Response.Params params =
        new SegmentCompletionProtocol.Response.Params().withBuildTimeSeconds(BUILD_TIME_MILLIS).withOffset(OFFSET)
            .withSplitCommit(false).withStatus(SegmentCompletionProtocol.ControllerResponseStatus.COMMIT);

    SegmentCompletionProtocol.Response response = new SegmentCompletionProtocol.Response(params);
    JsonNode jsonNode = JsonUtils.objectToJsonNode(response);

    assertEquals(jsonNode.get("offset").asInt(), OFFSET);
    assertNull(jsonNode.get("segmentLocation"));
    assertFalse(jsonNode.get("isSplitCommitType").asBoolean());
    assertEquals(jsonNode.get("status").asText(), SegmentCompletionProtocol.ControllerResponseStatus.COMMIT.toString());
    assertNull(jsonNode.get("controllerVipUrl"));
  }

  @Test
  public void testJsonResponseWithSegmentLocationNullVip() {
    // Should never happen because if split commit, should have both location and VIP, but testing deserialization regardless
    SegmentCompletionProtocol.Response.Params params =
        new SegmentCompletionProtocol.Response.Params().withBuildTimeSeconds(BUILD_TIME_MILLIS).withOffset(OFFSET)
            .withSegmentLocation(SEGMENT_LOCATION).withSplitCommit(false)
            .withStatus(SegmentCompletionProtocol.ControllerResponseStatus.COMMIT);

    SegmentCompletionProtocol.Response response = new SegmentCompletionProtocol.Response(params);
    JsonNode jsonNode = JsonUtils.objectToJsonNode(response);

    assertEquals(jsonNode.get("offset").asInt(), OFFSET);
    assertEquals(jsonNode.get("segmentLocation").asText(), SEGMENT_LOCATION);
    assertFalse(jsonNode.get("isSplitCommitType").asBoolean());
    assertEquals(jsonNode.get("status").asText(), SegmentCompletionProtocol.ControllerResponseStatus.COMMIT.toString());
    assertNull(jsonNode.get("controllerVipUrl"));
  }

  @Test
  public void testJsonResponseWithVipAndNullSegmentLocation() {
    // Should never happen because if split commit, should have both location and VIP, but testing deserialization regardless
    SegmentCompletionProtocol.Response.Params params =
        new SegmentCompletionProtocol.Response.Params().withBuildTimeSeconds(BUILD_TIME_MILLIS).withOffset(OFFSET)
            .withControllerVipUrl(CONTROLLER_VIP_URL).withSplitCommit(false)
            .withStatus(SegmentCompletionProtocol.ControllerResponseStatus.COMMIT);

    SegmentCompletionProtocol.Response response = new SegmentCompletionProtocol.Response(params);
    JsonNode jsonNode = JsonUtils.objectToJsonNode(response);

    assertEquals(jsonNode.get("offset").asInt(), OFFSET);
    assertNull(jsonNode.get("segmentLocation"));
    assertFalse(jsonNode.get("isSplitCommitType").asBoolean());
    assertEquals(jsonNode.get("status").asText(), SegmentCompletionProtocol.ControllerResponseStatus.COMMIT.toString());
    assertEquals(jsonNode.get("controllerVipUrl").asText(), CONTROLLER_VIP_URL);
  }
}