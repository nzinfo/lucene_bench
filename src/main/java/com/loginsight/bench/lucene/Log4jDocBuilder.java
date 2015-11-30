package com.loginsight.bench.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.util.BytesRef;

import com.google.gson.JsonObject;

public class Log4jDocBuilder extends DocBuilder {

  @Override
  public Document build(JsonObject json, byte[] originalJson) {
    Document doc = new Document();
    if (originalJson != null) {
      doc.add(new Field("_stored", originalJson, STORE_FIELD_TYPE));
    }
    doc.add(new TextField("text", json.get("message").getAsString(), Store.NO));    
    doc.add(new TextField("path", json.get("path").getAsString(), Store.NO));
    doc.add(new TextField("logger_name", json.get("logger_name").getAsString(), Store.NO));    
    doc.add(new TextField("thread", json.get("thread").getAsString(), Store.NO));
    doc.add(new TextField("class", json.get("class").getAsString(), Store.NO));
    doc.add(new TextField("file", json.get("file").getAsString(), Store.NO));
    doc.add(new TextField("method", json.get("method").getAsString(), Store.NO));
    
    String priority = json.get("priority").getAsString();            
    doc.add(new SortedDocValuesField("priority", new BytesRef(priority)));
    doc.add(new SortedDocValuesField("type", new BytesRef(json.get("type").getAsString())));
    doc.add(new SortedDocValuesField("timestamp", new BytesRef(json.get("@timestamp").getAsString())));
    return doc;
  }

}
