package github.saukiya.sxlevel.sql;

/**
 * @author Bkm016
 * @since 复制日期: 2018年5月8日
 */

import github.saukiya.sxlevel.SXLevel;
import github.saukiya.sxlevel.util.Config;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.*;

public class MySQLConnection {

    @Getter
    private String url;
    @Getter
    private String user;
    @Getter
    private String port;
    @Getter
    private String password;
    @Getter
    private String database;
    @Getter
    private String connectionUrl;
    @Getter
    private Connection connection;
    @Getter
    @Setter
    private boolean fallReconnection = true;


    public MySQLConnection() {
        this(Config.getConfig().getString(Config.SQL_HOST), Config.getConfig().getString(Config.SQL_USER), Config.getConfig().getString(Config.SQL_PORT), Config.getConfig().getString(Config.SQL_PASSWORD), Config.getConfig().getString(Config.SQL_DATABASE_NAME));
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
        this.connectionUrl = "jdbc:mysql://" + this.url + ":" + this.port + "/" + this.database + "?characterEncoding=utf-8&useSSL=false";

        // 连接数据库
        connect();
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

    @SuppressWarnings("deprecation")
    public void closeConnection() {
        try {
            connection.close();
        } catch (Exception e) {
            //
        }
    }

    public boolean deleteTable(String name) {
        return execute("drop table if exists " + name);
    }

    /**
     * 2018年1月17日 新增, TabooLib 版本 3.25
     */
    public boolean truncateTable(String name) {
        return execute("truncate table " + name);
    }

    public boolean clearTable(String name) {
        return execute("delete from " + name);
    }

    public boolean renameTable(String name, String newName) {
        return execute("rename table `" + name + "` to `" + newName + "`");
    }

    public boolean deleteColumn(String name, String column) {
        return execute("alter table `" + name + "` drop `" + column + "`");
    }

    public void addColumn(String name, Column... columns) {
        for (Column column : columns) {
            execute("alter table " + name + " add " + column.toString());
        }
    }

    public boolean addColumn(String name, String column) {
        if (!column.contains("/")) {
            return execute("alter table " + name + " add `" + column + "` text");
        }
        return execute("alter table " + name + " add `" + column.split("/")[0] + "` " + column.split("/")[1]);
    }

    public boolean editColumn(String name, String oldColumn, Column newColumn) {
        return execute("alter table " + name + " change `" + oldColumn + "` " + newColumn.toString());
    }

    public boolean editColumn(String name, String oldColumn, String newColumn) {
        if (!newColumn.contains("/")) {
            return execute("alter table " + name + " change `" + oldColumn + "` `" + newColumn + "` text");
        }
        return execute("alter table " + name + " change `" + oldColumn + "` `" + newColumn.split("/")[0] + "` " + newColumn.split("/")[1]);
    }

    /**
     * 删除数据
     *
     * @param name        名称
     * @param column      参考列
     * @param columnValue 参考值
     * @return boolean
     */
    public boolean deleteValue(String name, String column, Object columnValue) {
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            pstmt = connection.prepareStatement("delete from `" + name + "` where `" + column + "` = ?");
            pstmt.setObject(1, columnValue);
            pstmt.executeUpdate();
        } catch (Exception e) {
            print("数据库命令执行出错");
            print("错误类型: deleteValue(String,String,Object);");
            print("错误原因: " + e.getMessage());
            // 重新连接
            if (fallReconnection && e.getMessage().contains("closed")) {
                connect();
            }
        } finally {
            freeResult(resultSet, pstmt);
        }
        return false;
    }

    /**
     * 写入数据
     *
     * @param name        名称
     * @param column      参考列
     * @param columnValue 参考值
     * @param valueColumn 数据列
     * @param value       数据值
     * @return boolean
     */
    public boolean setValue(String name, String column, Object columnValue, String valueColumn, Object value) {
        return setValue(name, column, columnValue, valueColumn, value, false);
    }

