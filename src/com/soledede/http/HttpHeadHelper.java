package com.soledede.http;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Http协议帮助类
 * 
 * HttpWrapFactory将每个行为具体落实又委托给帮助类HttpHeadHelper实现，
 * 在HttpHeadHelper中具体实现数据正文和HTTP协议头部组合过程，
 * 以及从数据包中分离开HTTP协议头部以及数据正文等过程。
 */
public class HttpHeadHelper {

  private final static String module = HttpHeadHelper.class.getName();

  public final static String ENCODING = "UTF-8";

  public final static String NAME_contentType =
      "Content-Type";
  public final static String REQ_STRING_contentType =
      "application/x-www-form-urlencoded";
  public final static String REQ_OBJECT_contentType =
      "application/octet-stream";

  public final static String RES_STRING_contentType =
      "text/html";
  public final static String RES_OBJECT_contentType =
      "application/octet-stream";

  private final static String HEAD_11 = "HEAD_11";
  private final static String HEAD_12 = " HEAD_12";
  private final static String HEAD_13 = " HEAD_13";
  private final static String SPLIT = " Split_index";

  public static byte[] assembleRequest(byte[] bytes) {
    byte[] result = null;
    try {
      String header = getPOSTHeader("", 0);
      byte[] headers = header.getBytes("UTF-8");
      int length = bytes.length + headers.length;
      result = new byte[length];
      System.arraycopy(headers, 0, result, 0, header.length());
      System.arraycopy(bytes, 0, result, headers.length, bytes.length);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }

  public static String getGETHeader() {
    StringBuffer buffer = new StringBuffer("GET ");
    buffer.append("/HttpHeader");
    buffer.append(" HTTP/1.0\r\n");
    buffer.append(getBaseHeads());
    buffer.append("\r\n");
    return buffer.toString();
  }

  public static String getPOSTHeader(String contentType, int contentLength) {
    StringBuffer buffer = new StringBuffer("POST").append(" ");
    buffer.append("/HttpHeader").append(" ").append("HTTP/1.0").append("\r\n");
    buffer.append(getBaseHeads());
    if (contentType.equals(""))
      contentType = REQ_STRING_contentType;
    buffer.append("Content-type:").append(contentType).append("\r\n");

    if (contentLength != 0)
      buffer.append("Content-length:").append(contentLength).append("\r\n");
    buffer.append("\r\n");
    return buffer.toString();

  }

  private static String getBaseHeads() {
    StringBuffer buffer = new StringBuffer("User-Agent:ChatClient\r\n");
    buffer.append("Accept:www/source; text/html; image/gif; */*\r\n");
    return buffer.toString();
  }

  public static byte[] assembleResponse(byte[] bytes) {
    byte[] result = null;
    try {
      String header = getResponseHeader("", 0);
      byte[] headers = header.getBytes("UTF-8");
      int length = bytes.length + headers.length;
      result = new byte[length];
      System.arraycopy(headers, 0, result, 0, header.length());
      System.arraycopy(bytes, 0, result, headers.length, bytes.length);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }

  public static String getResponseHeader(String contentType, int statusCode) {
    if (statusCode == 0)
      statusCode = 200;
    if (contentType.equals(""))
      contentType = RES_STRING_contentType;

    StringBuffer buffer = new StringBuffer("HTTP/1.1").append(" ");
    buffer.append(statusCode).append(" ").append("OK").append("\r\n");
    Date d = new Date(System.currentTimeMillis());
    buffer.append("Date:").append(d.toGMTString()).append("\r\n");
    buffer.append("Server:").append(" TCP/UDP Server").append("\r\n");
    buffer.append("Connection:").append("close").append("\r\n"); ;
    buffer.append(NAME_contentType).append(":").append(contentType).append(
        "\r\n");
    buffer.append("\r\n");
    return buffer.toString();
  }

  /**
   * 分析Http信息，分离头部和内容两部分
   * 头部信息保存在Map中。
   * @param bytes
   * @return
   * @throws java.lang.Exception
   */
  public static Map parse(byte[] bytes) throws Exception {
    try {
      Map map = new HashMap();

      ByteArrayInputStream in =
          new ByteArrayInputStream(bytes);

      //分解头部放入header中

      byte header[] = new byte[bytes.length];
      int count = 0;
      int index = 0;
      int word = -1;
      boolean begin = false;
// 解析第一行
      while ( (word = in.read()) != -1) {
        index++;
        if (word == '\r') {
          word = in.read();
          header[count++] = (byte) word;
          index++;
          if (word == '\n') {
            word = in.read();
            index++;
            if (word == '\r') {
              index++;
              word = in.read();
              if (word == '\n') {

//                System.out.println("index=" + index);
                break;
              }

            }
          }
        }
        header[count++] = (byte) word;
      }
      //记录下从byte数组第几个开始是正文部分
      map.put(SPLIT, new Integer(index));

      //以下是提取头部的信息
      InputStreamReader inr = new InputStreamReader(new ByteArrayInputStream(
          header));

      BufferedReader rd = new BufferedReader(inr);
      //第一行头部信息
      String w = rd.readLine();
      String[] strings = w.split(" ");
      map.put(HEAD_11, strings[0]);
      map.put(HEAD_12, strings[1]);
      map.put(HEAD_13, strings[2]);

      //一直查询到Http的空行。
      while ( (w = rd.readLine()) != null) {
        if (w.indexOf(":") != -1) {
          String[] strings2 = w.split(":");
          map.put(strings[0], strings[1]);
//          System.out.println(" key=" + strings2[0]);
//          System.out.println(" value=" + strings2[1]);
        }
      }

      return map;
    } catch (Exception ex) {
      throw new Exception(module + " parse() " + ex);
    }
  }

  /**
   * 获取Http信号中的内容部分
   * @param bytes
   * @return
   * @throws java.lang.Exception
   */
  public static byte[] getContent(byte[] bytes) throws Exception {
    try {
      Map map = parse(bytes);
      int index = ( (Integer) map.get(SPLIT)).intValue();

      int length = bytes.length - index;
      byte[] result = new byte[length];

//      System.out.println(" new index =" + length);
      System.arraycopy(bytes, index, result, 0, length);
      return result;

    } catch (Exception ex) {
      throw new Exception(module + "getContent" + ex);
    }
  }

  public static byte[] getContent(byte[] bytes, Map map) throws Exception {
    try {
      int index = ( (Integer) map.get(SPLIT)).intValue();

      int length = bytes.length - index;
      byte[] result = new byte[length];

//      System.out.println(" new index =" + length);
      System.arraycopy(bytes, index, result, 0, length);
      return result;

    } catch (Exception ex) {
      throw new Exception(ex);
    }
  }

  private static String getNoSpaces(String sr) {
    String newString = null;
    if (sr.indexOf(" ") == 0) {
      sr = sr.substring(1);
    }
    int maxIndex = sr.length() - 1;
    if (sr.lastIndexOf(" ") == maxIndex)
      newString = sr.substring(0, maxIndex);

    return newString;
  }

  public static void main(String[] args) throws Exception {

//    String HttpPOSTHeader = "HTTP/1.1 200 OK\r\n";
//    String HttpPOSTHeader2 = "Date: Tue, 14 Sep 1999 02:19:57 GMT\r\n";
//    String HttpPOSTHeader3 = "\r\n";
//    String HttpPOSTHeader4 = "Server: Apache/1.2.6";

    String content = "I am Peng";
    byte[] result2 = HttpHeadHelper.assembleResponse(content.getBytes("UTF-8"));
//       byte[] result2 = HttpHeadHelper.assembleRequest(content.getBytes("UTF-8"));

    byte[] reqContent = HttpHeadHelper.getContent(result2);

    System.out.print("reqContent==" + new String(reqContent) + "==");

  }

}