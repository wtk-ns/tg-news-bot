package org.example;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Map;
import java.util.Properties;

public final class Constants {

    public static final Map<String, String> systemEnvironment = System.getenv();
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("E, H:mm");
    public static final ZoneId timeZone = ZoneId.of("Europe/Moscow");
    public static final Integer defaultAmountOfHoursForParse = 12;
    public static final Integer minimumParseTime = 1;
    public static final Integer maximumParseTime = 24;
    public static final String dateBaseURL = "jdbc:postgresql://ec2-3-89-0-52.compute-1.amazonaws.com:5432/da8es7ot1po1tm";

    private static final String user = "zoxueogowfudvx";
    private static final String password = "283491e761cc1714870af77a2ad6d52cdcbc2992e34873f028a12bb73b7b0816";



    public static Properties getPropertiesForDB(){
        Properties properties = new Properties();
        properties.put("user",user);
        properties.put("password",password);

        return properties;
    }



}

enum Journals{
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
