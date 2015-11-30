package com.loginsight.bench.lucene;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonFileCleaner {
  public static void main(String[] args) throws Exception {
    File src = new File(args[0]);
    File target = new File(args[1]);
    
    BufferedReader reader = new BufferedReader(
        new InputStreamReader(new FileInputStream(src), StandardCharsets.UTF_8));
    
    BufferedWriter writer = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(target), StandardCharsets.UTF_8));
    
    JsonParser parser = new JsonParser();
    int lineCount = 0;
    int skipCount = 0;
    
    while(true) {
      String line = reader.readLine();
      if (line == null) {
        break;
      }
    
      try {
        JsonObject json = parser.parse(line).getAsJsonObject();
        // parsed ok, so write it out
        writer.write(line);
        writer.newLine();
        lineCount++;
      } catch (Exception e) {
        skipCount ++;
      }
    }
    
    writer.flush();
    writer.close();
    
    reader.close();
    
    System.out.println("line processed: " + lineCount);
    System.out.println("line skipped: " + skipCount);
  }
}
