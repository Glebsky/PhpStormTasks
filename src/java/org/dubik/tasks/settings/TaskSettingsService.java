/*
 * Copyright 2013 Sergiy Dubovik, WarnerJan Veldhuis
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

package org.dubik.tasks.settings;

import com.intellij.openapi.components.*;
import org.jetbrains.annotations.Nullable;

@State(
        name = "TaskPluginSettings",
        storages = {
                @Storage(value = "/taskpluginsettings.xml")
        }
)
public class TaskSettingsService implements PersistentStateComponent<TaskSettings> {
    private TaskSettings state;

    public TaskSettingsService() {
        state = TaskSettings.getDefaults();
    }

    public static TaskSettings getSettings() {
        TaskSettingsService service = ServiceManager.getService(TaskSettingsService.class);
        return service.getState();
    }

    @Nullable
    public TaskSettings getState() {
        return state;
    }

    public void loadState(TaskSettings state) {
        this.state = state;
    }


}
