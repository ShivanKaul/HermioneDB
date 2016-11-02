package org.mcgill.ecse420.f2016;

@SuppressWarnings("serial")
public class WrongKeyFormatException extends Exception {
  public WrongKeyFormatException() { super(); }
  public WrongKeyFormatException(String message) { super(message); }
  public WrongKeyFormatException(String message, Throwable cause) { super(message, cause); }
  public WrongKeyFormatException(Throwable cause) { super(cause); }
}