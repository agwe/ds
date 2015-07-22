package edu.innopolis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by lara on 15/07/15.
 */
public class DirectoryNode extends TreeNode{
    private List<TreeNode> children;

    public DirectoryNode(String name) {
        this.setName(name);
        this.children = null;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public ArrayList<TreeNode> getChildrenWithName(String name){
        for (TreeNode node: children){
            if (node.getName().equals(name)){
                return new ArrayList<TreeNode>((Collection<? extends TreeNode>) node);
            }
        }
        return new ArrayList<TreeNode>();
    }

    public ArrayList<TreeNode> getChildDirectoryByName(String name){
        for (TreeNode node : children) {
            if (node.getName().equals(name) && node instanceof DirectoryNode) {
                return new ArrayList<TreeNode>((Collection<? extends TreeNode>) node);
            }
        }
        return new ArrayList<TreeNode>();
    }

    public ArrayList<TreeNode> getChildFileByName(String name){
        for (TreeNode node : children) {
            if (node.getName().equals(name) && node instanceof FileNode) {
                return new ArrayList<TreeNode>((Collection<? extends TreeNode>) node);
            }
        }
        return new ArrayList<TreeNode>();
    }

    public void setChildren(TreeNode children) {
        this.children.add(children);
    }

    public Boolean ifChildWithNameExist(String name) {
        for (TreeNode node : children) {
            if (node.getName() == name) {
                return true;
            }
        }
        return false;
    }

    public Boolean ifChildFileExist(String name) {
        for (TreeNode node : children) {
            if (node.getName() == name && node instanceof FileNode) {
                return true;
            }
        }
        return false;
    }

    public Boolean ifChilDirectoryExist(String name) {
        for (TreeNode node : children) {
            if (node.getName() == name && node instanceof DirectoryNode) {
                return true;
            }
        }
        return false;
    }

    public Boolean ifChildrenWithHashExist(String hash) {
        for (TreeNode node : children) {
            if (node.getHash() == hash) {
                return true;
            }
        }
        return false;
    }

    public Boolean ifExistInTree(String name){
        for (TreeNode node : this.getChildren()){
            if (node.getName().equals(name)) {
                return true;
            }
                else {
                    if (node instanceof DirectoryNode) {
                        ((DirectoryNode) node).ifExistInTree(name);
                    }
                }
            }
            return false;
    }


    public Boolean removeChild(String name){
        return this.children.remove(getChildrenWithName(name));
    }

    @Override
    public List getInfo() {
        List<String> nodeParameters = null;
        nodeParameters.add("Directory name: " + this.getName());
        nodeParameters.add("Parent directory: " + this.getParent().getName());
        // todo return children
        return nodeParameters;

    }
}