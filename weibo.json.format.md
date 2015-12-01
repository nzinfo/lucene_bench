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


