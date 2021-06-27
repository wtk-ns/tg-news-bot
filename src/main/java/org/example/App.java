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

        //DataBase.sql("CREATE TABLE subscribers (chatid bigint UNIQUE, settings int);");
        DataBase.sql("DELETE FROM subscribers");

/*
        try {
            api.registerBot(new Bot());
        } catch (TelegramApiRequestException exception)
        {
            exception.printStackTrace();
        }



*/

    }

}
