package com.sailing.db.Db;


import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;

public class DBCPUtils {

    private static Logger log = LoggerFactory.getLogger(DBCPUtils.class);

    private static BasicDataSource dataSource = new BasicDataSource();

    /*
     * DBCP连接池的配置只需要进行一次，故放在静态代码块中
     */
    static {

    }

    /*
     * 返回值为接口，提高了代码的扩展性
     */
    public static DataSource getDataSource(String dbDriverClassName, String dbUrl, String dbUsername, String dbPassword) {
        dataSource.setDriverClassName(dbDriverClassName);
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUsername);
        dataSource.setPassword(dbPassword);
        dataSource.setInitialSize(10);
        dataSource.setMaxIdle(1);
        dataSource.setMaxIdle(5);
        return dataSource;
    }

    public static void close(){
        try {
            if(dataSource!=null){
                dataSource.close();
                dataSource = null;
            }
        } catch (SQLException e) {
            log.error("关闭数据源出错，msg:{}",e.getMessage());
        }
    }
}
