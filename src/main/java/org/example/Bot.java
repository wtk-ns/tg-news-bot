package org.example;


import com.rometools.rome.feed.synd.SyndEntry;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Bot extends TelegramLongPollingBot {

    private final String BOT_NAME;
    private final String BOT_TOKEN;
    private final List<String> markupButtons = Arrays.asList("VC","TJ","KOD");
    private final List<Long> subscribers = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
    private final List<String> rssFeedList = Arrays.asList("https://vc.ru/rss", "https://journal.tinkoff.ru/feed/", "https://kod.ru/rss/");


    public Bot(String BOT_NAME, String BOT_TOKEN){
        this.BOT_NAME=BOT_NAME;
        this.BOT_TOKEN=BOT_TOKEN;
        mailingThreadStart();
    }

    private void mailingThreadStart(){
        MailingThread mailingThread = new MailingThread(this);
        Thread thread = new Thread(mailingThread);

        thread.start();
    }

    @Override
    public String getBotUsername() {
        return this.BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return this.BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasCallbackQuery())
        {
            handleCallback(update.getCallbackQuery());
        } else {
            handleMessage(update.getMessage());
        }

    }


    private void handleMessage(Message command){

        switch (command.getText()){
            case "/start":
                startAction(command.getChatId());
                break;
            case "/help":
                helpAction(command.getChatId());
                break;
            case "/news":
                newsAction(command.getChatId());
                break;
            default:
                noncommandAction(command.getChatId());
                break;
        }

    }

    private void handleCallback(CallbackQuery callbackQuery){

        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.HOUR_OF_DAY,-12);
        Parser parser = new Parser(calendar);

        switch (callbackQuery.getData()){
            case "VC":
                try {
                    editMessage(callbackQuery.getMessage(), makeTextFormList(parser.parse(rssFeedList.get(0))), true);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                break;
            case "TJ":
                try {
                    editMessage(callbackQuery.getMessage(), makeTextFormList(parser.parse(rssFeedList.get(1))), true);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                break;
            case "KOD":
                try {
                    editMessage(callbackQuery.getMessage(), makeTextFormList(parser.parse(rssFeedList.get(2))), true);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                break;
            default:
                System.out.println("Error with callBackQuerry interpretation. handleCallback method");
        }
    }

    private void editMessage(Message message, String newText, Boolean hasInlines){

        EditMessageText editedMessage = new EditMessageText();

        editedMessage.setMessageId(message.getMessageId());
        editedMessage.setChatId(message.getChatId());
        editedMessage.setText(newText);
        editedMessage.disableWebPagePreview();

        if (hasInlines){
            editedMessage.setReplyMarkup(makeInlineMarkup());
        }

        try {
            execute(editedMessage);
        } catch (TelegramApiException exception){
            System.out.println(exception.getMessage());
            System.out.println("editMessage");
        }

    }

    private void startAction(Long chatID){

        if (!subscribers.contains(chatID)) {
            subscribers.add(chatID);
            sendMessage(chatID, "Congrats! You are now subscriber", false);
        } else {
            sendMessage(chatID, "You are already subscribed.\n/news - for instant news", false);
        }

    }

    private void newsAction(Long chatID){
        if (subscribers.contains(chatID)) {
            Calendar calendar = new GregorianCalendar();
            calendar.add(Calendar.HOUR_OF_DAY, -12);
            Parser parser = new Parser(calendar);

            try {
                sendMessage(chatID, makeTextFormList(parser.parse(rssFeedList.get(0))), true);
            } catch (Exception exception) {
                exception.printStackTrace();
                System.out.println("newsAction");
            }
        } else {
            sendMessage(chatID,"Please /start to subscribe",false);
        }
    }

    public void mailingForAllSubs(List<SyndEntry> feedNewsList, Integer ammountOfHours){
        for (Long chatID : subscribers){
            sendMessage(chatID, "Новости за последние " + ammountOfHours +
                    " часов\n" + makeTextFormList(feedNewsList), true);
        }
    }

    private String makeTextFormList(List<SyndEntry> list){

        String text = "";

        for (SyndEntry syndEntry : list){
            text += dateFormat.format(syndEntry.getPublishedDate()) + " " + syndEntry.getTitle() + "\n" + syndEntry.getLink() + "\n\n";

        }

        return text;
    }


    private void sendMessage(Long chatID, String messageText, Boolean hasInlines){

        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(chatID);
        sendMessage.setText(messageText);
        sendMessage.disableWebPagePreview();

        if (hasInlines)
        {
            sendMessage.setReplyMarkup(makeInlineMarkup());
        }

        try {
            execute(sendMessage);
        } catch (TelegramApiException exception){
            System.out.println(exception.getMessage());
            System.out.println("sendMessage");
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

        for (String buttonText : markupButtons){
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(buttonText);
            button.setCallbackData(buttonText);
            markupRow.add(button);
        }

        markupGrid.add(markupRow);
        inlineKeyboardMarkup.setKeyboard(markupGrid);

        return inlineKeyboardMarkup;
    }

    private void helpAction(Long chatID){
        if (subscribers.contains(chatID))
        {

            String subList = "Current subs:\n\n";
            for (Long id : subscribers){
                subList += id + "\n";
            }
            sendMessage(chatID, subList, false);

        } else {
            sendMessage(chatID,"Please /start to subscribe",false);
        }
    }

    private void noncommandAction(Long chatId){

        if (subscribers.contains(chatId)){
            sendMessage(chatId, "Try to use:\n/start - to subscribe\n/news - to get instant news" +
                    "\n/help - for help", false);
        } else {
            sendMessage(chatId, "Please /start to subscribe", false);
        }
    }

    public String getRssFromList(int index){
        return rssFeedList.get(index);
    }




}
