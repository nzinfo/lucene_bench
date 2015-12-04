package com.loginsight.bench.lucene;

import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.util.BytesRef;

public class NginxAccessLogDocBuilder extends DocBuilder {

  private static final String REGEX = 
      "^([\\d.]+) (\\S+) (\\S+) (\\S+) \\[([\\w:/]+\\s[+\\-]\\d{4})\\] \"(.+?)\" (\\d{3}) (\\d+) (\\d+) \"([^\"]+)\" \"([^\"]+)\"";
  
  private static final Pattern PATTERN = Pattern.compile(REGEX);
  
  private static int pack(byte[] bytes) {
    int val = 0;
    for (int i = 0; i < bytes.length; i++) {
      val <<= 8;
      val |= bytes[i] & 0xff;
    }
    return val;
  }

  @Override
  public Document build(String line, byte[] original) {
    Matcher matcher = PATTERN.matcher(line);
    if (!matcher.matches()) {
      return null;
    }
    
    try {
      String host = matcher.group(1);
      String date = matcher.group(5);
      String request = matcher.group(6);
      String respCode = matcher.group(7);
      String bytesSent = matcher.group(8);
      String browser = matcher.group(11);
      
      int hostInt = pack(InetAddress.getByName(host).getAddress());      
      Document doc = new Document();
      
      if (original != null) {
        doc.add(new Field("_stored", original, STORE_FIELD_TYPE));
      }
      
      doc.add(new NumericDocValuesField("host", hostInt));
      doc.add(new SortedDocValuesField("date", new BytesRef(date)));
      doc.add(new TextField("request", request == null ? "" : request, Store.NO));
      doc.add(new Field("response", respCode == null ? "0" : respCode, META_FIELD_TYPE));      
      doc.add(new LongField("bytesSent", bytesSent == null ? 0 : Long.parseLong(bytesSent), Field.Store.NO));
      doc.add(new TextField("browser", browser == null ? "" : browser, Store.NO));
      /*
      System.out.println("host: " + host);
      System.out.println("date: " + date);
      System.out.println("request: " + request);
      System.out.println("respCode: " + respCode);
      System.out.println("bytesSent: " + bytesSent);
      System.out.println("browser: " + browser);
      */
      return doc;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    
    
  }

}
