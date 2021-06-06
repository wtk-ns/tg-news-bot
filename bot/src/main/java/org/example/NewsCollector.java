package org.example;

import com.rometools.rome.feed.synd.SyndEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewsCollector {
    public static String tjRssUrl = "https://journal.tinkoff.ru/feed/";
    public static String vcRssUrl = "https://vc.ru/rss";
    public static String kodRssUrl = "https://kod.ru/rss/";
    private static Float parseTime = 0.005f;

    public static Date currentDate;
    public static String botStart;
    private static SimpleDateFormat df = new SimpleDateFormat("HH:mm");



    public static List<SyndEntry> tjList = new ArrayList<>();
    public static List<SyndEntry> vcList = new ArrayList<>();
    public static List<SyndEntry> kodList = new ArrayList<>();

    private static Bot bot;

    public void collectNews(Float delayTimeinHours, Bot botName) throws Exception
    {
        this.bot=botName;
        botStart = df.format(System.currentTimeMillis());





        NewsCollect collector = new NewsCollect(delayTimeinHours);
        Thread mainThread = new Thread(collector);
        mainThread.start();


        ParsingThread timerTJ = new ParsingThread(parseTime, tjRssUrl);
        Thread tjThread = new Thread(timerTJ);
        tjThread.start();

        ParsingThread timerVC = new ParsingThread(parseTime, vcRssUrl);
        Thread vcThread = new Thread(timerVC);
        vcThread.start();

        ParsingThread timerKod = new ParsingThread(parseTime, kodRssUrl);
        Thread kodThread = new Thread(timerKod);
        kodThread.start();



    }

    private static class NewsCollect implements Runnable
    {

        private Integer time;
        public NewsCollect(Float time) {
            this.time = (int)(time * 3.6f * 1000000);
        }

        @Override
        public void run() {

            while (true)
            {

                try {

                    action();
                    Thread.sleep(time);


                } catch (Exception e) {

                }
            }

        }

        public static void action()
        {
            Date today = new Date();
            if (today.getHours() < 13) {
                currentDate = today;
                currentDate.setDate(today.getDay()-2);
                currentDate.setHours(20);
                currentDate.setMinutes(0);
                System.out.println(currentDate);
            } else {
                currentDate = today;
                currentDate.setHours(8);
                currentDate.setMinutes(0);
            }
            currentDate.setTime(System.currentTimeMillis());
            currentDate.setHours(currentDate.getHours()-12);



            bot.currentKODnews = kodList;
            bot.currentTJnews = tjList;
            bot.currentVCnews = vcList;


            System.out.println(df.format(System.currentTimeMillis()));

            String vcText = df.format(System.currentTimeMillis()) + "\n\n";
            for (SyndEntry a : NewsCollector.vcList)
            {
                vcText += df.format(a.getPublishedDate()) + " " + a.getTitle() + "\n" + a.getLink() + "\n\n";
            }
            if (bot.chatID != null) {
                bot.sendMsg(bot.chatID,vcText, true);
            }

        }


    }

}
