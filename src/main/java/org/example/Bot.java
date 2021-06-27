package org.example;


import com.rometools.rome.feed.synd.SyndEntry;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;

public class Bot extends TelegramLongPollingBot {

    private Boolean hasSettings = false;

    public Bot(){
        DataBase.getSubListFromBase();
        mailingThreadStart();
    }


    private void mailingThreadStart(){
        MailingThread mailingThread = new MailingThread(this);
        Thread thread = new Thread(mailingThread);
        thread.start();
    }

    @Override
    public String getBotUsername() {
        return Constants.systemEnvironment.get("BOT_NAME");
    }

    @Override
    public String getBotToken() {
        return Constants.systemEnvironment.get("BOT_TOKEN");
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasCallbackQuery())
        {
            handleCallback(update.getCallbackQuery());
        } else if (hasSettings){
            handleSetup(update.getMessage());
        } else {
            handleMessage(update.getMessage());
        }
    }


    private void handleMessage(Message command){

        Subscriber subscriber = DataBase.getSubscriber(command.getChatId());

        switch (command.getText()){
            case "/help":
                helpAction(subscriber);
                break;
            case "/news":
                newsAction(subscriber);
                break;
            case "/settings":
                settingsAction(subscriber);
                break;
            default:
                noncommandAction(subscriber);
                break;
        }
    }


    private void handleSetup(Message settings){
        hasSettings = false;
    }


    private void handleCallback(CallbackQuery callbackQuery){

        Parser parser = Parser.makeParser(Constants.defaultAmountOfHoursForParse);

        switch (callbackQuery.getData()){
            case "VC":
                try {
                    editMessage(callbackQuery.getMessage(), makeTextFormList(parser.parse(Journals.VC.getRssUrl())), true);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                break;
            case "TJ":
                try {
                    editMessage(callbackQuery.getMessage(), makeTextFormList(parser.parse(Journals.TJ.getRssUrl())), true);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                break;
            case "KOD":
                try {
                    editMessage(callbackQuery.getMessage(), makeTextFormList(parser.parse(Journals.KOD.getRssUrl())), true);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                break;
            default:
                System.out.println("Error with callBackQuerry interpretation. handleCallback method");
        }
    }


    private void settingsAction(Subscriber subscriber){

    }


    private void editMessage(Message message, String newText, Boolean hasInlines){

        EditMessageText editedMessage = new EditMessageText();

        editedMessage.setMessageId(message.getMessageId());
        editedMessage.setChatId(message.getChatId());
        editedMessage.setText(newText);
        editedMessage.disableWebPagePreview();
        editedMessage.setParseMode(ParseMode.HTML);

        if (hasInlines){
            editedMessage.setReplyMarkup(makeInlineMarkup());
        }

        if (newText.length()>4096){
            Integer tempLength = newText.length();
            sendMessage(message.getChatId(),newText.substring(0, 4096),false);
            sendMessage(message.getChatId(),newText.substring(4096, tempLength),true);

        } else {

            try {
                execute(editedMessage);
            } catch (TelegramApiException exception) {
                //System.out.println(exception.getMessage());
                //System.out.println("editMessage");
            }
        }

    }



    private void newsAction(Subscriber subscriber){

        Parser parser = Parser.makeParser(Constants.defaultAmountOfHoursForParse);
        try {
            sendMessage(subscriber.getChatID(), makeTextFormList(parser.parse(Journals.VC.getRssUrl())), true);
        } catch (Exception exception) {
            exception.printStackTrace();
            System.out.println("newsAction");
        }

    }



    public void mailingForAllSubs(List<SyndEntry> feedNewsList){


        for (Subscriber sub : DataBase.getSubscribersList()){

            sendMessage(sub.getChatID(), makeTextFormList(feedNewsList), true);
        }
    }


    private String makeTextFormList(List<SyndEntry> list){

        StringBuilder returnedString = new StringBuilder();


        if (list.size()!=0) {

            for (SyndEntry syndEntry : list) {
                returnedString.append(Constants.dateFormat.format(syndEntry.getPublishedDate()) + "\n<b>" + syndEntry.getTitle() + "</b>\n" +
                        "<a href=\"" + syndEntry.getLink() + "\">" + "в источник" + "</a>\n\n");

            }

        } else {

            returnedString.append("К сожалению, за последний настроенный промежуток новостей по этому ресурсу нет :(\n");
        }


        return returnedString.toString();
    }


    /*
    Отправка сообщения в чат

    @param chatID - ID чата, в который отправляется сообщение
    @param messageText - текст сообщения
    @param hasInlines - наличие кнопок в сообщении
     */
    private void sendMessage(Long chatID, String messageText, Boolean hasInlines){

        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(chatID);
        sendMessage.setText(messageText);
        sendMessage.disableWebPagePreview();
        sendMessage.setParseMode(ParseMode.HTML);

        if (hasInlines)
        {
            sendMessage.setReplyMarkup(makeInlineMarkup());
        }

        if (messageText.length()>4096){
            Integer tempLength = messageText.length();
            sendMessage(chatID,messageText.substring(0, 4096),false);
            sendMessage(chatID,messageText.substring(4096, tempLength),true);

        } else {

            try {
                execute(sendMessage);
            } catch (TelegramApiException exception) {
                System.out.println(exception.getMessage());
                System.out.println("sendMessage");
            }
        }
    }

    /*
    создание разметки inline клавиатуры из массива markupButtons
    массив markupButtons содержит наименования всех изданий, откуда парсится RSS
     */

    private InlineKeyboardMarkup makeInlineMarkup(){

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> markupGrid = new ArrayList<>();
        List<InlineKeyboardButton> markupRow = new ArrayList<>();


        for (Journals journals : Journals.values()){
            String buttonText = journals.getName();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(buttonText);
            button.setCallbackData(buttonText);
            markupRow.add(button);
        }

        markupGrid.add(markupRow);
        inlineKeyboardMarkup.setKeyboard(markupGrid);

        return inlineKeyboardMarkup;
    }


    /*
    Действия при вводе команды /help

    @param chatID - ID чата, в котором была написана команда
     */
    private void helpAction(Subscriber subscriber){


        String subList = "Current subs (ID):\n\n";

        for (Subscriber sub : DataBase.getSubscribersList()){
            subList += sub.getChatID() + "\n";
        }
        sendMessage(subscriber.getChatID(), "/news - for instant news\n\n" + subList, false);
    }


    /*
    Действия при вводе любого текста, не соответствующего командам

    @param chatID - ID чата, в котором был введен текст
     */
    private void noncommandAction(Subscriber subscriber){


        sendMessage(subscriber.getChatID(), "Try to use:\n/start - to subscribe\n/news - to get instant news" +
                "\n/help - for help", false);


    }


    public Integer getAmmountOfHoursForNewsParsing(){
        return Constants.defaultAmountOfHoursForParse;
    }



}
