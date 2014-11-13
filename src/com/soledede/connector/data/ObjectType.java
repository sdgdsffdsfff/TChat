package com.soledede.connector.data;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Visitable具体子类
 */

public class ObjectType implements Linkable {

  private Object content = null;
  private int msgType;
  private ByteBuffer byteBuffer = null;

  public ObjectType(int msgType) {
    this.msgType = msgType;
  }

  public Object getContent() {
    return content;
  }
  public void setContent(Object content){
     this.content = content;
  }

  public void accpet(QueueWorker worker) throws Exception {
    worker.run(msgType, this);
  }

  public OutputStream getOutputStream() {
    OutputStream outputStream = null;
    try {
      outputStream = DataTypeHelper.writeObject(content);
    } catch (Exception ex) {
    }
    return outputStream;
  }

  public void setInputStream(InputStream in) {
    try {
      this.content = DataTypeHelper.getObject(in);
    } catch (Exception ex) {
    }
  }

}
