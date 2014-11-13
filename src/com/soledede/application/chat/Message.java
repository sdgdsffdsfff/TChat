package com.soledede.application.chat;

public class Message implements java.io.Serializable {

  private String from = null;
  private String to = null;
  private String content = null;

  public String getFrom() {
    return this.from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  public String getTo() {
    return to;
  }

  public void setTo(String to) {
    this.to = to;
  }

  public String getContent() {
    return content;
  }

  public void setToContent(String content) {
    this.content = content;
  }

}