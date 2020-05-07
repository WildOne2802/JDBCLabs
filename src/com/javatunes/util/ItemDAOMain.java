package com.javatunes.util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Date;

public class ItemDAOMain {
    public static void main(String[] args) throws SQLException {
        MusicItem mi;
        Connection conn = null;

        conn = DriverManager.getConnection("jdbc:derby://localhost:1527/JavaTunesDB");

        ItemDAO itemDAO = new ItemDAO(conn);

        mi = itemDAO.searchById(1L);
        System.out.println(mi.toString());

//         mi = itemDAO.searchById(100L);
//         System.out.println(mi.toString());

        Collection<MusicItem> cmi = itemDAO.searchByKeyword("Ray");
        System.out.println(cmi.toString());

        cmi = itemDAO.searchByKeyword("of");
        System.out.println(cmi.toString());

//        MusicItem mi1 = new MusicItem(1L, "AUSLANDER", "RAMMSTEIN", new Date(119, 9, 2),
//                BigDecimal.valueOf(140.0), BigDecimal.valueOf(120.0));
//        itemDAO.create(mi1);

        cmi = itemDAO.searchByKeyword("AUSLANDER");
        System.out.println(cmi.toString());

//        String deleteSql = "DELETE FROM GUEST.ITEM WHERE TITLE = \'AUSLANDER\'";
//        Statement stmt = conn.createStatement();
//        stmt.executeUpdate(deleteSql);


        itemDAO.showAll();
        itemDAO.swap(1,2);
        itemDAO.showAll();
        itemDAO.close();

    }
}
