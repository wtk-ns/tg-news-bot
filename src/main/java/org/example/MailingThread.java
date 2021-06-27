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


            if (LocalTime.now(Constants.timeZone).getHour() == 8 && LocalTime.now(Constants.timeZone).getMinute() == 0 && LocalTime.now(Constants.timeZone).getSecond() == 0){
                getNews(Constants.defaultAmountOfHoursForParse);
            } else if (LocalTime.now(Constants.timeZone).getHour() == 14 && LocalTime.now(Constants.timeZone).getMinute() == 0 && LocalTime.now(Constants.timeZone).getSecond() == 0){
                getNews(Constants.defaultAmountOfHoursForParse);
            } else if (LocalTime.now(Constants.timeZone).getHour() == 20 && LocalTime.now(Constants.timeZone).getMinute() == 0 && LocalTime.now(Constants.timeZone).getSecond() == 0){
                getNews(Constants.defaultAmountOfHoursForParse);
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

        Parser parser = Parser.makeParser(-ammountHours);

        try {
            bot.mailingForAllSubs(parser.parse(Journals.VC.getRssUrl()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


}
