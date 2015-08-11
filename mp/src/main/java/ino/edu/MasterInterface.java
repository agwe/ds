package ino.edu;

import java.net.SocketException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by User on 10.08.2015.
 */
public interface MasterInterface extends Remote {
    String createDirectory(String name) throws RemoteException, SocketException;

    String requestServer(String command, String hash, String pathToServer, String value) throws RemoteException, SocketException;

    String createFile(String name, String value) throws RemoteException, SocketException;

    String rewriteFile(String name, String value) throws RemoteException, SocketException;

    String openFile(String name) throws RemoteException, SocketException;

    String deleteDirectory(String name) throws RemoteException, SocketException;

    String deleteFile(String name) throws RemoteException, SocketException;

    String changeDirectory(String directoryName) throws RemoteException, SocketException;

    String changeDirectoryToParent() throws RemoteException, SocketException;

    void changeDirectoryToRoot() throws RemoteException, SocketException;

    List<String> listDirectoryFiles() throws RemoteException, SocketException;

    List<String> listDirectoryDirectories() throws RemoteException, SocketException;

    Boolean ifChildFileExist(String name) throws RemoteException, SocketException;

    List<String> getInfo(String name) throws RemoteException, SocketException;
}