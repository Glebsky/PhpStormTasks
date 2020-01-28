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
package org.dubik.tasks.ui.actions.highlight;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import org.dubik.tasks.TaskController;
import org.dubik.tasks.model.ITask;
import org.dubik.tasks.model.TaskHighlightingType;
import org.dubik.tasks.ui.actions.BaseTaskAction;

import java.util.List;

/**
 * @author Sergiy Dubovik
 */
@SuppressWarnings({"ComponentNotRegistered"})
public abstract class BaseHighlightTaskAction extends BaseTaskAction {
    public void actionPerformed(AnActionEvent e) {
        TaskController controller = getController(e);
        if (controller != null) {
            List<ITask> selectedTasks = controller.getSelectedTasks();
            if (canHighlightOrUnhighlight(selectedTasks, controller)) {
                for (ITask task : selectedTasks) {
                    controller.setTaskHighlightingType(task, getHightlightingType());
                    controller.highlightTask(task);
                }
            }
        }
    }

    protected void update(TaskController controller, List<ITask> selectedTasks, Presentation presentation) {
        presentation.setEnabled(canHighlightOrUnhighlight(selectedTasks, controller) && selectedTasks.size() != 0);
    }

    private boolean canHighlightOrUnhighlight(List<ITask> tasks, TaskController controller) {
        boolean result = true;

        for (ITask task : tasks) {
            result = result && (controller.canHighlight(task) || controller.canBeUnhighlighted(task));
        }

        return result;
    }

    abstract TaskHighlightingType getHightlightingType();
}
