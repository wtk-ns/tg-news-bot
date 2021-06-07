package org.example;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;

import java.util.Date;
import java.util.logging.Logger;

public class App
{

    static Logger log = Logger.getLogger(App.class.getName());

    private static final String bot_name = "wtcnstestbot";
    private static final String bot_token = "1820940667:AAF8C1DxFx2Bmjx5GlnQKJW4yD_i4dkrcQc";

    public static void main( String[] args )
    {

        ApiContextInitializer.init();
        TelegramBotsApi api = new TelegramBotsApi();

        try {
            api.registerBot(new Bot(bot_name,bot_token));
        } catch (Exception e)
        {
            log.info(e.toString());
        }

    }
}
