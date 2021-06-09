package org.example;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import java.net.URL;
import java.util.*;

/*
Один метод - parse(), который возвращает массив новостей
Новости берет из RSS, которые позже указанного времени
 */

public class Parser {

    private final Calendar calendar = new GregorianCalendar();


    public Parser(Calendar newsFrom) {
        this.calendar.setTime(newsFrom.getTime());

    }

    public List<SyndEntry> parse(String FEED) throws Exception{
        return this.printRss(createFeed(FEED));
    }

    //все private, что парсит и обрабатывает RSS

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
        return sort(list);
    }


    private void shortLink(SyndEntry entry)
    {
        String temp = entry.getLink();

        if (temp.contains("vc.ru"))
        {
            entry.setLink(temp.substring(0, temp.lastIndexOf("/")+7));
        } else if (temp.contains("journal.tinkoff.ru"))
        {
            entry.setLink(temp.substring(0, temp.lastIndexOf("/")+1));
        }
    }

    private List<SyndEntry> sort(List<SyndEntry> list)
    {

        Comparator<SyndEntry> comparator = (o1, o2) -> {
            if (o1.getPublishedDate().before(o2.getPublishedDate()))
            {
                return -1;
            } else if (o1.getPublishedDate().after(o2.getPublishedDate())) {
                return 1;
            } else return 0;

        };
        list.sort(comparator);
        return  list;
    }


}