package com.loginsight.bench.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.TextField;

import com.google.gson.JsonObject;
//import system;

public class WeiboDocBuilder extends JsonDocBuilder {

  @Override
  public Document build(JsonObject json, byte[] originalJson) {
    Document doc = new Document();
    
    /*
    if (json.get("delete") != null) {
      // delete tweet, skip
      return null;
      以下是对照map：
    keyMap.put("uid", "a");
    keyMap.put("mblogid", "b");
    keyMap.put("content", "c");
    keyMap.put("pic", "d");
    keyMap.put("time", "e");
    keyMap.put("mblogtime", "e");
    keyMap.put("client_src", "f");
    keyMap.put("commentnum", "g");
    keyMap.put("rtnum", "h");
    keyMap.put("rtrootuid", "i");
    keyMap.put("rtmblogid", "j");
    keyMap.put("rtreason", "k");
    keyMap.put("rtrootnick", "l");
    keyMap.put("rtrootvip", "m");
    
 
微博数据字段格式说明：
"uid":微博用户
"content":内容
"mblogid":Sina微博id
"pic":微博带的图片
"commentnum":评论数,
"rtnum":转发数量,
"time":微博发布时间
"client_src":客户端名称
"create_date":抓取时间
如果存在以下字段，则此微博是转发：
"rtmblogid":被转发微博的id
"rtreason":微博uid转发时说的话，注意：如果是转发此条微博content就是被转发微博的原文
"rtrootnick":被转发微博的用户名称
"rtrootuid":被转发微博的用户uid
"rtrootvip":被转发微博的用户是否是认证用户为1
{"a":"2105499184","b":"xlUGXwIYo","c":"研究生。好好念。 ","d":"","e":1314716937,"f":"网页版","g":0,"h":0}
{"a":"2105499184","b":"xlJDJ7Zew","c":"拼不下去了…我想。你觉得呢。 ","d":"http://wp1.sina.cn/wap240/7d7f5e30jw1dkmmekkm1pj.jpg","e":1314615646,"f":"S60客户端","g":0,"h":0}
{"a":"2105499184","b":"xlGvdeqiu","c":"【12星座EQ排行榜】冠军（双子座）、亚军（水瓶座）、季军（天秤座）、第四名（射手座）、第五名（双鱼座）、第六名（巨蟹座）、
第七名（狮子座）、第八名（天蝎座）、第九名（处女座）、第十名（摩羯座）、第十一名（金牛座）、第十二名（白羊座） ",
"d":"http://wp3.sina.cn/wap240/67736df9jw1dkm6lpww01j.jpg","e":1314586893,"f":"S60客户端","g":1,"h":0,"i":"1735618041",
"j":"xlG3P98NW","k":"我怎么能是最后。。。 ","l":"星座秘语","m":"0"}
    }
    */
    
    if (originalJson != null) {
      doc.add(new Field("_stored", originalJson, STORE_FIELD_TYPE));
    }else{
      System.out.println("originalJson is null");
      System.exit(-1); 
    }
    
    
    doc.add(new TextField("content", getWithDefault(json, "c", ""), Store.NO));    
    //doc.add(new TextField("name", getWithDefault(json, "name", ""), Store.NO));
    //doc.add(new TextField("screen_name", getWithDefault(json, "screen_name", ""), Store.NO));
    doc.add(new TextField("client_src", getWithDefault(json, "f", ""), Store.NO));
    
    long uid = getWithDefaultLong(json, "a", 0L);
    long commentnum = getWithDefaultLong(json, "g", 0L);
    long rtnum = getWithDefaultLong(json, "h", 0L);
    
    doc.add(new LongField("uid", uid, Field.Store.YES));
    doc.add(new LongField("commentnum", commentnum, Field.Store.YES));
    doc.add(new LongField("rtnum", rtnum, Field.Store.YES));
    
    //doc.add(new SortedDocValuesField("retweeted", new BytesRef(getWithDefault(json, "retweeted", ""))));
    //doc.add(new SortedDocValuesField("created_at", new BytesRef(getWithDefault(json, "created_at", ""))));
    //doc.add(new NumericDocValuesField("followers_count", followerCount));
    //doc.add(new NumericDocValuesField("friends_count", friends_count));
    //doc.add(new NumericDocValuesField("favourites_count", favourites_count));
    return doc;
  }

}
