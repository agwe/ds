package ino.edu;

/**
 * Created by lara on 16/07/15.
 */
class NoServerException extends Exception
{
    //Parameterless Constructor
    public NoServerException() {}

    //Constructor that accepts a message
    public NoServerException(String message)
    {
        super(message);
    }
}
