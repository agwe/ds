package edu.innopolis;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Calendar;
import java.util.Objects;

/**
 * Created by lara on 16/06/15.
 */
public class Slave{
    static Logger logger = Logger.getLogger(Slave.class);
    DatagramSocket slaveSocket;
    int serverPort;
    String[] slaveAdd;
    InetAddress serverAddress;
    Calendar localTime;

    public Slave(String address, int millisec, String logFile) throws IOException {
        //initialize variables
        slaveSocket = new DatagramSocket(null);
        slaveAdd = address.split(":");
        InetSocketAddress slaveAddress = new InetSocketAddress(slaveAdd[0], Integer.parseInt(slaveAdd[1]));
        slaveSocket.bind(slaveAddress);

        //initialize local time
        localTime = Calendar.getInstance();
        localTime.getTimeInMillis();
        localTime.add(Calendar.MILLISECOND, millisec);

        SimpleLayout layout = new SimpleLayout();
        FileAppender appender = new FileAppender(layout, logFile,false);
        //logger.addAppender(appender);

        //initialize threads
        readThread();

        logger.info("Client on " + slaveAddress.getAddress() + ":" + slaveAddress.getPort() + " is running");
    }

    public void readThread() {
        Thread readThread = new Thread() {
            public void run() {
                while (true) {
                    try {
                        byte[] buf = new byte[256];
                        DatagramPacket packet = new DatagramPacket(buf, buf.length);

                        //receiving packet from server
                        slaveSocket.receive(packet);
                        logger.info("[Client]: I got a packet");
                        serverPort = packet.getPort();
                        serverAddress = packet.getAddress();
                        String message;
                        message = new String(packet.getData(), 0, packet.getLength());

                        //sending localtime back to server
                        if (message.equals("time")) {
                            logger.info("[Client]: Server is requesting time");
                            String time = Objects.toString(localTime.getTimeInMillis(), null);
                            buf = time.getBytes();

                            packet = new DatagramPacket(buf, buf.length, serverAddress, serverPort);
                            slaveSocket.send(packet);
                            logger.info("time: " + localTime.getTime() + " address: " + serverAddress + " port: " + serverPort);
                        }
                        //changing current time
                        else {
                            writeThread(message);
                        }
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
                        logger.info("Correcting clocks..");
                        logger.info("Client old time is ... " + localTime.getTime());
                        localTime.add(Calendar.MILLISECOND, Integer.parseInt(localMessage));//.valueOf(packet.getData())));
                        logger.info("Client current time is ... " + localTime.getTime());
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
