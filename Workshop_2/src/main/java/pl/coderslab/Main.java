package pl.coderslab;

import pl.coderslab.entity.User;
import pl.coderslab.entity.UserDao;

public class Main {
    public static void main(String[] args) {

//        User adam_małysz_12 = new User();
//        adam_małysz_12.setUserName("AdamMałysz");
//        adam_małysz_12.setEmail("lec_adas_lec@poczta.pl");
//        adam_małysz_12.setPassword("jebac_hannavalda");

        UserDao userDao = new UserDao();

//        userDao.create(adam_małysz_12);

        User test2 = userDao.read(1);
        System.out.println(test2.toString());
    }
}
