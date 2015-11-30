package com.loginsight.bench.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.codecs.lucene50.Lucene50StoredFieldsFormat.Mode;
import org.apache.lucene.codecs.lucene53.Lucene53Codec;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.NoMergePolicy;
import org.apache.lucene.index.NoMergeScheduler;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class LuceneIndexingBench {
  
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
  
  static IndexWriter buildIndexWriter(Directory dir) throws Exception {
    IndexWriterConfig writerConfig = new IndexWriterConfig(new WhitespaceAnalyzer());
    writerConfig.setCodec(new Lucene53Codec(Mode.BEST_COMPRESSION));
    writerConfig.setMaxBufferedDocs(1000000);
    writerConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
    writerConfig.setRAMBufferSizeMB(2000);
    writerConfig.setUseCompoundFile(false);
    writerConfig.setMergePolicy(NoMergePolicy.INSTANCE);
    writerConfig.setMergeScheduler(NoMergeScheduler.INSTANCE);
    return new IndexWriter(dir, writerConfig);
  }
  
  static IndexWriter buildDiskWriter(Directory dir) throws Exception {
    IndexWriterConfig writerConfig = new IndexWriterConfig(new StandardAnalyzer());
    writerConfig.setUseCompoundFile(false);
    return new IndexWriter(dir, writerConfig);
  }
  
  static Document buildDocument(JsonObject json, String originalJson) {
    Document doc = new Document();
    if (originalJson != null) {
      doc.add(new Field("_stored", originalJson.getBytes(StandardCharsets.UTF_8), STORE_FIELD_TYPE));
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
  
  public static void main(String args[]) throws Exception{
    File dataFile = new File("/Users/john/bitbucket/logparser/logs/logs.json");    
    Path idxPath = FileSystems.getDefault().getPath("/tmp/john_test");
    
    FSDirectory fsDir = FSDirectory.open(idxPath);
    
    int numDocsPerSegment = 500000;
    boolean withStore = true;
    
    JsonParser parser = new JsonParser();
    BufferedReader reader = new BufferedReader(
        new InputStreamReader(new FileInputStream(dataFile), StandardCharsets.UTF_8));
    
    IndexWriter diskWriter = buildDiskWriter(fsDir);
    
    RAMDirectory ramDir = new RAMDirectory();
    IndexWriter ramWriter = buildIndexWriter(ramDir);
    
    int numIndexed = 0;
    long totalTime = 0L;
    while(true) {
      String line = reader.readLine();
      if (line == null) {
        // reached end of data file
        break;
      }
      JsonObject json = parser.parse(line).getAsJsonObject();
      long start = System.currentTimeMillis();
      Document doc = buildDocument(json, withStore ? line : null);
      ramWriter.addDocument(doc);
      numIndexed++;
      if (numIndexed % numDocsPerSegment == 0) {  // batched reached
        System.out.println("flushing batch, numDocs indexed so far: " + numIndexed + ", took: " + totalTime/1000+"s");
        System.out.println("current indexing rate per second: " + (double)numIndexed * 1000 / (double) totalTime);
        ramWriter.forceMerge(1);
        ramWriter.commit();
        ramWriter.close();
        diskWriter.addIndexes(ramDir);        
        diskWriter.commit();
        ramDir = new RAMDirectory();        
        ramWriter.close();
        ramWriter = buildIndexWriter(ramDir);
      }
      long end = System.currentTimeMillis();
      totalTime += (end-start);
    }
    reader.close();
    diskWriter.commit();
    diskWriter.close();
    System.out.println("flushing batch, numDocs indexed so far: " + numIndexed + ", took: " + totalTime/1000+"s");
    System.out.println("current indexing rate per second: " + (double)numIndexed * 1000 / (double) totalTime);
    System.out.println("indexing completed");
  }
}
