package com.soledede.connector.data;

import java.io.OutputStream;

import com.soledede.connector.queue.MessageQueue;

/**
 * 一个访问者具体子类 将应用对象序列化为Stream后放入queue
 */
public class QueueAddWorker implements QueueWorker{

    private MessageQueue messageQueue = null;

    public QueueAddWorker(MessageQueue messageQueue){
           this.messageQueue = messageQueue;
    }

    public void run(int msgType, Linkable object) throws Exception {
      OutputStream outputStream = object.getOutputStream(); //将上层应用对象序列化为Stream
      if (msgType == REQUEST) {
        messageQueue.pushRequest(outputStream);
      } else if (msgType == RESPONSE) {
        messageQueue.pushResponse(outputStream);
      }
    }

}