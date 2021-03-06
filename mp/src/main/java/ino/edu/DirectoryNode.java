package ino.edu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lara on 15/07/15.
 */
public class DirectoryNode extends TreeNode{
    private ArrayList<TreeNode> children;

    public DirectoryNode(String name) {
        this.setName(name);
        this.children = new ArrayList();
    }

    public List<TreeNode> getChildren() {
        if (children!=null){
            return children;
        } else {
            return new ArrayList<TreeNode>();

        }
    }

    public ArrayList<TreeNode> getChildrenWithName(String name){
        if (children!=null) {
            for (TreeNode node : children) {
                if (node.getName().equals(name)) {
                    ArrayList<TreeNode> nodes = new ArrayList<TreeNode>();
                    nodes.add(node);
                    return nodes;
                }
            }
        }
        return new ArrayList<TreeNode>();
    }

    public ArrayList<TreeNode> getChildDirectoryByName(String name){
        if (this.children != null) {
            for (TreeNode node : children) {
                if (node.getName().equals(name) && node instanceof DirectoryNode) {
                    ArrayList<TreeNode> nodes = new ArrayList<TreeNode>();
                    nodes.add(node);
                    return nodes;
                }

            }
        }
        return new ArrayList<TreeNode>();
    }

    public ArrayList<TreeNode> getChildFileByName(String name){
        if (children!=null) {
            for (TreeNode node : children) {
                if (node.getName().equals(name) && node instanceof FileNode) {
                    ArrayList<TreeNode> nodes = new ArrayList<TreeNode>();
                    nodes.add(node);
                    return nodes;
                }
            }
        }
        return new ArrayList<TreeNode>();
    }

    /**
     * recursively get all child files from the specified directory
     * @return ArrayList of all child files
     */
    public ArrayList<FileNode> getAllChildFilesRecursively(){
        ArrayList<FileNode> allFiles = new ArrayList();
        for (TreeNode node : this.getChildren()){
            if (node instanceof FileNode) {
                allFiles.add((FileNode) node);
            }
            else {
                if (node instanceof DirectoryNode) {
                    ((DirectoryNode) node).getAllChildFilesRecursively();
                }
            }
        }
        return allFiles;
    }

    public void setChildren(TreeNode child) {
        this.children.add(child);
    }

    public Boolean ifChildWithNameExist(String name) {
        if (this.children!=null) {
            for (TreeNode node : children) {
                if (node.getName().equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Boolean ifChildFileExist(String name) {
        if (children!=null) {
            for (TreeNode node : children) {
                if (node.getName() == name && node instanceof FileNode) {
                    return true;
                }
            }
        }
        return false;
    }

    public Boolean ifChilDirectoryExist(String name) {
        if (children!=null) {
            for (TreeNode node : children) {
                if (node.getName().equals(name) && node instanceof DirectoryNode) {
                    return true;
                }
            }
        }
        return false;
    }

    public Boolean ifChildrenWithHashExist(String hash) {
        if (children!=null) {
            for (TreeNode node : children) {
                if (node.getHash() == hash) {
                    return true;
                }
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
        if (children!=null) {
            return this.children.remove(getChildrenWithName(name).get(0));
        }
        else
            return false;
    }

    @Override
    public List getInfo() {
        ArrayList<String> nodeParameters = new ArrayList();
        nodeParameters.add("Directory name: " + this.getName());
        nodeParameters.add("Parent directory: " + this.getParent().getName());
        // todo return children
        return nodeParameters;

    }
}