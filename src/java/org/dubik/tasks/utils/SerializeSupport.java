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
package org.dubik.tasks.utils;

import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.util.text.StringUtil;
import org.dubik.tasks.model.*;
import org.dubik.tasks.model.impl.TaskBuilder;
import org.jdom.Element;

import java.util.List;

/**
 * @author Sergiy Dubovik
 */
public class SerializeSupport {
    private static final String TASKS = "tasks";
    private static final String TASK = "task";
    private static final String TASK_PRIORITY = "priority";
    private static final String TASK_COMPLETED = "completed";
    private static final String TASK_ESTIMATED = "estimated";
    private static final String TASK_CREATED = "created";
    private static final String TASK_HIGHLIGHTED = "highlighted";
    private static final String TASK_HIGHLIGHTING_TYPE = "highlightingtype";
    private static final String TASK_TITLE = "title";
    private static final String TASK_ACTUAL = "actual";
    private static final String TASK_DESCRIPTION = "description";

    static public void writeExternal(ITaskModel taskModel, Element element) throws WriteExternalException {
        Element tasksRoot = new Element(TASKS);
        element.addContent(tasksRoot);
        for (int i = 0; i < taskModel.size(); i++) {
            ITask task = taskModel.getTask(i);
            writeTasksRecursively(tasksRoot, task);
        }
    }

    static private void writeTasksRecursively(Element taskRoot, ITask task) {
        Element newTaskRoot = writeTask(taskRoot, task);
        for (int i = 0; i < task.size(); i++) {
            ITask subTask = task.get(i);
            writeTasksRecursively(newTaskRoot, subTask);
        }
    }

    static private Element writeTask(Element taskRoot, ITask task) {
        Element xTask = new Element(TASK);
        xTask.setAttribute(TASK_PRIORITY, task.getPriority().name());
        xTask.setAttribute(TASK_COMPLETED, Boolean.toString(task.isCompleted()));
        xTask.setAttribute(TASK_ESTIMATED, Long.toString(task.getEstimatedTime()));
        xTask.setAttribute(TASK_CREATED, Long.toString(task.getCreationTime()));
        xTask.setAttribute(TASK_ACTUAL, Long.toString(task.getActualTime()));
        xTask.setAttribute(TASK_HIGHLIGHTED, Boolean.toString(task.isHighlighted()));
        xTask.setAttribute(TASK_HIGHLIGHTING_TYPE, task.getHighlightingType().toString());
        xTask.setAttribute(TASK_TITLE, task.getTitle());
        xTask.setAttribute(TASK_DESCRIPTION, StringUtil.escapeXml(task.getDescription() == null ? "" : task.getDescription()));

        taskRoot.addContent(xTask);

        return xTask;
    }

    static public void readExternal(ITaskModel taskModel, Element element) throws InvalidDataException {
        Element tasksRoot = element.getChild(TASKS);
        if (tasksRoot == null) {
            return;
        }

        List tasks = tasksRoot.getChildren();

        for (Object taskElem : tasks) {
            Element xTask = (Element) taskElem;
            addTasksRecursively(xTask, taskModel, null);
        }
    }

    static private void addTasksRecursively(Element taskElem, ITaskModel model, ITask parentTask) {
        TaskPriority priority = ExternalizeSupport.getSafelyTaskPriority(taskElem, TASK_PRIORITY, TaskPriority.Normal);
        boolean completed = ExternalizeSupport.getSafelyBoolean(taskElem, TASK_COMPLETED, false);
        boolean highlighted = ExternalizeSupport.getSafelyBoolean(taskElem, TASK_HIGHLIGHTED, false);
        TaskHighlightingType type = ExternalizeSupport.getSafelyHighlightingType(taskElem, TASK_HIGHLIGHTING_TYPE, TaskHighlightingType.Red);
        long estimated = ExternalizeSupport.getSafelyLong(taskElem, TASK_ESTIMATED, 0);
        long actual = ExternalizeSupport.getSafelyLong(taskElem, TASK_ACTUAL, 0);
        long created = ExternalizeSupport.getSafelyLong(taskElem, TASK_CREATED, System.currentTimeMillis());

        String oldVersionTitle = taskElem.getText();
        String newVersionTitle = taskElem.getAttributeValue(TASK_TITLE);
        String title;
        if (newVersionTitle != null && newVersionTitle.length() != 0) {
            title = newVersionTitle;
        }
        else {
            title = oldVersionTitle;
        }

        String description = StringUtil.unescapeXml(taskElem.getAttributeValue(TASK_DESCRIPTION));
        if (description != null && description.isEmpty()) {
            description = null;
        }

        ITask task = new TaskBuilder()
                .setTitle(title)
                .setDescription(description)
                .setPriority(priority)
                .setEstimatedTime(estimated)
                .setActualTime(actual)
                .setCreationTime(created)
                .setCompleted(completed)
                .setHighlighted(highlighted)
                .setHighlightingType(type)
                .build();

        model.addTask(parentTask, task);

        for (Object subTaskElem : taskElem.getChildren()) {
            Element xSubTask = (Element) subTaskElem;
            addTasksRecursively(xSubTask, model, task);
        }
    }
}
