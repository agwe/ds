package edu.innopolis;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException {
        if (args[0].equals("m"))
            new Master(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        else if (args[0].equals("s"))
            new Slave(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
    }
}
