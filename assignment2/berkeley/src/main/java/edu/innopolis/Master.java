package edu.innopolis;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by lara on 16/06/15.
 */
public class Master {
    static Logger logger = Logger.getLogger(Slave.class);
    DatagramSocket datagramSocket;

    final String time = "time";
    int clientsCount = 0;
    long delta = 0;

    ArrayList<String> sockets = new ArrayList<String>();
    ArrayList<Long> clientsTime = new ArrayList<Long>();

    public Master(String add, int d, String slaves, String logFile) throws IOException {
        //adding logger
        SimpleLayout layout = new SimpleLayout();
        FileAppender appender = new FileAppender(layout, logFile, false);
        logger.addAppender(appender);

        //creating master socket
        String[] serverAddress = add.split(":");
        datagramSocket = new DatagramSocket(null);
        InetSocketAddress socketAddress = new InetSocketAddress(serverAddress[0], Integer.parseInt(serverAddress[1]));
        datagramSocket.bind(socketAddress);
        logger.info("Server is running on  " + datagramSocket.getLocalSocketAddress());
        delta = d;

        //registering slaves;
        try {
            BufferedReader br = new BufferedReader(new FileReader(slaves));
            String line;
            while ((line = br.readLine()) != null) {
                sockets.add(line);
                clientsCount++;
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        //running writing thread
        masterWrite();
    }

    public void masterWrite() {
        try {
            datagramSocket.setSoTimeout(10000);
        } catch (SocketException e) {
            logger.info("[Server]: New round");
        }

        final Thread masterWrite = new Thread() {
            public void run() {
                while (true) {
                    try {
                        //Requesting time from clients
                        logger.info("[Server] Requesting time from clients...");
                        byte[] buf;
                        buf = time.getBytes();
                        DatagramPacket packet;
                        InetAddress address;
                        int port;

                        for (String d : sockets) {
                            String[] ipPort = d.split(":");
                            address = InetAddress.getByName(ipPort[0]);
                            port = Integer.valueOf(ipPort[1]);
                            packet = new DatagramPacket(buf, buf.length, address, port);
                            datagramSocket.send(packet);
                        }
                        masterRead();


                        //Calculating time

                        //Sending time back to clients

                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (SocketTimeoutException e) {
                        logger.info("[Server]: New round");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        };
        masterWrite.start();
    }

    public void masterRead() throws IOException {
        clientsTime.clear();
        byte[] buf = new byte[256];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        int i = 0;
        while (i != clientsCount) {
            datagramSocket.receive(packet);
            DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
            long milliSeconds = Long.parseLong(new String(packet.getData(), 0, packet.getLength()));
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliSeconds);

            logger.info("[Server] Client " + packet.getSocketAddress() + " time is " + formatter.format(calendar.getTime()));

            clientsTime.add(milliSeconds);
            i++;
        }
    }
}

    /*public void masterReadTimeout(long timeA) {
        byte[] buf = new byte[256];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        while (true) {
            try {
                datagramSocket.receive(packet);
                DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
                long milliSeconds = Long.parseLong(new String(packet.getData(), 0, packet.getLength()));
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);

                logger.info("[Server] Client " + packet.getSocketAddress() + " time is " + formatter.format(calendar.getTime()));

                if (Math.abs(timeA - milliSeconds) < delta) {
                    clientsTime.add(milliSeconds);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    final Runnable stuffToDo = new Thread() {
        @Override
        public void run() {
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

                try {
                    datagramSocket.receive(packet);
                    DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
                    long milliSeconds = Long.parseLong(new String(packet.getData(), 0, packet.getLength()));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(milliSeconds);

                    logger.info("[Server] Client " + packet.getSocketAddress() + " time is " + formatter.format(calendar.getTime()));
                    clientsTime.add(milliSeconds);
                    //if (Math.abs(timeA - milliSeconds) < delta) {
                    //  clientsTime.add(milliSeconds);
                    //}
                } catch (IOException e) {
                    e.printStackTrace();


            }
        }
    };
}

        /*Boolean isTimeout = true;
        try {
            datagramSocket.setSoTimeout(2000);
        } catch (SocketException e) {
            isTimeout = false;
        }
        clientsTime.clear();
        while (isTimeout) {
            try {
                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                long aTime = System.currentTimeMillis();
                long cTime = 0;

                datagramSocket.receive(packet);

                DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
                long milliSeconds = Long.parseLong(new String(packet.getData(), 0, packet.getLength()));
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);

                logger.info("[Server] Client " + packet.getSocketAddress() + " time is " + formatter.format(calendar.getTime()));

                // if (Math.abs(aTime - milliSeconds) < 1000) {
                //      finalTime += (aTime+cTime)/2 - milliSeconds;
                //  }
                clientsTime.add(milliSeconds);

            } catch (SocketException e1) {
                isTimeout = false;

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
                        executor.submit(new Task<Void>() {
                            protected Void call() throws TimeoutException {
                                byte[] buf = new byte[256];
                                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                                while (true) {
                                    try {
                                        datagramSocket.receive(packet);
                                        DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
                                        long milliSeconds = Long.parseLong(new String(packet.getData(), 0, packet.getLength()));
                                        Calendar calendar = Calendar.getInstance();
                                        calendar.setTimeInMillis(milliSeconds);

                                        logger.info("[Server] Client " + packet.getSocketAddress() + " time is " + formatter.format(calendar.getTime()));
                                        clientsTime.add(milliSeconds);
                                        //if (Math.abs(timeA - milliSeconds) < delta) {
                                        //  clientsTime.add(milliSeconds);
                                        //}
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        }).get(3, TimeUnit.SECONDS);

                        executor.shutdown();*/
 /*/Reading clients responses
                        final ExecutorService executor = Executors.newSingleThreadExecutor();
                        final Future future = executor.submit(stuffToDo);
                        executor.shutdown(); // This does not cancel the already-scheduled task.

                        try {
                            future.get(5, TimeUnit.SECONDS);
                        }
                        catch (InterruptedException ie) {

                        }
                        catch (ExecutionException ee) {

                        }
                        catch (TimeoutException te) {
  /* Handle the timeout. Or ignore it.
                        }
                        if (!executor.isTerminated())
                            executor.shutdownNow(); // If you want to stop the code that hasn't finished.*/

