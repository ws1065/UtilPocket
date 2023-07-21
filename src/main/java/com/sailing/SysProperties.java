package com.sailing;

import lombok.Data;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;


/**
 * 配置文件解析
 */
@Data
public class SysProperties {
    public static final Properties PROPERTISES = new Properties();



    /**
     * 加载静态配置
     */
    private void loadStaticConfig(){
        String fileName = "conf.properties";
        String path = System.getProperty("user.dir") + File.separator + fileName;
        File file = new File(path);
        if (file.exists()) {
            loadConfig(path);
        }else{
            InputStream in = SysProperties
                    .class
                    .getClassLoader()
                    .getResourceAsStream(fileName);
            loadConfig(in);
        }
    }

    /**
     * 无参数，加载默认配置（开发）
     */
    public SysProperties() {
        loadStaticConfig();
    }

    /**
     * 有参数，加载指定配置（生产）
     * 增加一个系统的配置问题用于配置除业务意外的问题
     */
    public SysProperties(String configPath) {
        loadConfig(configPath);
    }

    private void loadConfig(String configPath){
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(configPath));
            loadConfig(in);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(15);
        }
    }

    /**
     * 加载配置
     */
    private boolean loadConfig(InputStream in) {
        try {
            PROPERTISES.load(in);
            //需要再次加载系统配置文件
            loadSysConfig();
            // 配置文件转到java bean
            propertyToBean(this, PROPERTISES);
            // 设置其它属性
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(15);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //加载系统配置文件
    private void loadSysConfig() throws IOException {
        String fileName = "sys.properties";
        String path = System.getProperty("user.dir") + File.separator + fileName;
        if (new File(path).exists()) {
            PROPERTISES.load(new BufferedInputStream(new FileInputStream(path)));
        }else {
            URL url = SysProperties
                    .class
                    .getClassLoader().getResource(fileName);
            if (url != null && new File(url.getFile()).exists()) {
                PROPERTISES.load(new BufferedInputStream(new FileInputStream(url.getFile())));
            }
        }
    }

    /**
     * 属性文件向Bean赋值
     */
    private void propertyToBean(Object bean,Properties prop){
        if(prop == null) {
            return;
        }
        Iterator keyIt = prop.keySet().iterator();
        Class beanClass = bean.getClass();
        while(keyIt.hasNext()){
            try{
                String key = (String)keyIt.next();
                String value = prop.getProperty(key);
                Field field = beanClass.getDeclaredField(key);
                Class fieldClass = field.getType();
                Method method = beanClass.getDeclaredMethod("set"+initialUpperCase(key), fieldClass);
                String simpleName = fieldClass.getSimpleName();
                if("boolean".equals(simpleName)){
                    method.invoke(bean, Boolean.parseBoolean((String)value));
                }else if("String".equals(simpleName)){
                    method.invoke(bean, value);
                }else if("Integer".equals(simpleName)){
                    method.invoke(bean, Integer.parseInt(value));
                }else if("Long".equals(simpleName)){
                    method.invoke(bean, Long.parseLong(value));
                }else{
                    BigDecimal bd = new BigDecimal(value);
                    Method convertMethod = bd.getClass().getDeclaredMethod(simpleName+"Value");
                    method.invoke(bean, convertMethod.invoke(bd));
                }
            }catch(NoSuchFieldException ex){
                ex.printStackTrace();
                continue;
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }

    /**
     * 首字母大写
     */
    private String initialUpperCase(String oldString){
        if(oldString == null || "".equals(oldString)) {
            return oldString;
        }
        return new StringBuffer().append(oldString.substring(0,1).toUpperCase()).append(oldString.substring(1)).toString();
    }

    //----------SimpleServer-------------------
    private String selfIpAndPortToWan;
    private String selfIpAndPortToLan;
    //----------系统参数-end-------------------



}
