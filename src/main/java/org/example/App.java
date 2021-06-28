package org.example;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.ArrayList;
import java.util.Map;


public class App
{

    public static void main( String[] args ) {

        ApiContextInitializer.init();
        TelegramBotsApi api = new TelegramBotsApi();

        //DataBase.sql("CREATE TABLE news (resource varchar(10), publishedDate varchar(80), title varchar(200), link varchar(100) UNIQUE);");
        //DataBase.sql("DELETE FROM news");
        //DataBase.addSubscriber(new Subscriber(1L));


        try {
            api.registerBot(new Bot());
        } catch (TelegramApiRequestException exception)
        {
            exception.printStackTrace();
        }







    }

}
