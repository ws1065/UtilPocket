package com.sailing.db.Db;

import com.sailing.dscg.common.secret.Base64Utils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Date;
import java.sql.SQLException;

public class HandleDb {
    /** 表名 */
    private final static String table_name = "server_strategy";

    private QueryRunner runner;

    private static Logger log = LoggerFactory.getLogger(HandleDb.class);
    public HandleDb(String dbDriverClassName,String dbUrl,String dbUsername,String dbPassword){
        // 无参时需要自己管理关闭connection
        runner = new QueryRunner(DBCPUtils.getDataSource(dbDriverClassName,dbUrl,dbUsername,dbPassword));
    }

    public void insert(String file) throws SQLException {
        //查询表是否存在
        String sql = "INSERT INTO `test` (`data`, `createtime`) \n" +
                "VALUES  (?,?) ";
        Object[] params = new Object[]{file,new Date(System.currentTimeMillis())};
        runner.insert(sql,new ScalarHandler<>(),params);
    }

    public static void start(String[] args) throws Exception{
        String dstHost = args[0];
        String dstPost = args[1];
        String dbName = args[2];
        String dbuserName = args[3];
        String dbpassword = args[4];
        String fileLocation = args[5];
        String times = args[6];

        String dbDriverClassName = "org.mariadb.jdbc.Driver";
        String dbUrl = "jdbc:mariadb://"+dstHost+":"+dstPost+"/"+dbName+"?generateSimpleParameterMetadata=true";
        String dbUsername = dbuserName;
        String dbPassword = dbpassword;

        HandleDb db = new HandleDb(dbDriverClassName,dbUrl,dbUsername,dbPassword);
        byte[] bytes = readLocalFile(fileLocation);
        for (int i = 0; i < Integer.parseInt(times); i++) {
            String encode = Base64Utils.encode(bytes);
            //set global max_allowed_packet = 2*1024*1024*10
            db.insert(encode);
            log.debug("大概第"+i+"个数据入库，数据长度"+encode.length()/1024D/1024D+"MB");
        }
    }
    public static byte[] readLocalFile(String filepath) throws IOException {
        InputStream inputStream = null;
        BufferedInputStream br = null;
        try {
            File file = new File(filepath);
            inputStream = new FileInputStream(file);
            br = new BufferedInputStream(inputStream);
            byte[] bytes = new byte[4096];
            byte[] result = new byte[(int)file.length()];
            int read;
            int location = 0;
            while (-1!= (read= br.read(bytes, 0, 4096))){
                System.arraycopy(bytes,0,result,location,read);
                location = location+read;
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                br.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return null;
    }

}
