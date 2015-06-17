package edu.innopolis;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Calendar;
import java.util.Objects;

/**
 * Created by lara on 16/06/15.
 */
public class Slave{
    MulticastSocket multicastSocket;
    InetAddress group;
    int serverPort = 4445;
    InetAddress serverAddress = InetAddress.getLocalHost();
    //byte[] bufq = new byte[256];

    Calendar localTime;
    String message;
    //SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");

    public Slave(int socket, int millisec) throws IOException {
        //initialize variables
        multicastSocket = new MulticastSocket(1234);
        group = InetAddress.getByName("230.0.0.1");
        multicastSocket.joinGroup(group);

        /*bufq = "client".getBytes();
        DatagramPacket packet = new DatagramPacket(bufq, bufq.length, serverAddress, serverPort);
        mu1lticastSocket.send(packet);*/

        //initialize threads
        readThread();

        //initialize local time
        localTime = Calendar.getInstance();
        localTime.getTimeInMillis();
        localTime.add(Calendar.MILLISECOND, millisec);
    }

    public void readThread() {
        Thread readThread = new Thread() {
            public void run() {
                System.out.println("[Client]: I'm ready");
                while (true) {
                    try {
                        byte[] buf = new byte[256];
                        DatagramPacket packet = new DatagramPacket(buf, buf.length);

                        //receiving packet from server
                        multicastSocket.receive(packet);
                        System.out.println("[Client]: I got a packet");
                        serverPort = packet.getPort();
                        serverAddress = packet.getAddress();
                        message = new String(packet.getData(), 0, packet.getLength());

                        //sending localtime back to server
                        if (message.equals("time")) {
                            System.out.println("[Client]: Server is requesting time");
                            String time = Objects.toString(localTime.getTimeInMillis(), null);
                            buf = time.getBytes();

                            packet = new DatagramPacket(buf, buf.length, serverAddress, serverPort);
                            multicastSocket.send(packet);
                            System.out.println("time: " + localTime.getTime() + " address: " + serverAddress + " port: " + serverPort);
                        }
                        //changing current time
                        else {
                            writeThread(message);
                        }
                        message = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        readThread.start();
    }

    public void writeThread(final String localMessage) {
        Thread writeThread = new Thread() {
            private volatile boolean running = true;

            public void terminate() {
                running = false;
            }

            public void run() {
                while (running) {
                    try {
                        System.out.println("Correcting clocks..");
                        System.out.println("Client old time is ... " + localTime.getTime());
                        localTime.add(Calendar.MILLISECOND, Integer.parseInt(localMessage));//.valueOf(packet.getData())));
                        System.out.println("Client current time is ... " + localTime.getTime());
                        running = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        writeThread.start();
    }
}
