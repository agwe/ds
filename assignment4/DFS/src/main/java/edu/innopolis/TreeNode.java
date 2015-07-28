package edu.innopolis;

import java.util.List;

/**
 * Created by lara on 15/07/15.
 */
public abstract class TreeNode {
        private String name;
        private String hash;
        private DirectoryNode parent;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public DirectoryNode getParent() {
        return parent;
    }

    public void setParent(DirectoryNode parent) {
        this.parent = parent;
    }

    public String getNodePath(){
        String res;
        TreeNode parent = this.getParent();
        if (parent != null){
            res = parent.getNodePath() + "/" + this.getName();
            return res;
        }
        else return "";
    }

    abstract public List getInfo();
}
