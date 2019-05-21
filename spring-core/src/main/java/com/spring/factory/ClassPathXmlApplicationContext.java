package com.spring.factory;

import com.spring.annotations.Component;
import com.spring.annotations.Resource;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工厂实现类
 */
public class ClassPathXmlApplicationContext implements BeanFactory {
    private String basePackage;//包范围

    private List<String> scanClassName=new ArrayList<String>();
    private Map<String,Object> map=new HashMap<String,Object>();
    public ClassPathXmlApplicationContext(String fileName) {
        // 1.读取xml 文件
        this.readXml(fileName);
        // 2.读取指定包范围下的class
        this.scanPackage(basePackage);
        // 3.根据指定标识实例化对象
        this.instanceBeans();
        // 4. 给属性设置值
        this.setBean();

    }


    private void setBean() {
        try {
            if(null !=scanClassName && scanClassName.size()>0){
                for(String clazz:scanClassName){
                    clazz=clazz.substring(0,clazz.lastIndexOf("."));
                    Field[] fields=Class.forName(clazz).getDeclaredFields();
                    if(null !=fields && fields.length>0){
                        for(Field field:fields){
                            Resource resource= field.getAnnotation(Resource.class);
                            if(null !=resource){
                                // 给属性设置值
                                field.setAccessible(true);
                                clazz=clazz.substring(clazz.lastIndexOf(".")+1);
                                String key=clazz.toLowerCase().charAt(0)+clazz.substring(1);
                                String value=resource.value();
                                field.set(map.get(key),map.get(value));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void instanceBeans() {
        try {
            if(null !=scanClassName && scanClassName.size()>0){
                for(String clazz:scanClassName){
                    clazz=clazz.substring(0,clazz.lastIndexOf("."));
                    String sourceClass=clazz;
                    Component component= Class.forName(clazz).getAnnotation(Component.class);
                    if(null !=component){
                        clazz=clazz.substring(clazz.lastIndexOf(".")+1);
                        String key=clazz.toLowerCase().charAt(0)+clazz.substring(1);
                        if(null !=component.value() && !("".equals(component.value().trim()))){
                            key=component.value();
                        }
                        map.put(key,Class.forName(sourceClass).newInstance());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取包范围下的class
     * @param basePackage
     */
    private void scanPackage(String basePackage) {
        /**
         * 绝对路径定位class 文件
         */
        // 传入路径
        URL url=Thread.currentThread()
                .getContextClassLoader()
                .getResource(basePackage.replaceAll("\\.","\\/"));
        String baseFilePath= url.getFile();
        //System.out.println("baseFilePath:"+baseFilePath);
        File baseFile=new File(baseFilePath);
        String[] files= baseFile.list();
        if(null !=files && files.length>0){
            for(String file:files){
                //System.out.println("文件:"+file);
                // 定位子文件
                File subFile=new File(baseFilePath+"/"+file);
                if(subFile.isDirectory()){
                    scanPackage(basePackage+"."+file);
                }else{
                    scanClassName.add(basePackage+"."+file);
                }
            }
        }
    }

    private void readXml(String fileName) {
        try {
            URL url=this.getClass().getClassLoader().getResource(fileName);
            if(null !=url){
                SAXReader reader=new SAXReader();
                Document document= reader.read(url);
                // xpath
                XPath xPath=document.createXPath("beans/component-scan");
                List<Element> elements= xPath.selectNodes(document);
                if(null !=elements && elements.size()>0){
                    Element element=elements.get(0);
                    basePackage=element.attributeValue("basepackage");
                }
            }else{
                System.out.println("资源文件不存在!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T> T getObject(String param) {
        return (T) map.get(param);
    }



}



