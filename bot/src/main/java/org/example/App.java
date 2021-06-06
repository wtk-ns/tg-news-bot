package org.example;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;

import java.util.Date;
import java.util.logging.Logger;

public class App
{

    static Logger log = Logger.getLogger(App.class.getName());


    public static void main( String[] args )
    {

        ApiContextInitializer.init();

        TelegramBotsApi api = new TelegramBotsApi();
        try {
            Bot bot = new Bot();
            api.registerBot(bot);
            NewsCollector nc = new NewsCollector();
            nc.collectNews(8f, bot);


        } catch (Exception e)
        {
            log.info(e.toString());
        }

    }
}
