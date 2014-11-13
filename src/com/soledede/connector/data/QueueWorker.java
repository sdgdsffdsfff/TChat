package com.soledede.connector.data;

/**
 * Visitor模式的Visitor 统一的访问者接口
 * 访问者是一个Queue操作工，它访问各种数据对象，然后实行对Queue的操作
 */
public interface QueueWorker {

  final static int REQUEST = 1;
  final static int RESPONSE = 2;

  public void run(int msgType, Linkable object) throws Exception;

}