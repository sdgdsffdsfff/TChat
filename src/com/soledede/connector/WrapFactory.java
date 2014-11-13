package com.soledede.connector;

import com.soledede.http.HttpWrapFactory;

/**
 * 数据包装工厂
 * 缺省是使用Http协议包装数据，也可以拓展成其它协议。
 */
public abstract class WrapFactory {

  private static WrapFactory factory =new  HttpWrapFactory();
  public static WrapFactory getInstance() {
    return factory;
  }

  public abstract byte[] getRequest(byte[] bytes);

  public abstract byte[] getResponse(byte[] bytes);

  public abstract byte[] getContentFromRequest(byte[] bytes) throws Exception;

  public abstract byte[] getContentFromResponse(byte[] bytes) throws Exception;

}