package com.loginsight.bench.lucene;

import org.apache.lucene.document.Document;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

abstract public class JsonDocBuilder extends DocBuilder {
  private final JsonParser parser = new JsonParser();
  
  static String getWithDefault(JsonObject json, String name, String defaultVal) {
    JsonElement elem = json.get(name);
    if (elem == null) {
      return defaultVal;
    }
    return elem.getAsString();
  }
  
  static long getWithDefaultLong(JsonObject json, String name, long defaultVal) {
    JsonElement elem = json.get(name);
    if (elem == null) {
      return defaultVal;
    }
    return elem.getAsLong();
  }
  
  public Document build(String line, byte[] original) {
    JsonObject json = parser.parse(line).getAsJsonObject();
    return build(json, original);
  }
  
  abstract public Document build(JsonObject json, byte[] originalJson);
}
