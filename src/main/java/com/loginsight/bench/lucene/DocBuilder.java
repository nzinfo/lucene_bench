package com.loginsight.bench.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.index.IndexOptions;

public abstract class DocBuilder {
  final static FieldType STORE_FIELD_TYPE = new FieldType();
  final static FieldType META_FIELD_TYPE = new FieldType();
  
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
    META_FIELD_TYPE.setStoreTermVectors(false);
    META_FIELD_TYPE.freeze();    
  }
  
  public abstract Document build(String doc, byte[] original);
}
