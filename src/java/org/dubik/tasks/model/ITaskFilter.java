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
package org.dubik.tasks.model;

import org.jetbrains.annotations.NotNull;

/**
 * Task filter, used in TaskGroup to filter out unneeded tasks.
 * For example, group by priority uses filters to show tasks only
 * with given priority.
 *
 * @author Sergiy Dubovik
 */
public interface ITaskFilter {
    /**
     * Returns <code>true</code> if task is not filtered out, otherwise <code>false</code>.
     *
     * @param task task which must be tested
     * @return <code>true</code> if it was accepted by filter
     */
    public boolean accept(@NotNull ITask task);
}
