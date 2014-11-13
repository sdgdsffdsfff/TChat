package com.soledede.http;

import com.soledede.connector.WrapFactory;


/**
 * WrapFactory的具体实现  采用http协议
 */

public class HttpWrapFactory extends WrapFactory {

  private String httpPOSTHeader = null;

  public HttpWrapFactory() {
    httpPOSTHeader = HttpHeadHelper.getPOSTHeader("", 0);
  }

  public  byte[] getRequest(byte[] bytes){
     return HttpHeadHelper.assembleRequest(bytes);
  }

  public byte[] getResponse(byte[] bytes){
     return HttpHeadHelper.assembleResponse(bytes);
  }

  public byte[] getContentFromRequest(byte[] bytes) throws Exception{
     return HttpHeadHelper.getContent(bytes);
  }

  public byte[] getContentFromResponse(byte[] bytes) throws Exception{
    return HttpHeadHelper.getContent(bytes);
  }

}