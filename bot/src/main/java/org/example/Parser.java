package org.example;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private String FEED;
    private List<SyndEntry> newsList = new ArrayList<>();

    public Parser(String FEED) {
        this.FEED = FEED;
    }


    public List<SyndEntry> parse() throws Exception{
        newsList = this.printRss(createFeed(FEED));
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
            list.add(entry);
        }
        return list;
    }
}
