package com.soledede.util;

import gnu.trove.THashMap;
import gnu.trove.THashSet;
import gnu.trove.TIntObjectHashMap;
import gnu.trove.TLinkedList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 */
public class CollectionFactory {

  static boolean trove = true;

  static {
    if (System.getProperty("trove").equalsIgnoreCase("true"))
      trove = true;
  }

  public static Map getHashMap() {
    if (trove) {
      return new THashMap();
    } else {
      return new HashMap();
    }
  }

  public static TIntObjectHashMap getHashMap2() {
      return new TIntObjectHashMap();
  }



  public static Map getHashMap(int capacity) {
    if (trove) {
      return new THashMap(capacity);
    } else {
      return new HashMap(capacity);
    }
  }

  public static Set getHashSet() {
    if (trove) {
      return new THashSet();
    } else {
      return new HashSet();
    }
  }

  public static Set getHashSet(int capacity) {
    if (trove) {
      return new THashSet(capacity);
    } else {
      return new HashSet(capacity);
    }
  }

  public static List getLinkedList() {
    if (trove) {
      return new TLinkedList();
    } else {
      return new LinkedList();
    }
  }

}