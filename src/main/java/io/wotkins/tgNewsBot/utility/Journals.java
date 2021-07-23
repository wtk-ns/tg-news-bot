package io.wotkins.tgNewsBot.utility;

public enum Journals{
    VC("VC", "https://vc.ru/rss"),
    TJ("TJ", "https://journal.tinkoff.ru/feed/"),
    KOD("KOD","https://kod.ru/rss/");

    private final String name;
    private final String rssUrl;

    Journals(String name, String rssUrl){
        this.name = name;
        this.rssUrl = rssUrl;
    }

    public String getName(){
        return name;
    }

    public String getRssUrl(){
        return rssUrl;
    }

}