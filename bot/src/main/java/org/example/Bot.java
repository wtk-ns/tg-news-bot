package org.example;


import com.rometools.rome.feed.synd.SyndEntry;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public class Bot extends TelegramLongPollingBot {

    private Logger log = Logger.getLogger(Bot.class.getName());

    private String name = "newswtcnsbot";
    private String token = "1816599655:AAHJqlQkuPBypLC-3tn_jVdfKQT9cSR7esg";
    private List<String> listOfResources = new ArrayList<>();
    private static SimpleDateFormat df = new SimpleDateFormat("HH:mm");

    public Long chatID;
    public List<SyndEntry> currentVCnews;
    public List<SyndEntry> currentTJnews;
    public List<SyndEntry> currentKODnews;




    public Bot(){
        listOfResources.add("VC");
        listOfResources.add("TJ");
        listOfResources.add("KOD");
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasCallbackQuery()){
            callBackHandler(update.getCallbackQuery());
        } else {
            commandHandler(msgHandler(update.getMessage().getText()), update.getMessage().getChatId());
            chatID = update.getMessage().getChatId();

        }
    }

    private void callBackHandler(CallbackQuery callBack)
    {
        String data = callBack.getData();
        EditMessageText editMessageText = new EditMessageText();

        editMessageText.setChatId(callBack.getMessage().getChatId());
        editMessageText.setMessageId(callBack.getMessage().getMessageId());
        editMessageText.setText(msgSwitcher(listOfResources.indexOf(data), callBack.getMessage().getChatId()));
        editMessageText.setReplyMarkup(makeMarkup());

        try {
            execute(editMessageText);
        } catch (Exception e)
        {
            System.out.println("Calback" + e);
        }



    }

    private String msgSwitcher(Integer index, Long chatId)
    {
        String msg = "";
        switch (index)
        {

            case 0:
                msg = formBlock(currentVCnews);
                break;
            case 1:
                msg = formBlock(currentTJnews);;
                break;
            case 2:
                msg = formBlock(currentKODnews);
                break;
            default:
                sendMsg(chatId,"Sorry, something get wrong with inline. Please , contact @wotkins", false);
                break;

        }

        return msg;
    }

    private String formBlock(List<SyndEntry> list)
    {
        String text = "";
        for (SyndEntry a: list)
        {
            text += df.format(a.getPublishedDate()) + " " + a.getTitle() + "\n" + a.getLink() + "\n\n";
        }
        return text;
    }

    // ""
    // "help"
    // "hey"
    // "news"
    public String msgHandler(String msg){
        if (msg.equals("/help"))
        {
            return "help";
        } else if (msg.equals("/news"))
        {
            return "news";
        } else if (msg.equals("/start"))
        {
            return "start";
        }
        return "";
    }

    public void commandHandler(String command, Long chatId)
    {
        if (command.equals("help")){

            sendMsg(chatId, "Two standard news blocks. Standard news block according to the bot start time & bot start time + 12. " + "\n" +
                    "Instant news - news between standard news period." + "\n\n" + "Bot start time: " + NewsCollector.botStart , false);

        } else if (command.equals("news")) {
            try {
                //Thread.sleep(100);
                sendMsg(chatID, formBlock(currentVCnews),true);
            } catch (Exception e)
            {            }

        } else if (command.equals("start")){
            try {
                sendMsg(chatID, "At first time you need to wait about a minute",false);
                Thread.sleep(6000);
                sendMsg(chatID, formBlock(NewsCollector.vcList),true);
            } catch (Exception e)
            {            }

        }else {
            sendMsg(chatId,"/help, /news", false);
        }
    }




    public void sendMsg(Long chatID, String text, Boolean hasKeyboard) {

        SendMessage msg = new SendMessage();
        msg.setText(text);
        msg.setChatId(chatID);

        if (hasKeyboard) {
            msg.setReplyMarkup(makeMarkup());
        }

        try {
            execute(msg);
        } catch (Exception e) {
            System.out.println(e);

        }
    }


    private InlineKeyboardMarkup makeMarkup()
    {


        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> markupParam = new ArrayList<>();
        List<InlineKeyboardButton> markupRow = new ArrayList<>();

        for (String a : listOfResources) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(a);
            button.setCallbackData(a);
            markupRow.add(button);
        }

        markupParam.add(markupRow);
        markup.setKeyboard(markupParam);


        return markup;
    }


    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;

    }






}
