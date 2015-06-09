package edu.innopolis;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client {
    private Socket socket = null;
    private InputStream inStream = null;
    private OutputStream outStream = null;

    public Client() {

    }

    public void createSocket() {
        try {
            socket = new Socket("localHost", 3339);
            System.out.println("Connected");
            inStream = socket.getInputStream();
            outStream = socket.getOutputStream();
            createReadThread();
            createWriteThread();
        } catch (UnknownHostException u) {
            u.printStackTrace();
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
                            String receivedMessage = new String(arrayBytes, "UTF-8");
                            System.out.println("Received message :" + receivedMessage);
                        }/* else {
                            // notify();
                        }*/
                        ;
                        //System.arraycopy();
                    }catch (SocketException se){
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
                                outStream.write(typedMessage.getBytes("UTF-8"));
                            }
                        }
                        ;
                        //System.arraycopy();

                    } catch (IOException i) {
                        i.printStackTrace();
                    }
                }
            }
        };
        writeThread.setPriority(Thread.MAX_PRIORITY);
        writeThread.start();
    }

    public static void main(String[] args) throws Exception {
        Client myChatClient = new Client();
        myChatClient.createSocket();
        /*myChatClient.createReadThread();
        myChatClient.createWriteThread();*/
    }
}
