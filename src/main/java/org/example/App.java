package org.example;

import com.rometools.rome.feed.synd.SyndEntry;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;

import java.util.*;
import java.util.logging.Logger;

public class App
{

    private final static String bot_name = "ultrasecretbot";
    private final static String bot_token = "1892433428:AAGcY5vDIFdi37SazDSxB7_s9iBcTwQ5o2k";


    public static void main( String[] args ) {

        ApiContextInitializer.init();
        TelegramBotsApi api = new TelegramBotsApi();

        try {

            api.registerBot(new Bot(bot_name,bot_token));

        } catch (Exception exception)
        {
            System.out.println("Main");

            exception.printStackTrace();

        }




    }

}
