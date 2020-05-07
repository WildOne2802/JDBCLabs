/*
 * This code is sample code, provided as-is, and we make no
 * warranties as to its correctness or suitablity for
 * any purpose.
 *
 * We hope that it's useful to you.  Enjoy.
 * Copyright 2004-8 LearningPatterns Inc.
 */

package com.javatunes.util;

import java.sql.*;
import java.util.Collection;
import java.util.ArrayList;

public class ItemDAO {
    // connection to the database (assumed to be open)
    private Connection m_conn = null;

    //// PreparedStatement Lab ////
    //-- declare the PreparedStatement for searchByKeyword --//
    PreparedStatement pstmtSearchByKey = null;
    //// Update Lab ////
    //-- declare the PreparedStatement for create --//
    PreparedStatement pstmtCreate = null;

    // constructor
    public ItemDAO(Connection conn) throws SQLException {
        // store the connection
        m_conn = conn;

        //// PreparedStatement Lab ////
        //-- define the ?-SQL for searchByKeyword --//
        String sqlSearchByKey = "SELECT * FROM GUEST.ITEM WHERE TITLE LIKE ?";

        //-- prepare the ?-SQL with the DBMS and initialize the PreparedStatement --//
        pstmtSearchByKey = m_conn.prepareStatement(sqlSearchByKey);

        //// Update Lab ////
        //-- define the ?-SQL for create --//
        String sqlCreate = "INSERT INTO GUEST.ITEM (TITLE, ARTIST, RELEASEDATE, LISTPRICE, PRICE, VERSION) VALUES (?, ?, ?, ?, ?, ?)";

        //-- prepare the ?-SQL with the DBMS and initialize the PreparedStatement --//
        pstmtCreate = m_conn.prepareStatement(sqlCreate);
    }


    //// DAO Lab ////
    public MusicItem searchById(Long id)
            throws SQLException {
        // declare return value
        MusicItem result = null;

        // declare JDBC objects
        Statement stmt = null;

        //-- build the SQL statement --//
        String sql = "SELECT * FROM GUEST.ITEM WHERE ITEM_Id = " + id.toString();

        try {
            //-- initialize the Statement object --//
            stmt = m_conn.createStatement();

            //-- execute the SQL statement, get a ResultSet back --//
            ResultSet rs = stmt.executeQuery(sql);

            //-- if ID found, extract data from the ResultSet and initialize the ItemValue return value --//
            //-- if ID not found, the return value remains null --//
            rs.next();
            if (rs.getRow() != 0) {
                result = new MusicItem(rs.getLong(1), rs.getString(2), rs.getString(3),
                        rs.getDate(4), rs.getBigDecimal(5), rs.getBigDecimal(6));
            }

        } finally {
            //-- close the Statement - this closes its ResultSet --//
            stmt.close();
        }

        // return the value object
        return result;
    }


    //// PreparedStaement Lab ////
    public Collection<MusicItem> searchByKeyword(String keyword)
            throws SQLException {
        // create storage for the results
        Collection<MusicItem> result = new ArrayList<MusicItem>();

        // create the %keyword% wildcard syntax used in SQL LIKE operator
        String wildcarded = "%" + keyword + "%";

        //-- set the ? parameters on the PreparedStatement --//
        pstmtSearchByKey.setString(1, wildcarded);

        //-- execute the PreparedStatement, get a ResultSet back --//
        ResultSet rs = pstmtSearchByKey.executeQuery();

        //-- iterate through the ResultSet, extracting data from each row and creating an ItemValue from it --//
        //-- add the ItemValue to the Collection via Collection's add method --//
        while (rs.next()) {
            result.add(new MusicItem(rs.getLong(1), rs.getString(2), rs.getString(3),
                    rs.getDate(4), rs.getBigDecimal(5), rs.getBigDecimal(6)));
        }

        // return the Collection
        return result;
    }


    //// Update Lab ////
    public void create(MusicItem item)
            throws SQLException {
        // Use the following releaseDate value in the  prepared statement for setDate
        java.sql.Date releaseDate = new java.sql.Date(item.getReleaseDate().getTime());
        //-- set the ? parameters on the PreparedStatement --//
        pstmtCreate.setString(1, item.getTitle());
        pstmtCreate.setString(2, item.getArtist());
        pstmtCreate.setDate(3, releaseDate);
        pstmtCreate.setBigDecimal(4, item.getListPrice());
        pstmtCreate.setBigDecimal(5, item.getPrice());
        pstmtCreate.setInt(6, 1);

        //-- execute the PreparedStatement - ignore the update count --//
        System.out.println(pstmtCreate.executeUpdate());
        m_conn.commit();
    }

    public void swap(int idFirst, int idSecond) throws SQLException{
        PreparedStatement preparedStatement = null;
        String sql = "UPDATE t1 SET t1.PRICE = t2.PRICE FROM GUEST.ITEM t1 INNER JOIN GUEST.ITEM t2 ON (t2.ID = ? AND t1.ID = ?) OR (t2.ID = ? AND t1.ID = ?)";
        preparedStatement = m_conn.prepareStatement(sql);
        preparedStatement.setInt(1, idFirst);
        preparedStatement.setInt(2, idSecond);
        preparedStatement.setInt(3, idSecond);
        preparedStatement.setInt(4,idFirst);
        preparedStatement.executeUpdate();
        m_conn.commit();
    }

    public void showAll() throws SQLException{
        String sql = "SELECT * FROM GUEST.ITEM";
        Statement stmt = m_conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while(rs.next()){
            System.out.println(new MusicItem(rs.getLong(1), rs.getString(2), rs.getString(3),
                    rs.getDate(4), rs.getBigDecimal(5), rs.getBigDecimal(6)).toString());
        }
}

    //// PreparedStatement and Update Labs ////
    public void close() {

        try {
            //// PreparedStatement Lab ////
            //-- close the PreparedStatement for searchByKeyword --//
            pstmtSearchByKey.close();

            //// Update Lab ////
            //-- close the PreparedStatement for create --//
            pstmtCreate.close();
        } catch (SQLException sqle) {
            JDBCUtilities.printSQLException(sqle);
        }
    }
}

// TODO:
// еще как можно кроме коммита -
// нужно ли вызывать коммит
// что такое autocommit
// update method
// gets 2 id
// swap price
// atom