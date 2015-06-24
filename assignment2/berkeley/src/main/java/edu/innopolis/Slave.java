package edu.innopolis;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by lara on 16/06/15.
 */
public class Slave{
    static Logger logger = Logger.getLogger(Slave.class);
    DatagramSocket slaveSocket;
    int serverPort;
    String[] slaveAdd;
    InetAddress serverAddress;

    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
    Date slaveTime;
    long slaveTimeDiff;

    public Slave(String address, String time, String logFile) throws IOException, ParseException {
        //initialize variables
        slaveSocket = new DatagramSocket(null);
        slaveAdd = address.split(":");
        InetSocketAddress slaveAddress = new InetSocketAddress(slaveAdd[0], Integer.parseInt(slaveAdd[1]));
        slaveSocket.bind(slaveAddress);

        //initialize local time
        slaveTime = dateFormat.parse(time);
        String currentTime = dateFormat.format(new Date());
        slaveTimeDiff = Math.abs(Master.getDateDiff(dateFormat.parse(currentTime), slaveTime, TimeUnit.MILLISECONDS));
        logger.info("[Server]: Current time: " + slaveTime.toString() + " (real time: " + currentTime + ", difference: " + slaveTimeDiff + " millisec).");

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
                            String currentTime = dateFormat.format(new Date());
                            buf = String.valueOf(dateFormat.parse(currentTime).getTime() + slaveTimeDiff).getBytes();

                            packet = new DatagramPacket(buf, buf.length, serverAddress, serverPort);
                            slaveSocket.send(packet);
                            logger.info("[Client]: Current time: " + currentTime + " address: " + serverAddress + " port: " + serverPort);
                        }
                        //changing current time
                        else {
                            writeTime(packet);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        readThread.start();
    }

    public void writeTime(final DatagramPacket packet) {
        try {
            logger.info("[Client]: Correcting clocks..");
            logger.info("[Client]: Old time is ... " + dateFormat.format(new Date()));

            long milliSeconds = Long.parseLong(new String(packet.getData(), 0, packet.getLength()));
            slaveTimeDiff+=milliSeconds;
            long current = dateFormat.parse(dateFormat.format(new Date())).getTime()+slaveTimeDiff;
            logger.info("[Client]: Current time is ... " + new Date(current * 1000));
            // running = false;
            } catch (Exception e) {
                e.printStackTrace();
        }
    }

}

/*
public void writeTime(final String localMessage) {
        Thread writeThread = new Thread() {
            private volatile boolean running = true;

            public void terminate() {
                running = false;
            }

            public void run() {
                while (running) {
                    try {
                        logger.info("[Client]: Correcting clocks..");
                        logger.info("[Client]: Old time is ... " + dateFormat.format(new Date()));

                        localTime.add(Calendar.MILLISECOND, Integer.parseInt(localMessage));//.valueOf(packet.getData())));
                        logger.info("[Client]: Current time is ... " + localTime.getTime());
                        running = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        writeThread.start();
    }
 */