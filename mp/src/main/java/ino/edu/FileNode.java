package ino.edu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lara on 15/07/15.
 */
public class FileNode extends TreeNode{
    private String pathToServer;
    private String hash;

    public FileNode(String name, DirectoryNode parent) {
        this.setName(name);
        this.setParent(parent);
        this.hash = String.valueOf(getNodePath().hashCode());
    }

    public String getPathToServer() {
        return pathToServer;
    }

    public String getHash() {
        return hash;
    }

    public void setPathToServer(String pathToServer) {
        this.pathToServer = pathToServer;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public List getInfo() {
        ArrayList<String> nodeParameters = new ArrayList();
        nodeParameters.add("File name: " + this.getName());
        nodeParameters.add("File parent directory: " + this.getParent().getName());
        nodeParameters.add("File path to the server: " + this.getPathToServer());
        return nodeParameters;
    }
}
