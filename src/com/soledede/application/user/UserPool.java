package com.soledede.application.user;

import gnu.trove.TIntObjectHashMap;

import com.soledede.connector.Connector;
/**
 * 保存用户相关状态
 * 连接状态
 *
 */
public class UserPool {

   private static UserPool userPool = new UserPool();
   public static UserPool getIntance(){
      return userPool;
   }

   private final static TIntObjectHashMap users = new TIntObjectHashMap();

   public synchronized void save(int userID, Connector connector) {
      users.put(userID, connector);
   }

   public synchronized Connector load(int userID){
      return (Connector)users.get(userID);
   }




}