package org.example;


import java.time.LocalTime;


public class MailingThread implements Runnable{

    private final Bot bot;


    public MailingThread(Bot bot){
        this.bot=bot;
    }

    @Override
    public void run() {

        while (true){


            if (LocalTime.now(bot.zone).getHour() == 8 && LocalTime.now(bot.zone).getMinute() == 0 && LocalTime.now(bot.zone).getSecond() == 0){
                getNews(bot.getAmmountOfHoursForNewsParsing());
            } else if (LocalTime.now(bot.zone).getHour() == 14 && LocalTime.now(bot.zone).getMinute() == 0 && LocalTime.now(bot.zone).getSecond() == 0){
                getNews(bot.getAmmountOfHoursForNewsParsing());
            } else if (LocalTime.now(bot.zone).getHour() == 20 && LocalTime.now(bot.zone).getMinute() == 0 && LocalTime.now(bot.zone).getSecond() == 0){
                getNews(bot.getAmmountOfHoursForNewsParsing());
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
