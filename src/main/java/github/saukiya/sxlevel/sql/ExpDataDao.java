package github.saukiya.sxlevel.sql;

import github.saukiya.sxlevel.SXLevel;
import github.saukiya.sxlevel.data.ExpData;
import github.saukiya.sxlevel.util.Config;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExpDataDao {

    private final String table = SXLevel.getPlugin().getConfig().getString(Config.SQL_TABLE_NAME);
    private final String createTable = "CREATE TABLE `" + table + "`  (\r\n" + "  `name` varchar(255) NOT NULL,\r\n"
            + "  `exp` int(255) NOT NULL,\r\n" + "  `level` int(255) NOT NULL,\r\n" + "  PRIMARY KEY (`name`)\r\n"
            + ");";
    public Connection connection;
    public ExpDataDao(MySQLConnection mysql) {
        connection = mysql.getConnection();
        if (!mysql.exitsTable(table))
            mysql.execute(createTable);
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean add(ExpData t) {
        String sql = "INSERT INTO " + table + "(name, exp, level) values(?,?,?)";
        try {
            PreparedStatement ptmt = getConnection().prepareStatement(sql);
            ptmt.setString(1, t.getPlayer().getName());
            ptmt.setInt(2, t.getExp());
            ptmt.setInt(3, t.getLevel());
            try {
                return ptmt.executeUpdate() >= 0;
            } catch (SQLException e) {
                if (e.getMessage().startsWith("[SQLITE_CONSTRAINT_PRIMARYKEY]")
                        || e.getMessage().startsWith("Duplicate entry")) {
                    return update(t.getPlayer().getName(), t);
                }
                return false;
            }
        } catch (Exception e) {
            if (e.getMessage().contains("closed")) {
                SXLevel.getPlugin().getMysql().connect();
                add(t);
            }
        }
        return false;
    }

    public boolean update(String name, ExpData t) throws SQLException {
        String sql = "UPDATE " + table + " set exp=?, level=? where name=?";
        try {
            PreparedStatement ptmt = getConnection().prepareStatement(sql);
            ptmt.setInt(1, t.getExp());
            ptmt.setInt(2, t.getLevel());
            ptmt.setString(3, name);
            return ptmt.executeUpdate() >= 0;
        } catch (Exception e) {
            if (e.getMessage().contains("closed")) {
                SXLevel.getPlugin().getMysql().connect();
                update(name, t);
            }
        }
        return false;
    }

    public void del(String name) throws SQLException {
        String sql = "delete from " + table + " where name=?";
        try {
            PreparedStatement ptmt = getConnection().prepareStatement(sql);
            ptmt.setString(1, name);
            ptmt.execute();
        } catch (Exception e) {
            if (e.getMessage().contains("closed")) {
                SXLevel.getPlugin().getMysql().connect();
                del(name);
            }
        }
    }

    public List<ExpData> queryAll() throws SQLException {
        String sql = "SELECT * FROM " + table;
        PreparedStatement ptmt = getConnection().prepareStatement(sql);

        ResultSet rs = ptmt.executeQuery();

        List<ExpData> listData = new ArrayList<ExpData>();
        ExpData data = null;
        while (rs.next()) {
            Player playerExact = Bukkit.getPlayerExact(rs.getString("name"));
            if (playerExact != null) {
                data = new ExpData(playerExact, rs.getInt("exp"), rs.getInt("level"));
                listData.add(data);
            }
        }
        return listData;
    }

    public ExpData get(String name) {
        ExpData data = null;
        String sql = "select * from  " + table + " where name=?";
        try {
            PreparedStatement ptmt = getConnection().prepareStatement(sql);
            ptmt.setString(1, name);
            ResultSet rs = ptmt.executeQuery();
            while (rs.next()) {
                Player playerExact = Bukkit.getPlayerExact(rs.getString("name"));
                if (playerExact != null) {
                    data = new ExpData(playerExact, rs.getInt("exp"), rs.getInt("level"));
                }
            }
            return data;
        } catch (Exception e) {
            if (e.getMessage().contains("closed")) {
                SXLevel.getPlugin().getMysql().connect();
                get(name);
            }
        }
        return data;
    }

    public void close() {
        try {
            getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
