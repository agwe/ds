package edu.innopolis;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by lara on 16/06/15.
 */
public class Master {
    DatagramSocket datagramSocket;
    BufferedReader in;
    final String time = "time";
    int clients = 0;
    long avg = 0;

    public Master(int port, int d) throws IOException {

        datagramSocket = new DatagramSocket(port);
        System.out.println("Server is running on  " + datagramSocket.getLocalSocketAddress());

        //registerClient();
        masterWrite();
    }

    public void registerClient() {
        Thread registerClient = new Thread() {
            String client;
            public void run() {
                while (true) {
                    try {
                        byte[] bufq = new byte[256];
                        DatagramPacket packet = new DatagramPacket(bufq, bufq.length);
                        datagramSocket.receive(packet);
                        client = new String(packet.getData());
                        if (client == "client"){
                            clients++;
                            System.out.println("[Server] New client is registered");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        registerClient.start();
    }

    public void masterWrite() {
        Thread masterWrite = new Thread() {
            public void run() {
                try {
                    datagramSocket.setSoTimeout(5000);
                } catch (SocketException e) { }
                while (true) {
                    try {
                        System.out.println("[Server] Requesting time from clients...");

                        byte[] buf = new byte[256];
                        buf = time.getBytes();

                        InetAddress address = InetAddress.getByName("230.0.0.1");
                        int port = 1234;
                        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);

                        datagramSocket.send(packet);

                        masterRead();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        masterWrite.start();
    }
    public void masterRead() {

        Thread masterRead = new Thread() {
            public void run() {
                try {
                    datagramSocket.setSoTimeout(20000);
                } catch (SocketException e) { }
                while (true) {
                    try {
                        byte[] buf = new byte[256];
                        DatagramPacket packet = new DatagramPacket(buf, buf.length);
                        long currentTime = System.currentTimeMillis();

                        datagramSocket.receive(packet);


                        DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
                        long milliSeconds= Long.parseLong(new String(packet.getData(), 0, packet.getLength()));
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(milliSeconds);

                        System.out.println("[Server] Client " + packet.getSocketAddress() + " time is " + formatter.format(calendar.getTime()));


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        masterRead.start();
    }

}

//MulticastSocket multicastSocket;
//File file = new File(clients);
//in = new BufferedReader(new FileReader(file));