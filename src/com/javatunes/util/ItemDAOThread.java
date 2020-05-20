package com.javatunes.util;

import java.sql.SQLException;

public class ItemDAOThread extends Thread {
    ItemDAO itemDAO;
    int a;
    int b;

    ItemDAOThread(ItemDAO itemDAO, int a, int b){
        this.itemDAO = itemDAO;
        this.a=a;
        this.b=b;
    }

    public void run() {
        try {
            itemDAO.swap(a, b);
        } catch (SQLException e) {
        }

    }
}
