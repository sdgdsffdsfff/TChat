package com.soledede.connector.data;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class DataTypeHelper {

  private final static String module = DataTypeHelper.class.getName();

  public static String getString(InputStream bin) throws Exception {
    StringBuffer sb = new StringBuffer();
    try {
      InputStreamReader in = new InputStreamReader(bin, "UTF-8");
      int buffer;
      while ( (buffer = in.read()) != -1) {
        sb.append( (char) buffer);
      }
      in.close();

    } catch (Exception ex) {
      throw new Exception(ex);
    }
    return sb.toString();
  }

  public static Object getObject(InputStream bin) throws Exception {
    try {
      ObjectInputStream is = new ObjectInputStream(bin);
      Object object = is.readObject();
      is.close();
      return object;
    } catch (Exception ex) {
      throw new Exception(ex);
    }
  }

  public static OutputStream writeObject(Object object) throws Exception {
    try {
      OutputStream outputStream = new ByteArrayOutputStream();
      //send the request query object to the server
      ObjectOutputStream oos = new ObjectOutputStream(outputStream);
      oos.writeObject(object);
      oos.close();
      return outputStream;
    } catch (Exception ex) {
      throw new Exception(ex);
    }
  }

  public static OutputStream writeString(String strs) throws Exception {
    try {
      OutputStream outputStream = new ByteArrayOutputStream();
      byte[] bytes = strs.getBytes("UTF-8");
      outputStream.write(bytes, 0, bytes.length);
      return outputStream;

    } catch (Exception ex) {
      throw new Exception(ex);
    }
  }

}