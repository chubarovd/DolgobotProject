package com.redeyesgang.DB;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class WTransaction implements ITransaction, IGetTotal{
    final private Connection _conn;
    final private Properties _props;


    public WTransaction(Connection conn, Properties props) {
        _conn = conn;
        _props = props;
    }


    @Override
    public int addTransaction(Transaction trans) throws SQLException {
        PreparedStatement ps = _conn.prepareStatement(_props.getProperty("createTransaction"));
        ps.setInt(1, trans.getFromId());
        ps.setInt(2, trans.getToId());
        ps.setInt(3,trans.getAmount());
        ps.setString(4,trans.getDescription());
        Date dt = new Date(System.currentTimeMillis());
        ps.setDate(5, dt);
        ps.executeUpdate();
        ps.close();
        ps = _conn.prepareStatement(_props.getProperty("getTransactionID"));
        ps.setInt(1, trans.getFromId());
        ps.setInt(2, trans.getToId());
        ps.setDate(3, dt);
        ResultSet rs = ps.executeQuery();
        int result=-1;
        if (rs.next()) {
            result = rs.getInt(1);
        }
        rs.close();
        ps.close();
        return result;
    }


    @Override
    public Map<Integer, Integer> getTotal(int userID) throws SQLException {
        Map<Integer,Integer> result = new HashMap<>();
        PreparedStatement ps = _conn.prepareStatement(_props.getProperty("getTotalByID"));
        ps.setInt(1,userID);
        ResultSet rs =  ps.executeQuery();
        while(rs.next()) {
            result.put(rs.getInt(1),rs.getInt(2));
        }
        rs.close();
        ps.close();
        return result;
    }


    @Override
    public void validate(int transactID) throws SQLException, TransactionException {
        PreparedStatement ps = _conn.prepareStatement(_props.getProperty("validate"));
        ps.setInt(1,transactID);
        ps.executeUpdate();
        ps.close();
        ps = _conn.prepareStatement(_props.getProperty("getTransactInfo"));
        ps.setInt(1,transactID);
        ResultSet rs = ps.executeQuery();
        int from,to,amount;
        if (rs.next()) {
            from = rs.getInt(1);
            to = rs.getInt(2);
            amount = rs.getInt(3);
        }else{
            throw new TransactionException("Database error!");
        }
        rs.close();
        ps.close();
        updateTotal(from,to,amount);
    }


    private void updateTotal(int from, int to, int amount) throws SQLException {
        PreparedStatement ps = _conn.prepareStatement(_props.getProperty("getTotal"));
        ps.setInt(1,from);
        ps.setInt(2,to);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int total = rs.getInt(1);
            PreparedStatement pss = _conn.prepareStatement(_props.getProperty("updateTotal"));
            pss.setInt(1,total+amount);
            pss.setInt(2,from);
            pss.setInt(3,to);
            pss.executeUpdate();
            pss.setInt(1,total-amount);
            pss.setInt(3,from);
            pss.setInt(2,to);
            pss.executeUpdate();
            pss.close();
        }else {
            PreparedStatement pss = _conn.prepareStatement(_props.getProperty("createTotal"));
            pss.setInt(1,from);
            pss.setInt(2,to);
            pss.setInt(3,amount);
            pss.executeUpdate();
            pss.setInt(2,from);
            pss.setInt(1,to);
            pss.setInt(3,-amount);
            pss.executeUpdate();
            pss.close();
        }
        rs.close();
        ps.close();
    }
}
