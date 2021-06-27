package org.example;

public class Subscriber {

    private final Long chatID;
    private Integer parseGap;

    public Subscriber(Long chatID, Integer parseGap){
        this.chatID = chatID;
        this.parseGap = parseGap;
    }

    public Subscriber(Long chatID){
        this.chatID = chatID;
        this.parseGap = Constants.defaultAmountOfHoursForParse;
    }

    public Long getChatID(){
        return chatID;
    }

    public Integer getParseGap(){
        return parseGap;
    }

    public void setParseGap(Integer parseGap){
        if (parseGap>=Constants.minimumParseTime && parseGap<=Constants.maximumParseTime){
            this.parseGap = parseGap;
            DataBase.insertNewSettings(parseGap,this);
        } else {
            System.out.println("Incorrect parse time format " + this.getClass().getName());
            this.parseGap = Constants.defaultAmountOfHoursForParse;
        }
    }

}
