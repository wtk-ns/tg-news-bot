package org.example;

import com.rometools.rome.feed.synd.SyndEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParsingThread implements Runnable{

    private final Integer delayTime;
    private final String url;
    private Parser parser;
    private List<SyndEntry> list = new ArrayList<>();

    public ParsingThread(Float delayTimeinHours, String url){
        this.delayTime = (int)(delayTimeinHours * 3.6f * 1000000);
        this.url=url;
    }


    @Override
    public void run() {
        while (true)
        {
            try{
                action();
                Thread.sleep(delayTime);
            } catch (InterruptedException e)
            {
                System.out.println("Thread sleed exception");
            }
        }
    }

    private void checkDuplicates(List<SyndEntry> newlist, List<SyndEntry> bigList)
    {

        for (SyndEntry a : newlist)
        {
            a.setLink(shorterLink(a.getLink()));
        }

        for (SyndEntry a : bigList)
        {
            if (newlist.contains(a)) {
                newlist.remove(a);
            }
        }



        if (newlist.size()!=0)
        {
            for (SyndEntry entry : newlist)
            {
                //

                if (entry.getPublishedDate().after(NewsCollector.currentDate)){
                    bigList.add(entry);
                }
            }
        }

    }

    private String shorterLink(String s)
    {
        String reg = "vc";
        String newLink = "";
        if (s.contains("vc.ru"))
        {
            newLink = s.substring(0, s.lastIndexOf("/")+7);

        } else if (s.contains("journal.tinkoff.ru"))
        {
            newLink = s.substring(0, s.lastIndexOf("/")+1);

        } else {
            newLink = s;
        }

        return newLink;


    }

    public synchronized void action()
    {
        try {
            parser = new Parser(url);
            list = parser.parse();


            if (url.equals(NewsCollector.tjRssUrl))
            {
                checkDuplicates(list, NewsCollector.tjList);

            } else if (url.equals(NewsCollector.vcRssUrl))
            {
                checkDuplicates(list, NewsCollector.vcList);

            } else if (url.equals(NewsCollector.kodRssUrl))
            {
                checkDuplicates(list, NewsCollector.kodList);
            }

            parser = null;
            list = null;

        } catch (Exception e){

        }
    }

}
