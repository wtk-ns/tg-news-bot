package org.example;


import com.rometools.rome.feed.synd.SyndEntry;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class DataBase {

    private static ArrayList<Subscriber> subscribers = new ArrayList<>();


    public static Subscriber getSubscriber(Long chatID){

        for (Subscriber sub : subscribers){
            if (sub.getChatID().equals(chatID)){
                return sub;
            }
        }

        Subscriber newSub = new Subscriber(chatID);
        addSubscriber(newSub);
        return subscribers.get(subscribers.indexOf(newSub));
    }

    public static void addSubscriber(Subscriber subscriber){

        try {
            if (!hasInSubscriberTable(subscriber)) {
                subscribers.add(subscriber);
                createStatement().executeQuery("INSERT INTO subscribers VALUES (" + subscriber.getChatID() + ", "
                        + subscriber.getParseGap() + ");");
                System.out.println("Insert done");
            } else {
                System.out.println("Already in subs");
            }
        } catch (SQLException throwables) {
            makeExceptionInfo("Insert successfully", throwables);
        }
    }

    public static void deleteSubscriber(Subscriber subscriber){

        try {
            subscribers.remove(subscriber);
            createStatement().executeQuery("DELETE FROM subscribers WHERE chatid=" + subscriber.getChatID() + ";");
        } catch (SQLException throwables) {
            makeExceptionInfo("Deleted", throwables);
        }

    }

    public static void insertDailyNews(){
        try {
            List<SyndEntry> list = Parser.parse(Journals.VC.getRssUrl(), Constants.defaultAmountOfHoursForParse);

            Comparator<SyndEntry> comparator = (o1, o2) -> {
                if (o1.getPublishedDate().after(o2.getPublishedDate()))
                {
                    return 1;
                } else if (o1.getPublishedDate().before(o2.getPublishedDate())) {
                    return -1;
                } else return 0;

            };
            list.sort(comparator);


            for (SyndEntry s : list){

                try {
                    PreparedStatement ps = prepareStatement("" +
                            "INSERT INTO news VALUES ('VC','" + s.getPublishedDate() + "','" + s.getTitle() + "','" + s.getLink() + "');");
                    ps.execute();
                } catch (Exception e){

                }
            }

            list = Parser.parse(Journals.TJ.getRssUrl(), Constants.defaultAmountOfHoursForParse);
            list.sort(comparator);

            for (SyndEntry s : list){
                try {
                    PreparedStatement ps = prepareStatement("" +
                            "INSERT INTO news VALUES ('TJ','" + s.getPublishedDate() + "','" + s.getTitle() + "','" + s.getLink() + "');");
                    ps.execute();
                } catch (Exception e){


                }
            }

            list = Parser.parse(Journals.KOD.getRssUrl(), Constants.defaultAmountOfHoursForParse);
            list.sort(comparator);

            for (SyndEntry s : list){
                try {
                    PreparedStatement ps = prepareStatement("" +
                            "INSERT INTO news VALUES ('KOD','" + s.getPublishedDate() + "','" + s.getTitle() + "','" + s.getLink() + "');");
                    ps.execute();
                } catch (Exception e){


                }
            }

            System.out.println("Inserting news Done");

        } catch (Exception e) {
            e.printStackTrace();
        } {

        }

    }

    private static PreparedStatement prepareStatement(String sql) throws Exception{
        return DriverManager.getConnection(Constants.getDBurl(), Constants.getPropertiesForDB()).prepareStatement(sql);
    }

    public static void getSubListFromBase(){
        try {


            ResultSet resultSet = createStatement().executeQuery("SELECT * FROM subscribers");

            while (resultSet.next()){
                Subscriber sub = new Subscriber(resultSet.getLong(1), resultSet.getInt(2));
                subscribers.add(sub);
            }

            resultSet.close();


        } catch (SQLException throwables) {
            throwables.printStackTrace();

        }
    }

    public static ArrayList<Subscriber> getSubscribersList(){
        return subscribers;
    }

    public static void insertNewSettings(Integer settings, Subscriber subscriber){


        try {
            createStatement().executeQuery("UPDATE subscribers SET settings=" + settings + " WHERE chatid=" + subscriber.getChatID());
        } catch (SQLException throwables) {
            makeExceptionInfo("Insert settings done", throwables);
        }

    }




    private static void makeExceptionInfo(String text, SQLException e){
        if (e.getSQLState().equals("02000")){
            System.out.println(text);
        } else
        {
            e.printStackTrace();
        }
    }

    private static Boolean hasInSubscriberTable(Subscriber subscriber) throws SQLException {

        ResultSet resultSet = createStatement().executeQuery("SELECT FROM subscribers * WHERE chatid=" + subscriber.getChatID() + ";");
        return resultSet.next();
    }

    public static Boolean hasInSubscribers(Subscriber subscriber){

        for (Subscriber sub : subscribers){
            if (sub.getChatID().equals(subscriber.getChatID())){
                return true;
            }
        }
        return false;

    }

    private static Statement createStatement() throws SQLException {
        return DriverManager.getConnection(Objects.requireNonNull(Constants.getDBurl()), Constants.getPropertiesForDB()).createStatement();
    }


    public static void sql(String text){

        try {
            createStatement().executeQuery(text);
        } catch (SQLException throwables) {
            makeExceptionInfo("SQL done", throwables);
        }
    }

}
