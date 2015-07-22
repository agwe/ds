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

    public static void parseUserInput(String request){
        String response;
        List<String> arrResponse;
        String cmd[] = request.split(" ");
        if (contains(cmd[0])) {
            if(cmd.length!=2){
                logger.info("[Client]: Please specify the correct argument. ");
            } else {
                String arg = cmd[1];
                Commands c = Commands.valueOf(cmd[0]);
                switch (c) {
                    case cat:
                        if (true) {
                            response = master.openFile(arg);
                            if (response.equals("NO_FILE)")){
                                logger.info("[Client]: No file with the name specified was found");
                                response = master.createFile(arg);
                            }
                        } else {
                           logger.info("[Client]: Opening " + arg + "...");
                           logger.info(response);
                        }
                        break;
                    case rm:
                        response = master.deleteFile(arg);
                        if (response.equals("NO_FILE)")){
                            logger.info("[Client]: No file with the name specified was found");
                        } else {
                            logger.info("[Client]: File " + arg + " was deleted");
                        }
                        break;
                    case rmdir:
                        response = master.deleteDirectory(arg);
                        if (response.equals("NO_DIRECTORY)")){
                            logger.info("[Client]: No directory with the name specified was found");
                        } else {
                            logger.info("[Client]: Directory " + arg + " was deleted");
                        }
                        break;
                    case mkdir:
                        response = master.createFolder(arg);
                        if (response.equals("OK")){
                            logger.info("[Client]: Directory " + arg + " was created");
                        }
                        break;
                    case cd:
                        response = master.changeDirectory(arg);
                        if (response.equals("OK")){
                            logger.info("[Client]: Current directory is " + arg);
                        }
                        else {
                            logger.info("[Client]: No directory with the name specified was found");
                        }
                        break;
                    case ls:
                        arrResponse = master.listDirectoryFiles(arg);
                        if (!arrResponse.isEmpty()){
                            logger.info("Files:");
                            for (String line : arrResponse){
                                logger.info(line);
                            }
                        }
                        arrResponse = master.listDirectoryDirectories(arg);
                        if (!arrResponse.isEmpty()){
                            logger.info("Directories:");
                            for (String line : arrResponse){
                                logger.info(line);
                            }
                        }
                        break;
                    case stat:
                        arrResponse = master.getInfo(arg);
                        if (!arrResponse.isEmpty()){
                            logger.info(arg+ " parameters:");
                            for (String line : arrResponse){
                                logger.info(line);
                            }
                        }
                        break;
                }
            }
        } else if (cmd[0].equals("help")){
            logger.info(""); //todo
        } else {
            logger.info("Unknown command");
        }
    }

    public static Boolean parseUserArguments(String args[]){
        if (args.length==2){
            String[] clientAddress = args[1].split(":");
            if (clientAddress.length==2 && args[0]instanceof String) {
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
        ls,
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
