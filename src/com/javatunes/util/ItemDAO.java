/*
 * This code is sample code, provided as-is, and we make no
 * warranties as to its correctness or suitablity for
 * any purpose.
 *
 * We hope that it's useful to you.  Enjoy.
 * Copyright 2004-8 LearningPatterns Inc.
 */

package com.javatunes.util;

import org.apache.derby.client.am.Decimal;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Collection;
import java.util.ArrayList;

import static java.sql.Connection.TRANSACTION_SERIALIZABLE;

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
        String sql = "SELECT * FROM GUEST.ITEM WHERE ITEM_ID = " + id.toString();

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

    public void swap(int idFirst, int idSecond) throws SQLException {

        PreparedStatement statement = null;

        m_conn.setAutoCommit(false);

        m_conn.setTransactionIsolation(TRANSACTION_SERIALIZABLE);

        String selectIdFirst = "SELECT PRICE FROM GUEST.ITEM WHERE ITEM_ID = ?";
        String selectIdSecond = "SELECT PRICE FROM GUEST.ITEM WHERE ITEM_ID = ?";
        ResultSet rsSelectIdFirst;
        ResultSet rsSelectIdSecond;

        statement = m_conn.prepareStatement(selectIdFirst);
        statement.setInt(1, idFirst);
        rsSelectIdFirst = statement.executeQuery();
        rsSelectIdFirst.next();
        BigDecimal a = rsSelectIdFirst.getBigDecimal(1);

        statement = m_conn.prepareStatement(selectIdSecond);
        statement.setInt(1, idSecond);
        rsSelectIdSecond = statement.executeQuery();
        rsSelectIdSecond.next();
        BigDecimal b = rsSelectIdSecond.getBigDecimal(1);

        String finalSql = "UPDATE GUEST.ITEM SET PRICE = CASE" +
                " WHEN ITEM_ID = ? THEN " + b +
                " WHEN ITEM_ID = ? THEN " + a +
                " END" +
                " WHERE ITEM_ID IN (? , ?)";

        statement = m_conn.prepareStatement(finalSql);
        statement.setInt(1, idFirst);
        statement.setInt(2, idSecond);
        statement.setInt(3, idFirst);
        statement.setInt(4, idSecond);
        statement.executeUpdate();

        m_conn.commit();
        m_conn.setAutoCommit(true);


//        String sql = "UPDATE GUEST.ITEM SET PRICE = CASE" +
//                " WHEN ITEM_ID = ? THEN (SELECT PRICE FROM GUEST.ITEM WHERE ITEM_ID = ?)" +
//                " WHEN ITEM_ID = ? THEN (SELECT PRICE FROM GUEST.ITEM WHERE ITEM_ID = ?)" +
//                " END" +
//                " WHERE ITEM_ID IN (? , ?)";
//
//        statement = m_conn.prepareStatement(sql);
//
//        statement.setInt(1, idFirst);
//        statement.setInt(2, idSecond);
//        statement.setInt(3, idSecond);
//        statement.setInt(4, idFirst);
//        statement.setInt(5, idFirst);
//        statement.setInt(6, idSecond);
//        statement.executeUpdate();
//        m_conn.commit();
    }

    public void showAll() throws SQLException {
        String sql = "SELECT * FROM GUEST.ITEM";
        Statement stmt = m_conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
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
//
// Когда мы работает с JDBC, то по умолчанию наше соединение работает в режиме auto-commit,
// это означает, что каждый SQL – запрос будет выполнен и результаты будут сохранены в таблице нашей базы данных (далее – БД).
// Для простых приложений это крайне удобно. Но, если мы хотим увеличить производительность,
// использовать распределённые транзакции, либо интегрировать бизнес-логику,
// то нам необходимо выключить режим auto-commit для управления нашими транзакциями.
//
// update method
// gets 2 id
// swap price
// atom +
//
// Если вы хотите выполнить несколько операторов атомарно, вам нужно использовать транзакцию.
// Соединение JDBC по умолчанию использует режим "auto-commit", что означает, что каждый оператор выполняется в своей транзакции.
// Поэтому сначала нужно отключить режим автоматической фиксации, используя Connection.setAutoCommit(false).
//
// При отключенном режиме автоматической фиксации исполняемые операторы будут выполняться в текущей транзакции,
// если нет текущей транзакции, она будет запущена. Затем эта транзакция может быть выполнена с использованием
// Connection.commit() или откат с помощью Connection.rollback().
//
// SERIALIZABLE
// Указывает следующее.
// Инструкции не могут считывать данные, которые были изменены другими транзакциями, но еще не были зафиксированы.
// Другие транзакции не могут изменять данные, считываемые текущей транзакцией, до ее завершения.
// Другие транзакции не могут вставлять новые строки со значениями ключа, которые входят в диапазон ключей,
// считываемых инструкциями текущей транзакции, до ее завершения.
// Блокировка диапазона устанавливается в диапазоне значений ключа, соответствующих условиям поиска любой инструкции,
// выполненной во время транзакции. Обновление и вставка строк, удовлетворяющих инструкциям текущей транзакции,
// блокируется для других транзакций. Это гарантирует, что если какая-либо инструкция транзакции выполняется повторно,
// она будет считывать тот же самый набор строк. Блокировки диапазона сохраняются до завершения транзакции.
// Это самый строгий уровень изоляции, поскольку он блокирует целые диапазоны ключей и сохраняет блокировку
// до завершения транзакции. Из-за низкого параллелизма этот параметр рекомендуется использовать только при необходимости.
// Этот параметр действует так же, как и настройка HOLDLOCK всех таблиц во всех инструкциях SELECT в транзакции.