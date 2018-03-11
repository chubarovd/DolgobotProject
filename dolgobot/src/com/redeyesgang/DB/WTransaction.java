package com.redeyesgang.DB;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class WTransaction implements ITransaction {
    final private Connection _conn;
    final private Properties _props;


    public WTransaction(Connection conn, Properties props) {
        _conn = conn;
        _props = props;
    }


    @Override
    public int addTransaction(Transaction trans) throws SQLException {
        PreparedStatement ps = _conn.prepareStatement(_props.getProperty("createTransaction"));
        ps.setLong(1, trans.getFromId());
        ps.setLong(2, trans.getToId());
        ps.setLong(3,trans.getAmount());
        ps.setString(4,trans.getDescription());
        Date dt = new Date(System.currentTimeMillis());
        ps.setDate(5, dt);
        ps.executeUpdate();
        ps.close();
        ps = _conn.prepareStatement(_props.getProperty("getTransactionID"));
        ps.setLong(1, trans.getFromId());
        ps.setLong(2, trans.getToId());
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
        long from,to;
        int amount;
        if (rs.next()) {
            from = rs.getLong(1);
            to = rs.getLong(2);
            amount = rs.getInt(3);
        }else{
            throw new TransactionException("Database error!");
        }
        rs.close();
        ps.close();
        updateTotal(from,to,amount);
    }

    @Override
    public void cancel(int transactID) throws SQLException {
        PreparedStatement ps = _conn.prepareStatement(_props.getProperty("transactCancel"));
        ps.setInt(1,transactID);
        ps.executeUpdate();
        ps.close();
    }


    private void updateTotal(long from, long to, int amount) throws SQLException {
        PreparedStatement ps = _conn.prepareStatement(_props.getProperty("getTotal"));
        ps.setLong(1,from);
        ps.setLong(2,to);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int total = rs.getInt(1);
            PreparedStatement pss = _conn.prepareStatement(_props.getProperty("updateTotal"));
            pss.setLong(1,total+amount);
            pss.setLong(2,from);
            pss.setLong(3,to);
            pss.executeUpdate();
            pss.setLong(1,total-amount);
            pss.setLong(3,from);
            pss.setLong(2,to);
            pss.executeUpdate();
            pss.close();
        }else {
            PreparedStatement pss = _conn.prepareStatement(_props.getProperty("createTotal"));
            pss.setLong(1,from);
            pss.setLong(2,to);
            pss.setLong(3,amount);
            pss.executeUpdate();
            pss.setLong(2,from);
            pss.setLong(1,to);
            pss.setLong(3,-amount);
            pss.executeUpdate();
            pss.close();
        }
        rs.close();
        ps.close();
    }
}
