package pl.coderslab;

import pl.coderslab.entity.User;
import pl.coderslab.entity.UserDao;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        UserDao userDao = new UserDao();

        ArrayList listOfUsers = userDao.findAll();
        listOfUsers.forEach(System.out :: println);

        System.out.println(userDao.read(4));
        System.out.println(userDao.read(5));
        System.out.println(userDao.read(6));

    }
}
