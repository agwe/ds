package edu.innopolis;

import java.io.IOException;
import java.text.ParseException;

public class App 
{
    public static void main( String[] args ) throws IOException, ParseException {
        if (args[0].equals("-m"))
            new Master(args[1], args[2], Integer.parseInt(args[3]), args[4], args[5]);
        else if (args[0].equals("-s"))
            new Slave(args[1], args[2], args[3]);
    }
}
