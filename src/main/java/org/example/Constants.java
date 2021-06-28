package org.example;
import java.net.URI;
import java.net.URISyntaxException;
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

    private static String user, password;


    public static String getDBurl(){
        try {
            URI dbURi = new URI(systemEnvironment.get("DATABASE_URL"));
            String dbUrl = "jdbc:postgresql://" + dbURi.getHost() + ':' + dbURi.getPort() + dbURi.getPath() + "?sslmode=require";
            user = dbURi.getUserInfo().split(":")[0];
            password = dbURi.getUserInfo().split(":")[1];

            return dbUrl;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }


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
