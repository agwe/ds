package edu.innopolis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.util.List;
import java.util.logging.Logger;


/*
1. Parse arguments (ip:port serverlist)
2. Initialize master component
3. Parse commands
*/

public class Client
{
    static Logger logger = Logger.getLogger(String.valueOf(Client.class));
    static Master master;
    //Utility u = new Utility();

    /**
     * program entry point
     * @param args - args[0] - client address in format "ip:port", args[1] - txt file contains servers addresses in the same format
     * @throws SocketException
     */
    public static void main( String[] args ) throws SocketException {
        String res;
        Boolean running = true;
        running = parseUserArguments(args);
        if (running) {
            String[] clientAddress = args[0].split(":");
            master = new Master(args[1], clientAddress);

            logger.info("[Client]: Waiting for your command");
            logger.info("[Client]: Type help to get more info");
        }

        while (running){
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                res = reader.readLine();
                if (res.equals("exit")){
                    running = false;
                    logger.info("[Client]: Bye!");
                } else
                    parseUserInput(res);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * parse commands specified for working wth file system (cat, rm, cd etc...)
     * @param request - String contains of command and argument. like "cat testfile test" or "ls dir" etc
     * @throws SocketException
     */
    public static void parseUserInput(String request) throws SocketException {
        String response;
        List<String> arrResponse;
        String cmd[] = request.split(" ");
        if (contains(cmd[0])) {
            if(cmd.length<2 || cmd.length>3){
                logger.info("[Client]: Please specify the correct argument. ");
            } else {
                String arg = cmd[1];
                Commands c = Commands.valueOf(cmd[0]);
                switch (c) {
                    case cat:
                        if (master.ifChildFileExist(arg)) {
                            if (cmd.length == 3) {
                                master.createFile(arg, cmd[2]);

                            } else
                                response = master.openFile(arg);
                        } else {
                            if (cmd.length == 3) {
                                //response = master.createFile(arg, cmd[2]);
                            }
                        } else {

                        }


                        if (response.equals("NO_FILE")) {
                            logger.info("[Client]: No file with the name specified was found");
                            if (cmd.length == 3) {
                                response = master.createFile(arg, cmd[2]);
                            } else {
                                logger.info("[Client]: Please specify the correct value argument if you want to create a new file. ");
                            }
                        } else {
                            logger.info("[Client]: Opening " + arg + "...");
                            logger.info(response);
                        }
                        break;
                    case rm:
                        response = master.deleteFile(arg);
                        if (response.equals("NO_FILE")) {
                            logger.info("[Client]: No file with the name specified was found");
                        } else {
                            logger.info("[Client]: File " + arg + " was deleted");
                        }
                        break;
                    case rmdir:
                        response = master.deleteDirectory(arg);
                        if (response.equals("NO_DIRECTORY")) {
                            logger.info("[Client]: No directory with the name specified was found");
                        } else {
                            logger.info("[Client]: Directory " + arg + " was deleted");
                        }
                        break;
                    case mkdir:
                        response = master.createDirectory(arg);
                        if (response.equals("OK")) {
                            logger.info("[Client]: Directory " + arg + " was created");
                        }
                        break;
                    case cd:
                        if (arg.equals("..")) {
                            response = master.changeDirectoryToParent();
                            if (!response.equals("NO_DIRECTORY")) {
                                logger.info("[Client]: Current directory is " + response);
                            } else {
                                logger.info("[Client]: No directory with the name specified was found");
                            }
                        } else if (arg.equals(".")) {
                            master.changeDirectoryToRoot();
                            logger.info("[Client]: Current directory is root");
                        } else {
                            response = master.changeDirectory(arg);
                            if (response.equals("OK")) {
                                logger.info("[Client]: Current directory is " + arg);
                            } else {
                                logger.info("[Client]: No directory with the name specified was found");
                            }
                        }
                        break;
                    case stat:
                        arrResponse = master.getInfo(arg);
                        if (arrResponse!=null){
                            logger.info(arg+ " parameters:");
                            for (String line : arrResponse){
                                logger.info(line);
                            }
                        } else {
                            logger.info("[Client]: No file or directory with the name specified was found");
                        }
                        break;
                }
            }
        } else if (cmd[0].equals("help")){
            logger.info("Sorry, I won't help :)"); //todo commands help
        } else if (cmd[0].equals("ls")) {
            arrResponse = master.listDirectoryFiles();
            if (arrResponse!=null){
                logger.info("Files:");
                for (String line : arrResponse){
                    logger.info(line);
                }
            } else {
                logger.info("[Client]: No child files were found");
            }
            arrResponse = master.listDirectoryDirectories();
            if (arrResponse!=null){
                logger.info("Directories:");
                for (String line : arrResponse){
                    logger.info(line);
                }
            } else {
                logger.info("[Client]: No child directories were found");
            }
        } else {
            logger.info("Unknown command");
        }
    }

    /**
     * check user input arguments correctness
     * @param args
     * @return
     */
    public static Boolean parseUserArguments(String args[]){
        if (args.length==2){
            String[] clientAddress = args[0].split(":");
            if (clientAddress.length==2 && args[1]instanceof String) {
                return true;
            } else {
                logger.severe("Wrong format of arguments");
                return false;
            }
        } else {
            logger.severe("Wrong number of arguments");
            return false;
        }
    }

    public enum Commands {
        cat,
        rm,
        mkdir,
        rmdir,
        cd,
        stat
    }

    public static boolean contains(String test) {

        for (Commands c : Commands.values()) {
            if (c.name().equals(test)) {
                return true;
            }
        }

        return false;
    }
}
