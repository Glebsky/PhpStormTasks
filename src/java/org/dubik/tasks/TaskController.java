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
package org.dubik.tasks;

import org.dubik.tasks.model.*;
import org.dubik.tasks.model.impl.TaskGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;

/**
 * Task controller class.
 *
 * @author Sergiy Dubovik
 */
public class TaskController implements TreeSelectionListener {
    private static final List<ITask> EMPTY_SELECTION = new ArrayList<ITask>();
    private static ITask DUMMY_ROOT_TASK = new ITask() {

        public String getTitle() {
            return "Root";
        }

        @Nullable
        public String getDescription() {
            return "Dummy root object";
        }

        @NotNull
        public TaskPriority getPriority() {
            return TaskPriority.Normal;
        }

        public long getEstimatedTime() {
            return 0;
        }

        public long getCreationTime() {
            return 0;
        }

        public boolean isCompleted() {
            return false;
        }

        public boolean isHighlighted() {
            return false;
        }

        @NotNull
        public TaskHighlightingType getHighlightingType() {
            return TaskHighlightingType.Red;
        }

        public void add(@NotNull ITask task) {
            //noop
        }

        public void add(int index, @NotNull ITask task) {
            //noop
        }

        public int size() {
            return 0;
        }

        public ITask get(int index) {
            return null;
        }

        public ITask getParent() {
            return null;
        }

        public int indexOf(ITask subTask) {
            return 0;
        }

        public int getCompletionRatio() {
            return 0;
        }

        public long getActualTime() {
            return 0;
        }

        public boolean isRunning() {
            return false;
        }

        public void start() {
        }

        public void stop() {
        }
    };
    private ITaskModel taskModel;
    private List<ITask> selectedTasks;


    /**
     * Creates task controller.
     *
     * @param taskModel task model
     */
    public TaskController(ITaskModel taskModel) {
        this.taskModel = taskModel;
    }

    @NotNull
    synchronized public List<ITask> getSelectedTasks() {
        if (selectedTasks == null) {
            return EMPTY_SELECTION;
        }

        return selectedTasks;
    }

    public void setSelectedTasks(List<ITask> tasks) {
        selectedTasks = new ArrayList<ITask>(tasks);
    }

    /**
     * Checks if task can be deleted.
     *
     * @param task task you want to check
     * @return <code>true</code> if task can be deleted, otherwise <code>false</code>
     */
    public boolean canDelete(ITask task) {
        return task != null && !(task instanceof ITaskGroup);

    }

    /**
     * Checks if task can be completed.
     *
     * @param task task you want to check
     * @return <code>true</code> if task can be completed, otherwise <code>false</code>
     */
    public boolean canComplete(ITask task) {
        return !(task instanceof TaskGroup) && !task.isCompleted() && task.size() == 0;
    }

    /**
     * Checks if task can be uncompleted.
     *
     * @param task task you want to check
     * @return <code>true</code> if task can be uncompleted, otherwise <code>false</code>
     */
    public boolean canBeUncomplete(ITask task) {
        return !(task instanceof TaskGroup) && task.isCompleted() && task.size() == 0;
    }

    /**
     * Checks if task can be edited.
     *
     * @param task task you want to check
     * @return <code>true</code> if task can be edited, otherwise <code>false</code>
     */
    public boolean canEdit(ITask task) {
        return !(task instanceof TaskGroup);
    }

    /**
     * Adds a task to a model.
     *
     * @param parentTask            parent task
     * @param task                  a task
     */
    public void addTask(ITask parentTask, ITask task) {
        taskModel.addTask(parentTask, task);
    }

    /**
     * Updates task properties.
     *
     * @param sTask         task to update
     * @param parent        new parent
     * @param title         new title or description
     * @param priority      new priority
     * @param estimatedTime new estimated time
     */
    public void updateTask(ITask sTask, ITask parent, String title, String description, TaskPriority priority, long estimatedTime) {
        taskModel.updateTask(sTask, parent, title, description, priority, estimatedTime);
    }

    /**
     * Deletes specified task.
     *
     * @param task task to delete
     */
    public void deleteTask(ITask task) {
        if (canDelete(task)) {
            taskModel.deleteTask(task);
        }
    }

    /**
     * Updates currently selected tasks.
     *
     * @param e tree selection event
     */
    synchronized public void valueChanged(TreeSelectionEvent e) {
        TreePath[] selectedPaths = ((JTree) e.getSource()).getSelectionPaths();

        if (selectedPaths == null || selectedPaths.length == 0) {
            selectedTasks = EMPTY_SELECTION;
        }
        else {
            selectedTasks = new ArrayList<ITask>();
            for (TreePath selectedPath : selectedPaths) {
                selectedTasks.add((ITask) selectedPath.getLastPathComponent());
            }
        }
    }

