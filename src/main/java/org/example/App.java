package org.example;

import com.rometools.rome.feed.synd.SyndEntry;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;

import java.util.*;
import java.util.logging.Logger;

public class App
{

    private static final Map<String, String> getenv = System.getenv();

    public static void main( String[] args ) {

        ApiContextInitializer.init();
        TelegramBotsApi api = new TelegramBotsApi();

        try {

            api.registerBot(new Bot(getenv.get("BOT_NAME"), getenv.get("BOT_TOKEN")));

        } catch (Exception exception)
        {
            System.out.println("Error in Main");
            exception.printStackTrace();

        }




    }

}
