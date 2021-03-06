/*
 * Copyright 2000-2014 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jetbrains.buildServer.nuget.tests.server.tools;

import jetbrains.buildServer.BaseTestCase;
import jetbrains.buildServer.nuget.common.NuGetToolReferenceUtils;
import jetbrains.buildServer.nuget.server.ToolPaths;
import jetbrains.buildServer.nuget.server.feed.reader.NuGetFeedReader;
import jetbrains.buildServer.nuget.server.impl.ToolPathsImpl;
import jetbrains.buildServer.nuget.server.settings.impl.NuGetSettingsManagerImpl;
import jetbrains.buildServer.nuget.server.toolRegistry.NuGetInstalledTool;
import jetbrains.buildServer.nuget.server.toolRegistry.ToolException;
import jetbrains.buildServer.nuget.server.toolRegistry.impl.*;
import jetbrains.buildServer.nuget.server.toolRegistry.impl.impl.NuGetToolManagerImpl;
import jetbrains.buildServer.serverSide.ServerPaths;
import junit.framework.Assert;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created 27.12.12 18:34
 *
 * @author Eugene Petrenko (eugene.petrenko@jetbrains.com)
 */
public class NuGetToolManagerTest extends BaseTestCase {
  private Mockery m;
  private AvailableToolsState myAvailables;
  private NuGetFeedReader myFeed;
  private ToolsWatcher myWatcher;
  private ToolPaths myPaths;
  private NuGetToolsInstaller myInstaller;
  private NuGetToolDownloader myDownloader;
  private ToolsRegistry myToolsRegistry;
  private NuGetToolManagerImpl myToolManager;

  @BeforeMethod
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    m = new Mockery();
    myAvailables = m.mock(AvailableToolsState.class);
    myFeed = m.mock(NuGetFeedReader.class);
    myWatcher = m.mock(ToolsWatcher.class);
    myInstaller = m.mock(NuGetToolsInstaller.class);
    myDownloader = m.mock(NuGetToolDownloader.class);
    myToolsRegistry = m.mock(ToolsRegistry.class);

    myPaths = new ToolPathsImpl(new ServerPaths(createTempDir().getPath()));
    myToolManager = new NuGetToolManagerImpl(
            myAvailables,
            myInstaller,
            myDownloader,
            myToolsRegistry,
            new NuGetToolsSettings(new NuGetSettingsManagerImpl())
            );
  }

  @Test
  public void test_updload_tool() throws ToolException {
    final InstalledTool it = m.mock(InstalledTool.class);

    m.checking(new Expectations(){{
      oneOf(myInstaller).installNuGet("aaa", new File("foo")); will(returnValue("zzz"));
      oneOf(myToolsRegistry).findTool("zzz"); will(returnValue(it));
    }});

    Assert.assertSame(it, myToolManager.installTool("aaa", new File("foo")));
  }

  @Test
  public void test_install_tool() throws ToolException {
    final InstalledTool it = m.mock(InstalledTool.class);

    m.checking(new Expectations(){{
      oneOf(myDownloader).installNuGet("aaa"); will(returnValue("zzz"));
      oneOf(myToolsRegistry).findTool("zzz"); will(returnValue(it));
    }});

    Assert.assertSame(it, myToolManager.downloadTool("aaa"));
  }

  @Test
  public void test_default_tool_null() {
    m.checking(new Expectations(){{
      allowing(myToolsRegistry).findTool(null); will(returnValue(null));
    }});
    Assert.assertNull(myToolManager.getDefaultTool());
  }

  @Test
  public void test_default_tool_2() {
    myToolManager.setDefaultTool("aaa");
    final InstalledTool it = m.mock(InstalledTool.class);
    m.checking(new Expectations(){{
      allowing(it).getId(); will(returnValue("aaa"));
      allowing(it).getPath(); will(returnValue(new File("some-path")));
      allowing(it).getVersion(); will(returnValue("42.333.667"));
      allowing(myToolsRegistry).findTool("aaa"); will(returnValue(it));
    }});

    NuGetInstalledTool ret = myToolManager.getDefaultTool();
    Assert.assertNotNull(ret);
    Assert.assertTrue(ret.isDefaultTool());
    Assert.assertEquals(it.getId(), ret.getId());
    Assert.assertEquals(it.getPath(), ret.getPath());
    Assert.assertEquals(it.getVersion(), ret.getVersion());
  }

  @Test
  public void test_default_tool_3() {
    myToolManager.setDefaultTool("aaa");

    m.checking(new Expectations(){{
      allowing(myToolsRegistry).findTool("aaa"); will(returnValue(null));
    }});

    NuGetInstalledTool ret = myToolManager.getDefaultTool();
    Assert.assertNull(ret);
  }

  @Test
  public void test_set_default() {
    myToolManager.setDefaultTool("some_tool");

    Assert.assertEquals("some_tool", myToolManager.getDefaultToolId());
  }

  @Test
  public void test_nuget_path_custom() throws IOException {
    File file = createTempFile();
    Assert.assertEquals(myToolManager.getNuGetPath(file.getPath()), file.getPath());
  }

  @Test
  public void test_nuget_path_preset() throws IOException {
    final InstalledTool it = m.mock(InstalledTool.class);
    m.checking(new Expectations(){{
      allowing(it).getPath(); will(returnValue(new File("some-path")));
      allowing(myToolsRegistry).findTool("aaa"); will(returnValue(it));
    }});

    Assert.assertEquals(myToolManager.getNuGetPath("?aaa"), "some-path");
  }

  @Test
  public void test_nuget_resolves_default_tool() {
    myToolManager.setDefaultTool("aaa");

    final InstalledTool it = m.mock(InstalledTool.class);
    m.checking(new Expectations(){{
      allowing(it).getPath(); will(returnValue(new File("some-path")));
      allowing(myToolsRegistry).findTool("aaa"); will(returnValue(it));
    }});

    Assert.assertEquals(myToolManager.getNuGetPath(NuGetToolReferenceUtils.getDefaultToolPath()), "some-path");
  }

  @Test(expectedExceptions = RuntimeException.class)
  public void test_nuget_resolves_default_tool_not_found() {
    myToolManager.setDefaultTool("bbb");

    m.checking(new Expectations(){{
      allowing(myToolsRegistry).findTool("bbb"); will(returnValue(null));
    }});

    myToolManager.getNuGetPath(NuGetToolReferenceUtils.getDefaultToolPath());
  }

  @Test
  public void test_nuget_manager_should_not_return_default_tool_id() {
    myToolManager.setDefaultTool("aaa");
    final NuGetInstalledTool it = m.mock(NuGetInstalledTool.class);
    m.checking(new Expectations(){{
      allowing(it).getId(); will(returnValue("aaa"));
      allowing(myToolsRegistry).getTools(); will(returnValue(Arrays.asList(it)));
    }});

    final String defaultId = NuGetToolReferenceUtils.getDefaultToolPath();
    for (NuGetInstalledTool tool : myToolManager.getInstalledTools()) {
      Assert.assertFalse(tool.getId().equals(defaultId));
    }
  }

}

