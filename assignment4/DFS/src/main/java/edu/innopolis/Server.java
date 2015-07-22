package edu.innopolis;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lara on 15/07/15.
 */
public class Server {
    static Logger logger = Logger.getLogger(String.valueOf(Server.class));


    public static void main( String[] args ) throws SocketException, UnknownHostException {
        String res;
        Boolean running = true;
        running = parseUserArguments(args);
        if (running) {
            ServerThread server = new ServerThread(args[0]);
            logger.info("[Server]: Connection on " + args[0] + " is established");
        }

        while (running){


        }
    }

    public static Boolean parseUserArguments(String [] args){
        if (args.length==1 && validateIp(args[0])){
                return true;
        } else {
            logger.severe("Please, check your arguments string");
            return false;
        }
    }

    public static Boolean validateIp(String ipAddress){
        String IPADDRESS_PATTERN =
                "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}):(\\d{1,5})";

        Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
        Matcher matcher = pattern.matcher(ipAddress);
        if (matcher.find()) {
            return true;
        }
        else{
            return false;
        }
    }
}




            /*BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                res = reader.readLine();
                if (res.equals("exit")){
                    running = false;
                    logger.info("[Client]: Bye!");
                } else
                    parseUserInput(res);
            } catch (IOException e) {
                e.printStackTrace();
            }*/