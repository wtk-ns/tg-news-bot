package org.example;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import java.net.URL;
import java.util.*;

public class Parser {
    private String FEED;
    private Calendar calendar = new GregorianCalendar();

    public Parser(String FEED, Calendar news) {
        this.FEED = FEED;
        this.calendar.setTimeInMillis(news.getTimeInMillis());
    }


    public List<SyndEntry> parse() throws Exception{
        List<SyndEntry> newsList = this.printRss(createFeed(FEED));
        return newsList;
    }

    private SyndFeed createFeed(String url) throws Exception
    {
        return new SyndFeedInput().build(new XmlReader(new URL(url)));
    }

    private List<SyndEntry> printRss(SyndFeed feed){

        List<SyndEntry> list = new ArrayList<>();
        for (SyndEntry entry : feed.getEntries())
        {
            if (entry.getPublishedDate().after(calendar.getTime())) {
                shortLink(entry);
                list.add(entry);
            }
        }

        sort(list);
        return list;
    }


    private void shortLink(SyndEntry entry)
    {
        if (entry.getLink().contains("vc.ru"))
        {
            String temp = entry.getLink();
            entry.setLink(temp.substring(0, (temp.lastIndexOf("/")+7)));
        }  else if (entry.getLink().contains("journal.tinkoff.ru"))
        {
            String temp = entry.getLink();
            entry.setLink(temp.substring(0, (temp.lastIndexOf("/")+1)));
        }
    }

    private void sort(List<SyndEntry> list)
    {

        Comparator<SyndEntry> comparator = new Comparator<SyndEntry>() {
            @Override
            public int compare(SyndEntry o1, SyndEntry o2) {
                if (o1.getPublishedDate().before(o2.getPublishedDate()))
                {
                    return -1;
                } else if (o1.getPublishedDate().after(o2.getPublishedDate())) {
                    return 1;
                } else return 0;

            }
        };

        list.sort(comparator);

    }
}
