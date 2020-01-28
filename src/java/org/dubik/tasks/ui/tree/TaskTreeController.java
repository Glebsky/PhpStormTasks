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
package org.dubik.tasks.ui.tree;

import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.tree.TreeUtil;
import org.dubik.tasks.model.ITask;
import org.dubik.tasks.model.TaskChangeEvent;
import org.dubik.tasks.ui.filters.HideCompletedFilter;

import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;

/**
 * @author Sergiy Dubovik
 */
public class TaskTreeController {
    private TaskTreeModel treeModel;
    private Tree tree;
    private boolean groupedByPriority;
    private boolean hideCompletedTasks;

    public TaskTreeController(TaskTreeModel treeModel, Tree tree) {
        this.treeModel = treeModel;
        this.tree = tree;
        groupedByPriority = false;
        hideCompletedTasks = false;
    }

    public TaskTreeModel getTreeModel() {
        return treeModel;
    }

    public void groupByPriority(boolean group) {
        groupedByPriority = group;
        treeModel.groupByPriority(groupedByPriority);

        if (groupedByPriority) {
            expandWholeTreeOneLevel(treeModel.getRoot());
        }
    }

    public boolean isGroupByPriority() {
        return groupedByPriority;
    }

    public void hideCompletedTasks(boolean hide) {
        hideCompletedTasks = hide;
        if (hide) {
            treeModel.setTaskFilter(new HideCompletedFilter());
        }
        else {
            treeModel.setTaskFilter(null);
        }
    }

    public boolean isHideCompletedTasks() {
        return hideCompletedTasks;
    }

    private void expandWholeTreeOneLevel(Object root) {
        TreePath rootPath = new TreePath(root);
        for (int i = 0; i < treeModel.getChildCount(root); i++) {
            Object child = treeModel.getChild(root, i);
            TreePath pathToChild = rootPath.pathByAddingChild(child);
            tree.expandPath(pathToChild);
        }
    }

    public void expandToObject(Object obj) {
        final Object root = treeModel.getRoot();
        if (obj == root) {
            tree.expandPath(new TreePath(root));
        }
        else {
            Object[] path = treeModel.findPathToObject(root, obj);
            if (path.length > 0) {
                tree.expandPath(new TreePath(path).pathByAddingChild(obj));
            }
        }
    }

    public void changedTree() {
        changedTreeRecursively(new TreePath(treeModel.getRoot()));
    }

    private void changedTreeRecursively(TreePath path) {
        treeModel.fireTreeNodesChanged(new TreeModelEvent(this, path));

        Object root = path.getLastPathComponent();
        for (int i = 0; i < treeModel.getChildCount(root); i++) {
            changedTreeRecursively(path.pathByAddingChild(treeModel.getChild(root, i)));
        }
    }

    public void refreshTree(TreePath path) {
        treeModel.fireTreeStructureChanged(new TreeModelEvent(this, path));
    }

    public void selectObject(Object task) {
        TreePath path = pathToObject(task).pathByAddingChild(task);
        tree.setSelectionPath(path);
    }

    protected TreePath pathToObject(Object task) {
        if (task == treeModel.getRoot()) {
            return new TreePath(treeModel.getRoot());
        }

        return new TreePath(treeModel.findPathToObject(treeModel.getRoot(), task));
    }

    public TreePath[] getSelections() {
        return tree.getSelectionPaths();
    }

    public void setSelections(TreePath[] treePaths) {
        for (TreePath path : treePaths) {
            tree.setSelectionPath(path);
        }
    }

    public void expandAll() {
        TreeUtil.expandAll(tree);
    }

    public void collapseAll() {
        //Do no use TreeUtil.collapseAll(...) since it assumes all the objects in a tree are instances of javax.swing.tree.DefaultMutableTreeNode.
        int row = tree.getRowCount() - 1;
        while (row >= 0) {
            tree.collapseRow(row);
            row--;
        }
    }

    public void taskChanged(ITask task) {
        treeModel.handleChangeTaskEvent(new TaskChangeEvent(task.getParent(), task, treeModel.getIndexOfChild(task.getParent(), task)));
    }
}