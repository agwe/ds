package edu.innopolis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

/**
 * Created by lara on 15/07/15.
 */
public class Master {
    //log4j logger
    static Logger logger = Logger.getLogger(String.valueOf(Client.class));
    ArrayList<String> sockets = new ArrayList<String>();
    Iterator<String> it;
    DatagramSocket datagramSocket;
    //file system structure
    DirectoryNode root;
    DirectoryNode currentDirectory;
    int counter;

    public void registerServers(String servers){
        try {
            BufferedReader br = new BufferedReader(new FileReader(servers));
            String line;
            while ((line = br.readLine()) != null) {
                sockets.add(line);
            }
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
    }

    /**
     * Master constructor
     * @param servers - String, file with expected servers in format ip:port
     * @param clientAddress - String[], local address in format ip:port
     * @throws SocketException
     */
    public Master(String servers, String[] clientAddress) throws SocketException {
        //initialize storage for file system structure
        //fileTree = new Tree();
        root = new DirectoryNode(".");
        currentDirectory = root;

        //register servers sockets from servers list
        registerServers(servers);

        //initialize socket on client side
        datagramSocket = new DatagramSocket(null);
        InetSocketAddress socketAddress = new InetSocketAddress(clientAddress[0], Integer.parseInt(clientAddress[1]));
        datagramSocket.bind(socketAddress);

        //set servers counter to zero
        counter = 0;
    }

    /**
     * Use RoundRobin for choosing server
     * @return String path to server
     */
    private String chooseServer(){
        String path;
        try {
            path = it.next();
            return path;
        } catch (NoSuchElementException e){
            it =  sockets.iterator();
            return it.next();
        }
    }


    public void storeData(String command, String hash, String value) throws SocketException {
        datagramSocket.setSoTimeout(10000);
        String serverAddress = chooseServer();
        try {
            //check if any server has responded
            if (!serverAddress.isEmpty()) {
                counter = 0;
                //initiaize connection with chosen server
                String[] sd = serverAddress.split(":");
                InetAddress ipAddressName = InetAddress.getByName(sd[0]);
                Integer port = Integer.valueOf(sd[1]);

                byte[] buf;
                buf = (command+":+:"+hash+":+:" +value).getBytes();
                DatagramPacket packet;
                packet = new DatagramPacket(buf, buf.length, ipAddressName, port);
                datagramSocket.send(packet);
                datagramSocket.receive(packet);
                String message;
                message = new String(packet.getData(), 0, packet.getLength());

            } else {
                counter++;
                if (counter<sockets.size()){
                    storeData(command, hash, value);
                }
                else throw new NoServerException();
            }
        }catch(NoServerException e){
            logger.severe("[Client]: Can't save a file. Filesystem is unavailable");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketTimeoutException e){
            logger.severe("[Client]: Can't save a file. No respond from filesystem");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //todo retrieving file/folder data
    public String getData(TreeNode node){
        String pathToServer = node.getPathToServer();
        if (!pathToServer.isEmpty())
        return "";
    }

    /**
     * create file in current directory
     * @param name - file name
     * @param value - file data
     * @return String answer OK or FILE_EXIST
     * @throws SocketException
     */
    public response createFile(String name, String value) throws SocketException {
        if (currentDirectory.ifChildFileExist(name)){
            return response.FILE_EXIST;
        }
        else {
            FileNode node = new FileNode(name);
            storeData("put", node.getHash(), node.getName());
            currentDirectory.setChildren(node);
        }
        return response.OK;
    }

    public String openFile(String name){
        ArrayList<TreeNode> node = currentDirectory.getChildFileByName(name);
        if (node!=null){
            return getData(node.get(0));
        }
        else return String.valueOf(response.NO_FILE);
    }

    /**
     * delete requested directory if it is child of current
     * @param name - String, directory name
     * @return "OK", if deleted, FAIL - otherwise
     */
    public String deleteDirectory(String name) {
        if (currentDirectory.ifChilDirectoryExist(name)){
            currentDirectory.removeChild(name);
            return String.valueOf(response.OK);
        }
        else return String.valueOf(response.NO_DIRECTORY);
    }

    /**
     * delete requested directory if it is child of current
     * @param name - String, directory name
     * @return "OK", if deleted, FAIL - otherwise
     */
    public String deleteFile(String name) {
        if (currentDirectory.ifChildFileExist(name)){
            currentDirectory.removeChild(name);
            return String.valueOf(response.OK);
        }
        else return String.valueOf(response.NO_FILE);
    }


    /**
     * create new directory in the working one
     * @param name - name of new directory
     */
    public String createFolder(String name) {
        DirectoryNode node = new DirectoryNode(name);
        currentDirectory.setChildren(node);
        node.setParent(currentDirectory);
        return String.valueOf(response.OK);
    }

    /**
     * Change directory from current to one specified in signature, if it is the true child of current
     * @param directoryName - name of directory to one user wants to make working (current)
     * @return True, if directory was changed; false. if desired directory is not the child of current
     */
    public String changeDirectory(String directoryName) {
        if (currentDirectory.ifChildWithNameExist(directoryName)) {
            ArrayList<TreeNode> tempDirectory = currentDirectory.getChildDirectoryByName(directoryName);
            if (!tempDirectory.isEmpty()){
                currentDirectory = (DirectoryNode) tempDirectory.get(0);
                return String.valueOf(response.OK);
            }
        } return String.valueOf(response.NO_DIRECTORY);
    }

    /**
     * List all children of desired directory
     * @param directoryName - String
     * @return List of children
     */
    public List<String> listDirectoryFiles(String directoryName) {
        List<TreeNode> children = currentDirectory.getChildren();
        List dirChildren = null;
        for (TreeNode node: children){
            if (node instanceof FileNode){
                dirChildren.add(node.getName());
            }
        }
        return dirChildren;
    }

    public List<String> listDirectoryDirectories(String directoryName) {
        List<TreeNode> children = currentDirectory.getChildren();
        List dirChildren = null;
        for (TreeNode node: children){
            if (node instanceof DirectoryNode){
                dirChildren.add(node.getName());
            }
        }
        return dirChildren;
    }

    /**
     * Get node info - name, parent for file; name, children for directory
     * @param name - name of file/directory
     * @return List of parameters
     */
    public List<String> getInfo(String name) {
        List nodeParameters = null;
        ArrayList<TreeNode> nodes = currentDirectory.getChildDirectoryByName(name);
            if (!nodes.isEmpty()){
                nodeParameters = nodes.get(0).getInfo();
            } else {
                nodes = currentDirectory.getChildFileByName(name);
                if (!nodes.isEmpty()) {
                    nodeParameters = nodes.get(0).getInfo();
                }
            }
        return nodeParameters;
    }

    /**
     * return current working folder from tree structure
     * @return DirectoryNode
     */
    public DirectoryNode getCurrentDirectory(){
        return currentDirectory;
    }

    public enum response {
        OK,
        FILE_EXIST,
        DIRECTORY_EXIST,
        NO_FILE,
        NO_DIRECTORY,
        TIMEOUT,
        NO_RESPONSE,
        FAIL
    }

}