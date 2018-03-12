package com.redeyesgang.DB;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.*;

public class WTransaction implements ITransaction {
    final private Connection _conn;
    final private Properties _props;

    public WTransaction(Connection conn, Properties props) {
        _conn = conn;
        _props = props;

    }

    @Override
    public long addTransaction(Transaction trans) throws SQLException {
        PreparedStatement ps = _conn.prepareStatement(_props.getProperty("createTransaction"));
        ps.setLong(1, trans.getFromId());
        ps.setLong(2, trans.getToId());
        ps.setLong(3,trans.getAmount());
        ps.setString(4,trans.getDescription());
        Timestamp dt = new Timestamp(System.currentTimeMillis());
        ps.setTimestamp(5, dt);
        ps.executeUpdate();
        ps.close();
        ps = _conn.prepareStatement(_props.getProperty("getTransactionID"));
        ps.setLong(1, trans.getFromId());
        ps.setLong(2, trans.getToId());
        ps.setTimestamp(3, dt);
        ResultSet rs = ps.executeQuery();
        long result=-1;
        if (rs.next()) {
            result = rs.getLong(1);
        }
        rs.close();
        ps.close();
        return result;
    }

    @Override
    public Transaction validate(long transactID) throws SQLException, TransactionException {
        PreparedStatement ps = _conn.prepareStatement(_props.getProperty("validate"));
        ps.setLong(1,transactID);
        ps.executeUpdate();
        ps.close();
        ps = _conn.prepareStatement(_props.getProperty("getTransactInfo"));
        ps.setLong(1,transactID);
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
        return new Transaction(from,to,amount);
    }

    @Override
    public Transaction cancel(long transactID) throws SQLException, TransactionException {
        PreparedStatement ps = _conn.prepareStatement(_props.getProperty("getTransactInfo"));
        ps.setLong(1,transactID);
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
        ps = _conn.prepareStatement(_props.getProperty("transactCancel"));
        ps.setLong(1,transactID);
        ps.executeUpdate();
        ps.close();
        return new Transaction(from,to,amount);
    }

    @Override
    public List<Transaction> addTransactionToGroup(long telegramID, String groupName, int amount, String desc) throws SQLException, TransactionException {
        PreparedStatement ps = _conn.prepareStatement(_props.getProperty("getGroupID"));
        ps.setString(1,groupName);
        ResultSet rs = ps.executeQuery();
        if (!rs.next()) {
            ps.close();
            rs.close();
            throw new TransactionException("Такой группы не существует!");
        }
        ps.close();
        rs.close();
        ps = _conn.prepareStatement(QueryBuilderForGroup.getSelectFromGroupQuery(groupName));
        rs = ps.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        List<Long> users = new ArrayList<>(20);
        while(rs.next()) {
            long u= rs.getLong(1);
            if (u!=telegramID)
                users.add(u);
        }
        rs.close();
        ps.close();
        int pamount = amount / users.size();

        List<Transaction> res = new ArrayList<>(users.size());
        users.forEach((user)->{
            try {
                Transaction transaction = new Transaction(telegramID,user,pamount);
                transaction.setDescription(desc);
                long transID = addTransaction(transaction);
                transaction.setTransactID(transID);
                res.add(transaction);

            } catch (TransactionException | SQLException e) {
                //pass
            }
        });
        return res;


    }

    @Override
    public List<Transaction> getTransactions(long telegramID) throws SQLException, TransactionException {
        PreparedStatement ps = _conn.prepareStatement(_props.getProperty("getTransaction"));
        ps.setLong(1,telegramID);
        ResultSet rs = ps.executeQuery();
        List<Transaction> res = new ArrayList<>();
        while (rs.next()) {
            Transaction trans = new Transaction(rs.getLong(2),telegramID,rs.getInt(4));
            trans.setDescription(rs.getString(3)).setTransactID(rs.getLong(1));
            res.add(trans);
        }
        rs.close();
        ps.close();
        return res;
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
            pss.setLong(1,-total-amount);
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
