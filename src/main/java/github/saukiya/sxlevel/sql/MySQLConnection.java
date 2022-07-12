package github.saukiya.sxlevel.sql;

/**
 * @author Bkm016
 * @since 复制日期: 2018年5月8日
 */

import github.saukiya.sxlevel.SXLevel;
import github.saukiya.sxlevel.util.Config;
import org.bukkit.Bukkit;

import java.sql.*;

public class MySQLConnection {

    private String url;

    private String user;

    private String port;

    private String password;

    private String database;

    private String connectionUrl;

    private Connection connection;

    private boolean fallReconnection = true;

    public MySQLConnection() {
        this(Config.getConfig().getString(Config.SQL_HOST), Config.getConfig().getString(Config.SQL_USER),
                Config.getConfig().getString(Config.SQL_PORT), Config.getConfig().getString(Config.SQL_PASSWORD),
                Config.getConfig().getString(Config.SQL_DATABASE_NAME));
    }

    /**
     * 创建一个连接
     *
     * @param url      连接地址
     * @param user     用户名
     * @param port     端口
     * @param password 密码
     * @param database 数据库名
     * @return boolean
     * @author Bkm016
     */
    public MySQLConnection(String url, String user, String port, String password, String database) {
        // 检查驱动
        if (!loadDriverMySQL()) {
            print("驱动器获取失败, 无法连接到数据库");
            return;
        }

        // 设置数据
        this.url = url == null ? "localhost" : url;
        this.user = user == null ? "root" : user;
        this.port = port == null ? "3306" : port;
        this.password = password == null ? "" : password;
        this.database = database == null ? "test" : database;
        this.connectionUrl = "jdbc:mysql://" + this.url + ":" + this.port + "/" + this.database
                + "?characterEncoding=utf-8&useSSL=false";

        // 连接数据库
        connect();
    }

    public boolean isFallReconnection() {
        return fallReconnection;
    }

    public void setFallReconnection(boolean fallReconnection) {
        this.fallReconnection = fallReconnection;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPort() {
        return port;
    }

    public String getPassword() {
        return password;
    }

    public String getDatabase() {
        return database;
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean exitsTable(String name) {
        try {
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet set = meta.getTables(null, null, name, null);
            while (set.next()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void execute(String sql) {
        try {
            connection.createStatement().execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                return false;
            }
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public void closeConnection() {
        try {
            connection.close();
        } catch (Exception e) {
            //
        }
    }

    // 链接数据库
    public boolean connect() {
        try {
            print("正在连接数据库");
            print("地址: " + connectionUrl);
            long time = System.currentTimeMillis();
            connection = DriverManager.getConnection(connectionUrl, this.user, this.password);
            print("数据库连接成功 (" + (System.currentTimeMillis() - time) + "ms)");
            return true;
        } catch (SQLException e) {
            print("数据库连接失败");
            print("错误原因: " + e.getMessage());
            print("错误代码: " + e.getErrorCode());
            return false;
        }
    }

    public void print(String message) {
        Bukkit.getConsoleSender().sendMessage("[" + SXLevel.getPlugin().getName() + "] §c" + message);
    }

    private boolean loadDriverMySQL() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}
