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

package jetbrains.buildServer.nuget.server.trigger.impl;

import jetbrains.buildServer.serverSide.TeamCityProperties;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 04.10.11 14:01
 */
public class PackageCheckerSettings {

  public long getPackageCheckInterval() {
    return TeamCityProperties.getInteger("teamcity.nuget.packageCheckInterval", 5 * 60 * 1000);
  }

  public long getPackageCheckRequestIdleRemoveInterval(long checkInterval) {
    return 5 * checkInterval;
  }

  public long getCheckChangesThreashold() {
    return getPackageCheckInterval() / 2;
  }

  public long getMaxSleepInterval() {
    return getPackageCheckInterval() / 2;
  }

  public int getCheckerThreads() {
    return TeamCityProperties.getInteger("teamcity.nuget.trigger.pool", 4);
  }
}