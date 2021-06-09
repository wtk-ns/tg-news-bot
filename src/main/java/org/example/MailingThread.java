package org.example;

import java.time.LocalTime;
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


            if (LocalTime.now().getHour() == 8 && LocalTime.now().getMinute() == 0 && LocalTime.now().getSecond() == 0){
                getNews(12);
            } else if (LocalTime.now().getHour() == 14 && LocalTime.now().getMinute() == 0 && LocalTime.now().getSecond() == 0){
                getNews(6);
            } else if (LocalTime.now().getHour() == 20 && LocalTime.now().getMinute() == 0 && LocalTime.now().getSecond() == 0){
                System.out.println("true");
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
        Calendar newsAfter = new GregorianCalendar();
        newsAfter.add(Calendar.HOUR_OF_DAY,-ammountHours);
        Parser parser = new Parser(newsAfter);
        try {
            bot.mailingForAllSubs(parser.parse(bot.getRssFromList(0)),ammountHours);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


}