    /**
     * 写入数据
     *
     * @param name        名称
     * @param column      参考列
     * @param columnValue 参考值
     * @param valueColumn 数据列
     * @param value       数据值
     * @param append      是否追加（数据列类型必须为数字）
     * @return boolean
     */
    public boolean setValue(String name, String column, Object columnValue, String valueColumn, Object value, boolean append) {
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            if (append) {
                pstmt = connection.prepareStatement("update `" + name + "` set `" + valueColumn + "` = `" + valueColumn + "` + ? where `" + column + "` = ?");
            } else {
                pstmt = connection.prepareStatement("update `" + name + "` set `" + valueColumn + "` = ? where `" + column + "` = ?");
            }
            pstmt.setObject(1, value);
            pstmt.setObject(2, columnValue);
            pstmt.executeUpdate();
        } catch (Exception e) {
            print("数据库命令执行出错");
            print("错误类型: setValue(String,String,Object,String,Object,boolean);");
            print("错误原因: " + e.getMessage());
            // 重新连接
            if (fallReconnection && e.getMessage().contains("closed")) {
                connect();
            }
        } finally {
            freeResult(resultSet, pstmt);
        }
        return false;
    }

    /**
     * 插入数据
     *
     * @param name   名称
     * @param values 值
     * @return boolean
     */
    public boolean intoValue(String name, Object... values) {
        StringBuilder sb = new StringBuilder();
        for (Object value : values) {
            sb.append("?, ");
        }
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            pstmt = connection.prepareStatement("insert into `" + name + "` values(null, " + sb.substring(0, sb.length() - 2) + ")");
            for (int i = 0; i < values.length; i++) {
                pstmt.setObject(i + 1, values[i]);
            }
            pstmt.executeUpdate();
        } catch (Exception e) {
            print("数据库命令执行出错");
            print("错误类型: intoValue(String,Object...);");
            print("错误原因: " + e.getMessage());
            // 重新连接
            if (fallReconnection && e.getMessage().contains("closed")) {
                connect();
            }
        } finally {
            freeResult(resultSet, pstmt);
        }
        return false;
    }

    /**
     * 创建数据表
     *
     * @param name    名称
     * @param columns 列表
     * @return boolean
     */
    public boolean createTable(String name, Column... columns) {
        StringBuilder sb = new StringBuilder();
        for (Column column : columns) {
            sb.append(column.toString()).append(", ");
        }
        return execute("create table if not exists " + name + " (id int(1) not null primary key auto_increment, " + sb.substring(0, sb.length() - 2) + ")");
    }

    /**
     * 创建数据表
     *
     * @param name    名称
     * @param columns 列表
     * @return boolean
     */
    public boolean createTable(String name, String... columns) {
        StringBuilder sb = new StringBuilder();
        for (String column : columns) {
            if (!column.contains("/")) {
                sb.append("`").append(column).append("` text, ");
            } else {
                sb.append("`").append(column.split("/")[0]).append("` ").append(column.split("/")[1]).append(", ");
            }
        }
        return execute("create table if not exists " + name + " (id int(1) not null primary key auto_increment, " + sb.substring(0, sb.length() - 2) + ")");
    }

    /**
     * 检查数据表是否存在
     *
     * @param name 名称
     * @return boolean
     */
    public boolean isExists(String name) {
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            pstmt = connection.prepareStatement("select table_name FROM information_schema.TABLES where table_name = ?");
            pstmt.setString(1, name);
            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                return true;
            }
        } catch (Exception e) {
            print("数据库命令执行出错");
            print("错误类型: isExists(String);");
            print("错误原因: " + e.getMessage());
            // 重新连接
            if (fallReconnection && e.getMessage().contains("closed")) {
                connect();
            }
        } finally {
            freeResult(resultSet, pstmt);
        }
        return false;
    }

    /**
     * 检查数据是否存在
     *
     * @param name        名称
     * @param column      列表名
     * @param columnValue 列表值
     * @return boolean
     */
    public boolean isExists(String name, String column, Object columnValue) {
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            pstmt = connection.prepareStatement("select * from `" + name + "` where `" + column + "` = ?");
            pstmt.setObject(1, columnValue);
            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                return true;
            }
        } catch (Exception e) {
            print("数据库命令执行出错");
            print("错误类型: isExists(String,String,Object);");
            print("错误原因: " + e.getMessage());
            // 重新连接
            if (fallReconnection && e.getMessage().contains("closed")) {
                connect();
            }
        } finally {
            freeResult(resultSet, pstmt);
        }
        return false;
    }

    /**
     * 获取所有列表名称（不含主键）
     *
     * @param name 名称
     * @return {@link List}
     */
    public List<String> getColumns(String name) {
        return getColumns(name, false);
    }

    /**
     * 获取所有列表名称
     *
     * @param name    名称
     * @param primary 是否获取主键
     * @return {@link List}
     */
    public List<String> getColumns(String name, boolean primary) {
        List<String> list = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            pstmt = connection.prepareStatement("select column_name from information_schema.COLUMNS where table_name = ?");
            pstmt.setString(1, name);
            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                list.add(resultSet.getString(1));
            }
        } catch (Exception e) {
            print("数据库命令执行出错");
            print("错误类型: getColumns(String,boolean);");
            print("错误原因: " + e.getMessage());
            // 重新连接
            if (fallReconnection && e.getMessage().contains("closed")) {
                connect();
            }
        } finally {
            freeResult(resultSet, pstmt);
        }
        // 是否获取主键
        if (!primary) {
            list.remove("id");
        }
        return list;
    }

    /**
     * 获取单项数据
     *
     * @param name        名称
     * @param column      参考列
     * @param columnValue 参考值
     * @param valueColumn 数据列
     * @return Object
     */
    public Object getValue(String name, String column, Object columnValue, String valueColumn) {
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            pstmt = connection.prepareStatement("select * from `" + name + "` where `" + column + "` = ? limit 1");
            pstmt.setObject(1, columnValue);
            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                return resultSet.getObject(valueColumn);
            }
        } catch (Exception e) {
            print("数据库命令执行出错");
            print("错误类型: getValue(String,String,Object,String);");
            print("错误原因: " + e.getMessage());
            // 重新连接
            if (fallReconnection && e.getMessage().contains("closed")) {
                connect();
            }
        } finally {
            freeResult(resultSet, pstmt);
        }
        return null;
    }

    /**
     * 获取单项数据（根据主键倒叙排列后的最后一项）
     *
     * @param name        名称
     * @param column      参考列
     * @param columnValue 参考值
     * @param valueColumn 数据列
     * @return Object
     */
    public Object getValueLast(String name, String column, Object columnValue, String valueColumn) {
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            pstmt = connection.prepareStatement("select * from `" + name + "` where `" + column + "` = ? order by id desc limit 1");
            pstmt.setObject(1, columnValue);
            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                return resultSet.getObject(valueColumn);
            }
        } catch (Exception e) {
            print("数据库命令执行出错");
            print("错误类型: getValueLast(String,String,Object,String);");
            print("错误原因: " + e.getMessage());
            // 重新连接
            if (fallReconnection && e.getMessage().contains("closed")) {
                connect();
            }
        } finally {
            freeResult(resultSet, pstmt);
        }
        return null;
    }

    /**
     * 获取多项数据（根据主键倒叙排列后的最后一项）
     *
     * @param name        名称
     * @param column      参考列
     * @param columnValue 参考值
     * @param valueColumn 数据列
     * @return {@link HashMap}
     */
    public Map<String, Object> getValueLast(String name, String column, Object columnValue, String... valueColumn) {
        Map<String, Object> map = new HashMap<>();
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            pstmt = connection.prepareStatement("select * from `" + name + "` where `" + column + "` = ? order by id desc limit 1");
            pstmt.setObject(1, columnValue);
            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                for (String _column : valueColumn) {
                    map.put(_column, resultSet.getObject(_column));
                }
                break;
            }
        } catch (Exception e) {
            print("数据库命令执行出错");
            print("错误类型: getValueLast(String,String,Object,String...);");
            print("错误原因: " + e.getMessage());
            // 重新连接
            if (fallReconnection && e.getMessage().contains("closed")) {
                connect();
            }
        } finally {
            freeResult(resultSet, pstmt);
        }
        return map;
    }

    /**
     * 获取多项数据（单项多列）
     *
     * @param name        名称
     * @param column      参考列
     * @param columnValue 参考值
     * @param valueColumn 数据列
     * @return {@link HashMap}
     */
    public Map<String, Object> getValue(String name, String column, Object columnValue, String... valueColumn) {
        Map<String, Object> map = new HashMap<>();
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            pstmt = connection.prepareStatement("select * from `" + name + "` where `" + column + "` = ? limit 1");
            pstmt.setObject(1, columnValue);
            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                for (String _column : valueColumn) {
                    map.put(_column, resultSet.getObject(_column));
                }
                break;
            }
        } catch (Exception e) {
            print("数据库命令执行出错");
            print("错误类型: getValue(String,String,Object,String...);");
            print("错误原因: " + e.getMessage());
            // 重新连接
            if (fallReconnection && e.getMessage().contains("closed")) {
                connect();
            }
        } finally {
            freeResult(resultSet, pstmt);
        }
        return map;
    }

    /**
     * 获取多项数据（单列多列）
     *
     * @param name   名称
     * @param column 参考列
     * @param size   获取数量（-1 为无限制）
     * @return {@link List}
     */
    public List<Object> getValues(String name, String column, int size) {
        return getValues(name, column, size, false);
    }

    /**
     * 获取多项数据（单列多列）
     *
     * @param name   名称
     * @param column 参考列
     * @param size   获取数量（-1 位无限制）
     * @param desc   是否倒序
     * @return {@link List}
     */
    public List<Object> getValues(String name, String column, int size, boolean desc) {
        List<Object> list = new LinkedList<>();
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            if (desc) {
                pstmt = connection.prepareStatement("select * from `" + name + "` order by `" + column + "` desc " + (size < 0 ? "" : " limit " + size));
            } else {
                pstmt = connection.prepareStatement("select * from `" + name + "` order by `" + column + "` " + (size < 0 ? "" : " limit " + size));
            }
            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                list.add(resultSet.getObject(column));
            }
        } catch (Exception e) {
            print("数据库命令执行出错");
            print("错误类型: getValues(String,String,int,boolean);");
            print("错误原因: " + e.getMessage());
            // 重新连接
            if (fallReconnection && e.getMessage().contains("closed")) {
                connect();
            }
        } finally {
            freeResult(resultSet, pstmt);
        }
        return list;
    }

    /**
     * 获取多线数据（多项多列）
     *
     * @param name        名称
     * @param sortColumn  参考列（该列类型必须为数字）
     * @param size        获取数量（-1 为无限制）
     * @param valueColumn 获取数据列
     * @return {@link LinkedList}
     */
    public LinkedList<Map<String, Object>> getValues(String name, String sortColumn, int size, String... valueColumn) {
        return getValues(name, sortColumn, size, false, valueColumn);
    }

    /**
     * 获取多项数据（多项多列）
     *
     * @param name        名称
     * @param sortColumn  参考列（该列类型必须为数字）
     * @param size        获取数量（-1 为无限制）
     * @param desc        是否倒序
     * @param valueColumn 获取数据列
     * @return {@link LinkedList}
     */
    public LinkedList<Map<String, Object>> getValues(String name, String sortColumn, int size, boolean desc, String... valueColumn) {
        LinkedList<Map<String, Object>> list = new LinkedList<>();
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            if (desc) {
                pstmt = connection.prepareStatement("select * from `" + name + "` order by `" + sortColumn + "` desc" + (size < 0 ? "" : " limit " + size));
            } else {
                pstmt = connection.prepareStatement("select * from `" + name + "` order by `" + sortColumn + "`" + (size < 0 ? "" : " limit " + size));
            }
            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                Map<String, Object> map = new HashMap<>();
                for (String _column : valueColumn) {
                    map.put(_column, resultSet.getObject(_column));
                }
                list.add(map);
            }
        } catch (Exception e) {
            print("数据库命令执行出错");
            print("错误类型: getValues(String,String,int,boolean,String...);");
            print("错误原因: " + e.getMessage());
            // 重新连接
            if (fallReconnection && e.getMessage().contains("closed")) {
                connect();
            }
        } finally {
            freeResult(resultSet, pstmt);
        }
        return list;
    }

    // 输出指令
    public boolean execute(String sql) {
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement(sql);
            pstmt.execute();
            return true;
        } catch (Exception e) {
            print("数据库命令执行出错");
            print("错误类型: execute(String);");
            print("错误原因: " + e.getMessage());
            print("错误命令: " + sql);
            // 重连
            if (e.getMessage().contains("closed")) {
                connect();
            }
            return false;
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                //
            }
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

    /**
     * 释放结果集
     *
     * @param resultSet 不知道叫什么
     * @param pstmt     不知道叫什么
     */
    private void freeResult(ResultSet resultSet, PreparedStatement pstmt) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (Exception e) {
            //
        }
        try {
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (Exception e) {
            //
        }
    }

    private boolean loadDriverMySQL() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public enum ColumnInteger {

        TINYINT, SMALLINT, MEDIUMINT, INT, BIGINT
    }

    public enum ColumnFloat {

        FLOAT, DOUBLE
    }

    public enum ColumnChar {

        CHAR, VARCHAR
    }

    public enum ColumnString {

        TINYTEXT, TEXT, MEDIUMTEXT, LONGTEXT
    }

    public static class Column {

        private String name;
        private Object type;
        private int a;
        private int b;

        public Column(String name) {
            this.name = name;
            this.type = ColumnString.TEXT;
        }

        public Column(String name, ColumnInteger type) {
            this(name);
            this.type = type;
            this.a = 12;
        }

        public Column(String name, ColumnInteger type, int m) {
            this(name);
            this.type = type;
            this.a = m;
        }

        public Column(String name, ColumnFloat type, int m, int d) {
            this(name);
            this.type = type;
            this.a = m;
            this.b = d;
        }

        public Column(String name, ColumnChar type, int n) {
            this(name);
            this.type = type;
            this.a = n;
        }

        public Column(String name, ColumnString type) {
            this(name);
            this.type = type;
        }

        public String toString() {
            if (type instanceof ColumnInteger || type instanceof ColumnChar) {
                return "`" + name + "` " + type.toString().toLowerCase() + "(" + a + ")";
            } else if (type instanceof ColumnFloat) {
                return "`" + name + "` " + type.toString().toLowerCase() + "(" + a + "," + b + ")";
            } else {
                return "`" + name + "` " + type.toString().toLowerCase();
            }
        }
    }
}
