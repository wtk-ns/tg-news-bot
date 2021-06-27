package org.example;


import java.sql.*;
import java.util.ArrayList;
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
                prepareStatement("INSERT INTO subscribers VALUES (" + subscriber.getChatID() + ", "
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

        subscribers.remove(subscriber);
        prepareStatement("DELETE FROM subscribers WHERE chatid=" + subscriber.getChatID() + ";");

    }

    public static void getSubListFromBase(){
        try {


            ResultSet resultSet = createStatement().executeQuery("SELECT * FROM subscribers");

            while (resultSet.next()){
                Subscriber sub = new Subscriber(resultSet.getLong(1), resultSet.getInt(2));
                subscribers.add(sub);
                System.out.println(sub.getChatID());
            }

            resultSet.close();
            System.out.println("Got all subs from list");

        } catch (SQLException throwables) {
            throwables.printStackTrace();

        }
    }

    public static ArrayList<Subscriber> getSubscribersList(){
        return subscribers;
    }

    public static void insertNewSettings(Integer settings, Subscriber subscriber){


        prepareStatement("UPDATE subscribers SET settings=" + settings + " WHERE chatid=" + subscriber.getChatID());

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

    private static void prepareStatement(String sql){
        try {
            PreparedStatement ps = DriverManager.getConnection(Objects.requireNonNull(Constants.getDBurl()), Constants.getPropertiesForDB()).prepareStatement(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void sql(String text){

        try {
            createStatement().executeQuery(text);
        } catch (SQLException throwables) {
            makeExceptionInfo("SQL done", throwables);
        }
    }

}
