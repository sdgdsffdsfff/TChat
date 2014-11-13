package com.soledede.connector.data;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Visitable具体子类
 */
public class StringType implements Linkable {

  private String content = null;
  private int msgType;
  private ByteBuffer byteBuffer = null;

  public StringType(int msgType) {
    this.msgType = msgType;
  }

  public String getContent() {
    return content;
  }
  public void setContent(String content){
     this.content = content;
  }

  public void accpet(QueueWorker worker) throws Exception {
    worker.run(msgType, this);
  }

  public OutputStream getOutputStream() {
    OutputStream outputStream = null;
    try {
      outputStream = DataTypeHelper.writeString(content); //将String转换成ByteArrayOutputStream
    } catch (Exception ex) {
    }
    return outputStream;
  }

  public void setInputStream(InputStream in) {
    try {
      this.content = DataTypeHelper.getString(in);//将ByteArrayInputStream转换成String
    } catch (Exception ex) {
    }
  }

}