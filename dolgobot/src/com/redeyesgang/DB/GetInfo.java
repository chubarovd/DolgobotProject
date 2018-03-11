package com.redeyesgang.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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
    public List< Long> getGroupInfo(String groupName) throws SQLException, OnCreateException {
        PreparedStatement ps = _conn.prepareStatement(_props.getProperty("getGroupID"));
        ps.setString(1,groupName);
        ResultSet rs = ps.executeQuery();
        if (!rs.next()) {
            rs.close();
            ps.close();
            throw new OnCreateException("Такой группы не существует!");
        }
        rs.close();
        ps.close();

        List<Long> res = new ArrayList<>();
        ps = _conn.prepareStatement(QueryBuilderForGroup.getSelectFromGroupQuery(groupName));
        rs = ps.executeQuery();
        while(rs.next()){
            res.add(rs.getLong(1));
        }
        return res;
    }
}
