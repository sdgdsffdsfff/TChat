package com.soledede.connector.data;

import java.io.*;

/**
 * Visitor模式的Visitable 被访问者统一接口
 * 被访问者是那些数据对象
 */
public interface Linkable {

  public void accpet(QueueWorker worker) throws Exception; //用于接受访问者

  public OutputStream getOutputStream();

  public void setInputStream(InputStream in);

}