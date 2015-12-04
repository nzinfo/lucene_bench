package com.loginsight.bench.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Properties;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.codecs.lucene50.Lucene50StoredFieldsFormat.Mode;
import org.apache.lucene.codecs.lucene53.Lucene53Codec;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.NoMergePolicy;
import org.apache.lucene.index.NoMergeScheduler;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

public class LuceneIndexingBench {
  
  static IndexWriter buildIndexWriter(Directory dir) throws Exception {
    IndexWriterConfig writerConfig = new IndexWriterConfig(new StandardAnalyzer());
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
  
  public static void main(String args[]) throws Exception{
    
    FileReader confFileReader = new FileReader(new File(args[0]));
    Properties config = new Properties();
    config.load(confFileReader);
    confFileReader.close();
    
    File dataFile = new File(config.getProperty("data.dir"));
    System.out.println("data file: " + dataFile.getAbsolutePath());
    Path idxPath = FileSystems.getDefault().getPath(config.getProperty("index.dir"));
    System.out.println("target index dir: " + idxPath.toString());
    
    FSDirectory fsDir = FSDirectory.open(idxPath);
    
    int numDocsPerSegment = Integer.parseInt(config.getProperty("docs.per.segment"));
    System.out.println("num docs per segment: " + numDocsPerSegment);
    
    boolean withStore = Boolean.parseBoolean(config.getProperty("rawstore"));
    System.out.println("store raw data: " + withStore);
    
    DocBuilder docBuilder = (DocBuilder) Class.forName(config.getProperty("docbuilder.class")).newInstance();
    
    System.out.println("doc builder: " + docBuilder.getClass());
    
    BufferedReader reader = new BufferedReader(
        new InputStreamReader(new FileInputStream(dataFile), StandardCharsets.UTF_8));
    
    IndexWriter diskWriter = buildDiskWriter(fsDir);
    
    RAMDirectory ramDir = new RAMDirectory();
    IndexWriter ramWriter = buildIndexWriter(ramDir);
    
    int numIndexed = 0;
    long totalTime = 0L;
    long tatalBytes = 0L;
    int numSkipped = 0;
    while(true) {
      String line = reader.readLine();
      if (line == null) {
        // reached end of data file
        break;
      }
            
      byte[] storedBytes = line.getBytes(StandardCharsets.UTF_8);
      
      Document doc = null;
      
      try {
        doc = docBuilder.build(line, withStore ? storedBytes : null);
      } catch (Exception e) {
        // possible corrupt data, log it and ignore;
       // System.out.println("skip corrupt element: " + line +", numskipped: " + skippedCount++ + ", current processed: " + numIndexed);
      }
      if (doc == null) {
        numSkipped ++;
        continue;
      }
      long start = System.currentTimeMillis();
      ramWriter.addDocument(doc);
      numIndexed++;
      tatalBytes += storedBytes.length;
      if (numIndexed % numDocsPerSegment == 0) {  // batched reached
        System.out.println("flushing batch, numDocs indexed so far: " + numIndexed + ", took: " + totalTime/1000+"s");
        System.out.println("current indexing rate per second: " 
            + (double)numIndexed * 1000 / (double) totalTime + "docs, "
            + (double)tatalBytes / (double) totalTime / 1000.0 + "mb");
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
    System.out.println("numDocs indexed so far: " + numIndexed + ", took: " + totalTime/1000+"s");
    System.out.println("indexing rate per second: " 
        + (double)numIndexed * 1000 / (double) totalTime + "docs, "
        + (double)tatalBytes / (double) totalTime / 1000.0 + "mb");
    System.out.println("numDocs skipped: " + numSkipped);
    System.out.println("indexing completed");
  }
}
