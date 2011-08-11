/*
 * Copyright 2000-2011 JetBrains s.r.o.
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

package jetbrains.buildServer.nuget.server.toolRegistry.tab;

import jetbrains.buildServer.controllers.AuthorizationInterceptor;
import jetbrains.buildServer.controllers.BaseFormXmlController;
import jetbrains.buildServer.controllers.BasePropertiesBean;
import jetbrains.buildServer.controllers.RequestPermissionsChecker;
import jetbrains.buildServer.nuget.server.toolRegistry.NuGetToolManager;
import jetbrains.buildServer.serverSide.auth.AccessDeniedException;
import jetbrains.buildServer.serverSide.auth.AuthorityHolder;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 11.08.11 12:12
 */
public class InstallToolController extends BaseFormXmlController {
  private final String myPath;
  private final NuGetToolManager myToolsManager;
  private final PluginDescriptor myDescriptor;

  public InstallToolController(@NotNull final AuthorizationInterceptor auth,
                               @NotNull final PermissionChecker checker,
                               @NotNull final WebControllerManager web,
                               @NotNull final NuGetToolManager toolsManager,
                               @NotNull final PluginDescriptor descriptor) {
    myToolsManager = toolsManager;
    myDescriptor = descriptor;
    myPath = descriptor.getPluginResourcesPath("tool/nuget-server-tab-install-tool.html");
    auth.addPathBasedPermissionsChecker(myPath, new RequestPermissionsChecker() {
      public void checkPermissions(@NotNull AuthorityHolder authorityHolder,
                                   @NotNull HttpServletRequest request) throws AccessDeniedException {
        checker.assertAccess(authorityHolder);
      }
    });
    web.registerController(myPath, this);
  }

  @NotNull
  public String getPath() {
    return myPath;
  }


  @Override
  protected ModelAndView doGet(HttpServletRequest request, HttpServletResponse response) {
    ModelAndView mv = new ModelAndView(myDescriptor.getPluginResourcesPath("tool/installTool-show.jsp"));

    mv.getModelMap().put("available", myToolsManager.getAvailableTools());
    mv.getModelMap().put("propertiesBean", new BasePropertiesBean(new HashMap<String, String>()));

    return mv;
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response, Element xmlResponse) {

  }
}
