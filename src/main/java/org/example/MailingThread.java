package org.example;

import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MailingThread implements Runnable{

    private final Bot bot;


    public MailingThread(Bot bot){
        this.bot=bot;
    }

    @Override
    public void run() {

        while (true){


            if (LocalTime.now(bot.zone).getHour() == 8 && LocalTime.now(bot.zone).getMinute() == 0 && LocalTime.now(bot.zone).getSecond() == 0){
                getNews(12);
            } else if (LocalTime.now(bot.zone).getHour() == 14 && LocalTime.now(bot.zone).getMinute() == 0 && LocalTime.now(bot.zone).getSecond() == 0){
                getNews(6);
            } else if (LocalTime.now(bot.zone).getHour() == 18 && LocalTime.now(bot.zone).getMinute() == 47 && LocalTime.now(bot.zone).getSecond() == 0){
                getNews(12);
            }


            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("MailingThread");
            }
        }

    }

    private void getNews(int ammountHours){

        Parser parser = bot.makeParser(-ammountHours);

        try {
            bot.mailingForAllSubs(parser.parse(bot.getRssFromList(0)));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


}
