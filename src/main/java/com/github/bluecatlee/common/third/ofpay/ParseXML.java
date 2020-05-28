package com.github.bluecatlee.common.third.ofpay;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class ParseXML {
	
//	private static Map<String,String> map = new HashMap<>();
	
 	/*public static Map<String, String> parseToMap(String xmlText) {
 		//替换掉可能存在的特殊字符
 		String str = xmlText.replace("&", "&amp;");
 		Document document = null;
        try {
            document = DocumentHelper.parseText(str);
        } catch (DocumentException e) {
            e.printStackTrace();
        }  
        
        //获取根节点
        Element rootElement = document.getRootElement();
               
        Map<String, String> map = listNodes(rootElement);
        
        return map;
 	}
    
    public static Map<String,String> listNodes(Element node){  
    	 Map<String,String> map=new HashMap<String,String>();  
        //如果当前节点内容不为空，则存到map中 
        if(!(node.getTextTrim().equals(""))){  
             //System.out.println( node.getName() + "：" + node.getText());    
             map.put(node.getName(), node.getText());
        }  
        //同时迭代当前节点下面的所有子节点  
        Iterator<Element> iterator = node.elementIterator();
        while(iterator.hasNext()){  
            Element e = iterator.next();  
            //使用递归 
            listNodes(e);  
        }
        return map;
    } */
    
    /**
     * 该方法废弃 有XXE漏洞
     * @param strXml
     * @return
     * @throws Exception
     */
    public static Map<String,String> strToXmlAndPaserXmlOld(String strXml) throws Exception{  
        SAXReader reader = new SAXReader();  
        Document doc = reader.read(new ByteArrayInputStream(strXml.getBytes("UTF-8")));  
        Map<String,String> xml=paserXml(doc);  
        return xml;  
    }  
    
    /**
     * 该方法废弃 有XXE漏洞
     * @param strXml
     * @return
     * @throws Exception
     */
    public static Map<String,String> strToXmlAndPaserXmlGB2312Old(String strXml) throws Exception{  
    	SAXReader reader = new SAXReader();  
    	Document doc = reader.read(new ByteArrayInputStream(strXml.getBytes("gb2312")));  
    	Map<String,String> xml=paserXml(doc);  
    	return xml;  
    }  
    
    public static Map<String,String> strToXmlAndPaserXml(String strXml) throws Exception{  
    	SAXReader reader = new SAXReader();  
    	reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl",true);     //完全禁用DTDs 防止XXE漏洞
    	Document doc = reader.read(new ByteArrayInputStream(strXml.getBytes("UTF-8")));  
    	Map<String,String> xml=paserXml(doc);  
    	return xml;  
    }  
    
    public static Map<String,String> strToXmlAndPaserXmlGB2312(String strXml) throws Exception{  
    	SAXReader reader = new SAXReader();  
    	reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl",true);     //完全禁用DTDs 防止XXE漏洞
    	Document doc = reader.read(new ByteArrayInputStream(strXml.getBytes("gb2312")));  
    	Map<String,String> xml=paserXml(doc);  
    	return xml;  
    }  
      
    //遍历解析xml数据  
    public static Map<String,String> paserXml(Document doc) throws Exception{  
        Map<String,String> xml=new HashMap<String,String>();  
        Element root = doc.getRootElement();  
        Iterator it = root.elementIterator();  
        Element element;  
        while (it.hasNext()) {  
            element = (Element) it.next();  
            xml.put(element.getName(),element.getText());  
        }  
        return xml;  
    }  
    
    /**
     * 测试xxe攻击
     * @param args
     */
    public static void main(String[] args) {
		String str = "<?xml version='1.0' encoding='utf-8'?>\r\n" + 
				"\r\n" + 
				"<!DOCTYPE xdsec [\r\n" + 
				"\r\n" + 
				"<!ELEMENT methodname ANY>\r\n" + 
				"\r\n" + 
				"<!ENTITY xxe SYSTEM 'file:///c:/windows/system.ini'>]>\r\n" + 
				"\r\n" + 
				"<methodcall>\r\n" + 
				"\r\n" + 
				"<methodname>&xxe;</methodname>\r\n" + 
				"\r\n" + 
				"</methodcall>";
		
		try {
			Map<String, String> map = strToXmlAndPaserXml(str);
			System.out.println(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
}
