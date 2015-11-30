package com.loginsight.bench.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.index.IndexOptions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

abstract public class DocBuilder {
  final static FieldType STORE_FIELD_TYPE = new FieldType();
  final static FieldType META_FIELD_TYPE = new FieldType();
  final static FieldType META_NUMERIC_FIELD_TYPE = new FieldType();
  
  static {
    STORE_FIELD_TYPE.setStored(true);
    STORE_FIELD_TYPE.setTokenized(false);
    STORE_FIELD_TYPE.setIndexOptions(IndexOptions.NONE);
    STORE_FIELD_TYPE.setDocValuesType(DocValuesType.NONE);
    STORE_FIELD_TYPE.setStoreTermVectors(false);
    STORE_FIELD_TYPE.freeze();
    
    META_FIELD_TYPE.setStored(false);
    META_FIELD_TYPE.setTokenized(false);
    META_FIELD_TYPE.setIndexOptions(IndexOptions.DOCS);
    META_FIELD_TYPE.setDocValuesType(DocValuesType.SORTED);
    META_FIELD_TYPE.setStoreTermVectors(false);
    META_FIELD_TYPE.freeze();
    
    META_NUMERIC_FIELD_TYPE.setStored(false);
    META_NUMERIC_FIELD_TYPE.setTokenized(false);
    META_NUMERIC_FIELD_TYPE.setIndexOptions(IndexOptions.DOCS);
    META_NUMERIC_FIELD_TYPE.setDocValuesType(DocValuesType.NUMERIC);
    META_NUMERIC_FIELD_TYPE.setStoreTermVectors(false);
    META_NUMERIC_FIELD_TYPE.freeze();
  }
  
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
  
  abstract public Document build(JsonObject json, byte[] originalJson);
}