    /**
     * Completes specified task.
     *
     * @param task task to complete
     */
    public void completeTask(ITask task) {
        assert task != null;
        if (canComplete(task)) {
            taskModel.completeTask(task);
        }
    }

    /**
     * Uncompletes specified task.
     *
     * @param task task to uncomplete
     */
    public void uncompleteTask(ITask task) {
        assert task != null;

        if (canBeUncomplete(task)) {
            taskModel.uncompleteTask(task);
        }
    }

    /**
     * Highlights specified task.
     *
     * @param task task to highlight
     * @return <code>true</code> if task can be highlighted, otherwise <code>false</code>
     */
    public boolean canHighlight(ITask task) {
        assert task != null;

        return !(task instanceof ITaskGroup) && !task.isHighlighted();
    }

    /**
     * Checks if specified task can be unhighlighted.
     *
     * @param task specified task to check
     * @return <code>true</code> if task can be unhighlighted, otherwise <code>false</code>
     */
    public boolean canBeUnhighlighted(ITask task) {
        assert task != null;

        return !(task instanceof ITaskGroup) && task.isHighlighted();
    }

    /**
     * Highlights specified task.
     *
     * @param task task to highlighted
     */
    public void highlightTask(ITask task) {
        assert task != null;

        if (canHighlight(task)) {
            taskModel.highlightTask(task);
        }
    }

    /**
     * Unhighlights specified task.
     *
     * @param task task to unhighlight
     */
    public void unhighlightTask(ITask task) {
        assert task != null;

        if (canBeUnhighlighted(task)) {
            taskModel.unhighlightTask(task);
        }
    }

    /**
     * Updates actual time for specified task.
     *
     * @param task       task to update
     * @param actualTime new actual time
     */
    public void updateActualTime(ITask task, long actualTime) {
        assert task != null;
        assert actualTime >= 0;

        taskModel.updateActualTime(task, actualTime);
    }

    /**
     * Returns all tasks, model has.
     *
     * @return all tasks with their sub tasks
     */
    public List<ITask> getAllTasks() {
        List<ITask> allTasks = new ArrayList<ITask>();
        for (int i = 0; i < taskModel.size(); i++) {
            addTasksRecursively(taskModel.getTask(i), allTasks);
        }

        return allTasks;
    }

    /**
     * Returns all sub tasks of specified task.
     *
     * @param task parent task
     * @return all sub tasks
     */
    public List<ITask> getSubTasks(ITask task) {
        List<ITask> subTasks = new ArrayList<ITask>();
        for (int i = 0; i < task.size(); i++) {
            addTasksRecursively(task.get(i), subTasks);
        }

        return subTasks;
    }

    private void addTasksRecursively(ITask task, List<ITask> allTasks) {
        allTasks.add(task);
        for (int i = 0; i < task.size(); i++) {
            addTasksRecursively(task.get(i), allTasks);
        }
    }

    /**
     * Returns a list of possible parents of specified task.
     *
     * @param sTask task for which possible parents should be found
     * @return array of possible parents
     */
    public List<ITask> findPossibleParents(ITask sTask) {
        List<ITask> allTasks = getAllTasks();
        List<ITask> subTasks = getSubTasks(sTask);
        allTasks.removeAll(subTasks);
        allTasks.remove(sTask);

        return new ArrayList<ITask>(allTasks);
    }

    public ITask findParentFor(ITask task) {
        ITask parent = null;
        for (int i = 0; i < taskModel.size(); i++) {
            parent = findParentForRecursively(taskModel.getTask(i), task);
            if (parent != null) {
                return parent;
            }
        }

        return parent;
    }

    private ITask findParentForRecursively(ITask task, ITask childTask) {
        for (int i = 0; i < task.size(); i++) {
            ITask t = task.get(i);
            if (t == childTask) {
                return task;
            }
            else {
                ITask t1 = findParentForRecursively(task, childTask);
                if (t1 != null) {
                    return t1;
                }
            }

        }

        return null;
    }

    public boolean canMoveUp(ITask task) {
        return taskModel.canMoveUp(task);
    }

    public boolean canMoveDown(ITask task) {
        return taskModel.canMoveDown(task);
    }

    public void moveUp(ITask task) {
        taskModel.moveUp(task);
    }

    public void moveDown(ITask task) {
        taskModel.moveDown(task);
    }

    public void setTaskHighlightingType(ITask task, TaskHighlightingType hightlightingType) {
        taskModel.setTaskHighlightingType(task, hightlightingType);
    }

    /**
     * Returns dummy task. It's used in UI to show "root" task.
     * All methods returns 0 or null, except for title.
     *
     * @return dummy task
     */
    public ITask getDummyRootTaskInstance() {
        return DUMMY_ROOT_TASK;
    }

    public void moveTask(ITask task, ITask newParent, int childIndex) {
        taskModel.moveTask(task, newParent, childIndex);
    }

}
