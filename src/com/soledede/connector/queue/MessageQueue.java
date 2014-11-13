package com.soledede.connector.queue;

import java.util.LinkedList;

/**
 * 队列
 */
public class MessageQueue {

  private final static String module = MessageQueue.class.getName();

  private LinkedList requestList = new LinkedList(); //请求信号Queue
  private LinkedList responseList = new LinkedList();//响应信号Queue

  //加入数据
  public void pushRequest(Object requestMsg) {
    synchronized (requestList) {
      requestList.add(requestMsg);
      requestList.notifyAll();  //唤醒锁在requestList的其他线程
    }

  }

  //加入数据
  public void pushResponse(Object responseMsg) {
    synchronized (responseList) {
      responseList.add(responseMsg);
      responseList.notifyAll();//唤醒锁在requestList的其他线程
    }
  }

  //取出Queue中第一数据
  //在removeReqFirst()方法中，如果当前Queue中为空，就实现线程锁等待，这样节省了CPU占用时间，实现了高效率运行。当pushRequest方法被调用时，通过requestList.notifyAll()通知所有锁住requestList等线程将可以继续运行。虽然MessageQueue本身不是一个线程，但是它的方法是提供线程调用的。
  public Object removeReqFirst() {
    synchronized (requestList) {
      // 如果没有任务，就锁定在这里
      while (requestList.isEmpty()) { //为了防止Reactor在Queue中没有数据时还在不断地读取，这里使用了线程的触发机制，当Queue中为空时，读取Queue的线程处于等待暂停状态；一旦有数据放入，就触发读取线程开始读取。这样也是为了防止读取线程发生堵塞，完全独霸CPU，导致其他线程不能正常运行。
        try {
          requestList.wait(); //等待解锁 等待加入数据后的唤醒
        } catch (InterruptedException ie) {
          ie.printStackTrace();
        }
      }
      return requestList.removeFirst();
    }
  }

  //取出Queue中第一数据
  public Object removeResFirst() {
    synchronized (responseList) {
      // 如果没有任务，就锁定在这里
      while (responseList.isEmpty()) {
        try {
          responseList.wait(); //等待解锁
        } catch (InterruptedException ie) {
          ie.printStackTrace();
        }
      }
      return responseList.removeFirst();
    }
  }

}