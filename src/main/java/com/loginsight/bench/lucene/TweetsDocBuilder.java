package com.loginsight.bench.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.util.BytesRef;
//import system;

import com.google.gson.JsonObject;

public class TweetsDocBuilder extends JsonDocBuilder {

  @Override
  public Document build(JsonObject json, byte[] originalJson) {
    Document doc = new Document();
    
    /*
    if (json.get("delete") != null) {
      // delete tweet, skip
      return null;
    }
    */
    
    if (originalJson != null) {
      doc.add(new Field("_stored", originalJson, STORE_FIELD_TYPE));
    }else{
      System.out.println("originalJson is null");
      System.exit(-1); 
    }
    
    
    doc.add(new TextField("text", getWithDefault(json, "text", ""), Store.NO));    
    doc.add(new TextField("name", getWithDefault(json, "name", ""), Store.NO));
    doc.add(new TextField("screen_name", getWithDefault(json, "screen_name", ""), Store.NO));
    doc.add(new TextField("retweeted", getWithDefault(json, "retweeted", ""), Store.NO));
    
    long followerCount = getWithDefaultLong(json, "followers_count", 0L);
    long friends_count = getWithDefaultLong(json, "friends_count", 0L);
    long favourites_count = getWithDefaultLong(json, "favourites_count", 0L);
    
    doc.add(new LongField("followerCount", followerCount, Field.Store.NO));
    doc.add(new LongField("friends_count", friends_count, Field.Store.NO));
    doc.add(new LongField("favourites_count", favourites_count, Field.Store.NO));
    
    doc.add(new SortedDocValuesField("retweeted", new BytesRef(getWithDefault(json, "retweeted", ""))));
    doc.add(new SortedDocValuesField("created_at", new BytesRef(getWithDefault(json, "created_at", ""))));
    doc.add(new NumericDocValuesField("followers_count", followerCount));
    doc.add(new NumericDocValuesField("friends_count", friends_count));
    doc.add(new NumericDocValuesField("favourites_count", favourites_count));
    return doc;
  }

}
