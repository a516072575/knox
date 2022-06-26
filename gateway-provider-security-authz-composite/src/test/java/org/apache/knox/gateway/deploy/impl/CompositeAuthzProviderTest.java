/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.knox.gateway.deploy.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;


import org.apache.knox.gateway.descriptor.FilterParamDescriptor;
import org.apache.knox.gateway.descriptor.ResourceDescriptor;
import org.junit.Test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;

public class CompositeAuthzProviderTest {
  @Test
  public void testParsingProviderParams() throws Exception {
    String name = "AclsAuthz";
    Map<String, String> providerParams = new HashMap<>();
    // provider params are disambiguated by prefixing them with
    // the provider name. Therefore the following should only
    // result in a single param making it through since only
    // one has a prefix that matches the name.
    providerParams.put("AclsAuthz.webhdfs.acl", "admin;*;*");
    providerParams.put("SomeOther.webhdfs.acl", "admin;*;*");
    List<FilterParamDescriptor> params = new ArrayList<>();
    ResourceDescriptor resource = createMock(ResourceDescriptor.class);
    FilterParamDescriptor fpd = createMock(FilterParamDescriptor.class);
    expect(resource.createFilterParam()).andReturn(fpd).atLeastOnce();
    expect(fpd.name("webhdfs.acl")).andReturn(fpd).once();
    expect(fpd.value("admin;*;*")).andReturn(fpd).once();
    replay(resource, fpd);
    CompositeAuthzDeploymentContributor c = new CompositeAuthzDeploymentContributor();
    c.getProviderSpecificParams(resource, params, providerParams, name);
  }

  @Test
  public void testParsingProviderNames() throws Exception {
    String names = "AclsAuthz,   SomeOther,TheOtherOne";
    CompositeAuthzDeploymentContributor c = new CompositeAuthzDeploymentContributor();
    List providerNames = c.parseProviderNames(names);
    assertEquals(3, providerNames.size());
    assertEquals("AclsAuthz", providerNames.get(0));
    assertEquals("SomeOther", providerNames.get(1));
    assertEquals("TheOtherOne", providerNames.get(2));
  }

  @Test
  public void testingParsingProviderNames() throws Exception {
    String testnames = " SpaceBefore,SpaceAfter , SpaceBeforeandAfter ,NoSpaces,   MoreSpaces   ";
    CompositeAuthzDeploymentContributor c = new CompositeAuthzDeploymentContributor();
    List providerNames = c.parseProviderNames(testnames);
    assertEquals(5, providerNames.size());
    assertEquals("SpaceBefore", providerNames.get(0));
    assertEquals("SpaceAfter", providerNames.get(1));
    assertEquals("SpaceBeforeandAfter", providerNames.get(2));
    assertEquals("NoSpaces", providerNames.get(3));
    assertEquals("MoreSpaces", providerNames.get(4));
  }
  @Test

  public void testingNullandEmptyProviderNames() throws Exception {
    String testnames = "";
    CompositeAuthzDeploymentContributor c = new CompositeAuthzDeploymentContributor();
    List providerNames = c.parseProviderNames(testnames);
    assertEquals(0,providerNames.size());
    assertEquals(Collections.emptyList(), providerNames);

    testnames = "  ";
    providerNames = c.parseProviderNames(testnames);
    assertEquals(0, providerNames.size());
    assertEquals(Collections.emptyList(), providerNames);

    testnames = null;
    providerNames = c.parseProviderNames(testnames);
    assertEquals(0,providerNames.size());
    assertEquals(Collections.emptyList(), providerNames);
  }
}

