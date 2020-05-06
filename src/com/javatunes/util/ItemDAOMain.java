package com.javatunes.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;

public class ItemDAOMain {
    public static void main(String[] args) throws SQLException {
        MusicItem mi;
        Connection conn = null;
        conn = DriverManager.getConnection("jdbc:derby://localhost:1527/JavaTunesDB");
        ItemDAO itemDAO = new ItemDAO(conn);
        mi = itemDAO.searchById(1L);
        System.out.println(mi.toString());
//        mi = itemDAO.searchById(100L);
//        mi.toString();
        Collection<MusicItem> cmi = itemDAO.searchByKeyword("Gay");
        System.out.println(cmi.toString());
        cmi = itemDAO.searchByKeyword("of");
        System.out.println(cmi.toString());

    }
}
