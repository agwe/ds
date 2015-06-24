package edu.innopolis;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashSet;

public class Server {
    private ServerSocket severSocket = null;
    private Socket socket = null;
    private InputStream inStream = null;
    private OutputStream outStream = null;
    private static HashSet<OutputStream> outputStreams = new HashSet<OutputStream>();

    public Server() {

    }

    public void createSocket() {
        try {
            ServerSocket serverSocket = new ServerSocket(3339);
            while (true) {
                socket = serverSocket.accept();
                inStream = socket.getInputStream();
                outStream = socket.getOutputStream();
                outputStreams.add(outStream);
                System.out.println("Connected");
                createReadThread();
                createWriteThread();

            }
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public void createReadThread() {
        Thread readThread = new Thread() {
            public void run() {
                while (socket.isConnected()) {
                    try {
                        byte[] readBuffer = new byte[200];
                        int num = inStream.read(readBuffer);
                        if (num > 0) {
                            byte[] arrayBytes = new byte[num];
                            System.arraycopy(readBuffer, 0, arrayBytes, 0, num);
                            String recvedMessage = new String(arrayBytes, "UTF-8");
                            System.out.println("Received message :" + recvedMessage);
                            for (OutputStream stream : outputStreams) {
                                stream.write(recvedMessage.getBytes("UTF-8"));
                            }
                        } else {
                            notify();
                        }
                    } catch (SocketException se) {
                        System.exit(0);


                    } catch (IOException i) {
                        i.printStackTrace();
                    }
                }
            }
        };
        readThread.setPriority(Thread.MAX_PRIORITY);
        readThread.start();
    }

    public void createWriteThread() {
        Thread writeThread = new Thread() {
            public void run() {

                while (socket.isConnected()) {
                    try {
                        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
                        String typedMessage = inputReader.readLine();
                        if (typedMessage != null && typedMessage.length() > 0) {
                            synchronized (socket) {
                                for (OutputStream stream : outputStreams) {
                                    stream.write(typedMessage.getBytes("UTF-8"));
                                }
                            }
                        } else {
                            notify();
                        }
                    } catch (Exception i) {
                        i.printStackTrace();
                    }
                }
            }
        };
        writeThread.setPriority(Thread.MAX_PRIORITY);
        writeThread.start();
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.createSocket();
    }
}
