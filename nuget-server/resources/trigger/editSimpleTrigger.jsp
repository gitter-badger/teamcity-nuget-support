<%--
  ~ Copyright 2000-2011 JetBrains s.r.o.
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="ib" class="jetbrains.buildServer.nuget.server.trigger.TriggerBean" scope="request"/>
<jsp:useBean id="isWindows" type="java.lang.Boolean" scope="request"/>

<tr>
  <th>NuGet.exe<l:star/>:</th>
  <td>
    <jsp:include page="../tool/runnerSettings.html?name=${ib.nuGetExeKey}&style=width:20em"/>
  </td>
</tr>

<c:if test="${not isWindows}">
  <tr>
     <td colspan="2">
       <div class="attentionComment">
         TeamCity server is not running under Windows. TeamCity will emulate NuGet feeds check to avoid starting .NET processes
       </div>
     </td>
   </tr>
</c:if>

<tr>
  <th>NuGet package source:</th>
  <td>
    <props:textProperty name="${ib.sourceKey}" style="width:20em;" />
    <span class="smallNote">Specify NuGet packages repository to monitor packages changes. Leave blank to use default NuGet feed</span>
    <span class="error" id="error_${ib.sourceKey}"></span>
  </td>
</tr>

<tr>
  <th>Package Id<l:star/>:</th>
  <td>
    <props:textProperty name="${ib.packageKey}" style="width:20em;"/>
    <span class="smallNote">Specify package Id to check for updates.</span>
    <span class="error" id="error_${ib.packageKey}"></span>
  </td>
</tr>

<tr>
  <th>Package Version Spec:</th>
  <td>
    <props:textProperty name="${ib.versionKey}" style="width:20em;"/>
    <span class="smallNote">Specify package version to check. Leave empty to check for latest version</span>
    <span class="error" id="error_${ib.versionKey}"></span>
  </td>
</tr>
