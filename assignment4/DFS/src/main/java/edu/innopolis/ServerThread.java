package edu.innopolis;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Created by lara on 16/07/15.
 */
public class ServerThread {
    static Logger logger = Logger.getLogger(String.valueOf(Server.class));
    DatagramSocket datagramSocket;

    int serverPort;
    InetAddress serverAddress;
    String [] serverFullAddress;
    HashMap<String,String> storedData;
    DatagramPacket packet;

    public ServerThread(String args) throws SocketException, UnknownHostException {
        //initialize server
        serverFullAddress = args.split(":");
        serverAddress = InetAddress.getByName(serverFullAddress[0]);
        serverPort = Integer.parseInt(serverFullAddress[1]);
        datagramSocket = new DatagramSocket(null);
        InetSocketAddress socketAddress = new InetSocketAddress(serverAddress, serverPort);
        datagramSocket.bind(socketAddress);

        //initialize data storage
        storedData = new HashMap<>();

        //create server thread
        createServerThread();

    }
//request format <command:+:hash:+:value>
//e.g: "put:+:1bc29b36f623ba82aaf6724fd3b16718:+:bla-bla-bla\nla-la-la-la\nblum-blum"
    public void createServerThread() {
        Thread serverThread = new Thread() {
            public void run() {
                while (true) {
                    byte[] buf = new byte[256];
                    packet = new DatagramPacket(buf, buf.length);

                    try {
                        datagramSocket.receive(packet);
                        String[] request = (new String(packet.getData(), 0, packet.getLength())).split(":+:");
                        String command = request[0];

                        if (contains(command)) {
                            String hash = request[1];
                            String value = "";
                            Commands c = Commands.valueOf(command);
                            switch (c) {
                                case put:
                                    value = request[2];
                                    if (value!=null) {
                                        if (storedData.containsKey(hash)){
                                            storedData.remove(hash);
                                            storedData.put(hash, value);
                                        } else {
                                            storedData.put(hash, value);
                                        }
                                        buf = "ok".getBytes();
                                    } else {
                                        buf = "null".getBytes();
                                    }

                                    break;
                                case rm:
                                    value = storedData.get(hash);
                                    if (value!=null){
                                        storedData.remove(hash);
                                        buf = "ok".getBytes();
                                    } else {
                                        buf = "null".getBytes();
                                    }

                                    break;
                                case get:
                                    value = storedData.get(hash);
                                    if (value!=null){
                                        buf = value.getBytes();
                                    } else {
                                        buf = "null".getBytes();
                                    }
                                    break;
                                default:
                                    buf = "null".getBytes();
                            }
                        } else {
                            buf = "null".getBytes();
                        }
                        packet = new DatagramPacket(buf, buf.length, packet.getAddress(), packet.getPort());
                        datagramSocket.send(packet);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        serverThread.setPriority(Thread.MAX_PRIORITY);
        serverThread.start();
    }

    public enum Commands {
        get,
        put,
        rm
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
