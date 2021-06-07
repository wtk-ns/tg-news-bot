package org.example;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class NewsSender implements Runnable{

    private Calendar calendar = new GregorianCalendar();
    private Calendar tempCal = new GregorianCalendar();
    private Calendar newsFromHours = new GregorianCalendar();
    private Date curDate;

    private Bot bot;

    private final Long chatID;
    private SimpleDateFormat df = new SimpleDateFormat("HH : mm, dd MMMM");



    public NewsSender(Date date, Bot bot, Long chatID){
        this.calendar.setTime(date);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND,0);
        this.bot = bot;
        this.chatID = chatID;
        //curDate = calendar.getTime();

    }

    @Override
    public synchronized void run() {

        while (true) {

            curDate = calendar.getTime();
            newsFromHours.setTimeInMillis(calendar.getTimeInMillis());
            Long timeForSleep = timeCalculator(calendar.getTime());
            botinfo();

            try {
                System.out.println("will sleep " + timeForSleep);


                Thread.sleep(timeForSleep);
                calendar.setTimeInMillis(System.currentTimeMillis());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //System.out.println(calendar.getTime());
        }


        ///calendar.setTime(curDate);


        //Integer inta = timeCalculator(curDate);

        //bot.sendMsg(chatID, inta + " часов спать");

        //Date dt = curDate;
        //dt.setHours(dt.getHours() + inta);

        //bot.sendMsg(chatID, "Пришлет новости в: " + df.format(dt));
        //bot.sendMsg(chatID, "За промежуток с: " + df.format(newsFromHours.getTime()));

    }

    private void botinfo(){
       // bot.sendMsg(chatID, "lastNewsDate: " + df.format(curDate));
      //  bot.sendMsg(chatID, "News from " + df.format(newsFromHours.getTime()));

        bot.sendMsg(chatID, bot.parseNews("https://vc.ru/rss", newsFromHours), true);


    }

    private synchronized Long timeCalculator(Date currentDate){


        tempCal.setTime(currentDate);


        Integer currentHour = tempCal.get(Calendar.HOUR_OF_DAY);

        /*
        Возможно 4 варианта: 1- 00-8, 2- 8-14, 3- 14-20, 4- 20-00
         */


        if ((currentHour>=8) && (currentHour<14)) {

            tempCal.set(Calendar.HOUR_OF_DAY, (14-currentHour));

            //newsFromHours.add(Calendar.DATE, -1);
            newsFromHours.add(Calendar.HOUR_OF_DAY, -(18 - (14-currentHour)));

        } else if (currentHour>=14 && currentHour<20) {

            tempCal.set(Calendar.HOUR_OF_DAY, (20-currentHour));

            newsFromHours.add(Calendar.HOUR_OF_DAY, -(12-(20-currentHour)));

        } else if (currentHour>=20 && currentHour<=24) {

            tempCal.set(Calendar.HOUR_OF_DAY, ((24-currentHour)+8));

            newsFromHours.add(Calendar.HOUR_OF_DAY,-(12-((24-currentHour)+8)));

        } else if (currentHour<8 && currentHour>=0){

            tempCal.set(Calendar.HOUR_OF_DAY, (8-currentHour));

            newsFromHours.add(Calendar.HOUR_OF_DAY, -(12-(8-currentHour)));
        } else {
            bot.sendMsg(chatID, "Something get wrong with date formats in NewsSender ", false);
            System.out.println("here");
        }

        Long sleepingTime = tempCal.get(Calendar.HOUR) * 3600000L;
        bot.forInstantNews.setTimeInMillis(newsFromHours.getTimeInMillis());

        //LONg возвращает время, которое будет спать этот поток, т.е. надо вернуть время до следующего выпуска новостей
        return sleepingTime;

    }


}
