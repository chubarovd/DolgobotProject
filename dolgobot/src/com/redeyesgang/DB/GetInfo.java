package com.redeyesgang.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class GetInfo implements IGetInfo {
    final private Connection _conn;
    final private Properties _props;

    public GetInfo(Connection con, Properties props) {
        _conn = con;
        _props = props;
    }

    @Override
    public Map<Long,Long> getTotal(long userID) throws SQLException {
        Map<Long,Long> result = new HashMap<>();
        PreparedStatement ps = _conn.prepareStatement(_props.getProperty("getTotalByID"));
        ps.setLong(1,userID);
        ResultSet rs =  ps.executeQuery();
        while(rs.next()) {
            result.put(rs.getLong(1),rs.getLong(2));
        }
        rs.close();
        ps.close();
        return result;
    }

    @Override
    public List<String> getGroupInfo(String groupName) {
        return null;
    }
}
