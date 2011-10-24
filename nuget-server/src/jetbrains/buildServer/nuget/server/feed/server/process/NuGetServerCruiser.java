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

package jetbrains.buildServer.nuget.server.feed.server.process;

import jetbrains.buildServer.serverSide.BuildServerAdapter;
import jetbrains.buildServer.serverSide.BuildServerListener;
import jetbrains.buildServer.serverSide.executors.ExecutorServices;
import jetbrains.buildServer.util.EventDispatcher;
import jetbrains.buildServer.util.ExceptionUtil;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 07.10.11 15:27
 */
public class NuGetServerCruiser {
  public NuGetServerCruiser(@NotNull final NuGetServerRunner runner,
                            @NotNull final ExecutorServices executors,
                            @NotNull final EventDispatcher<BuildServerListener> events) {
    events.addListener(new BuildServerAdapter(){
      private ScheduledFuture<?> myCheckTask;
      @Override
      public void serverStartup() {
        runner.startServer();
        myCheckTask = executors.getNormalExecutorService().scheduleWithFixedDelay(ExceptionUtil.catchAll("Check NuGet Server is running task", new Runnable() {
          public void run() {
            runner.ensureAlive();
          }
        }), 5, 5, TimeUnit.SECONDS);
      }

      @Override
      public void serverShutdown() {
        final Future<?> task = myCheckTask;
        myCheckTask = null;
        task.cancel(false);

        runner.stopServer();
      }
    });
  }
}
