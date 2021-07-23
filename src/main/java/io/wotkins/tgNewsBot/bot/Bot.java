package io.wotkins.tgNewsBot.bot;


import com.rometools.rome.feed.synd.SyndEntry;
import io.wotkins.tgNewsBot.sql.DataBase;
import io.wotkins.tgNewsBot.utility.Parser;
import io.wotkins.tgNewsBot.utility.Constants;
import io.wotkins.tgNewsBot.utility.Journals;
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


import java.util.*;

public class Bot extends TelegramLongPollingBot {

    private Boolean mailListner = false;

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
        } else if (mailListner){
            sendMail(update);
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
                newsAction(subscriber, Constants.defaultAmountOfHoursForParse);
                break;
            case "/mail":
                mailAction(subscriber);
                break;
            default:
                noncommandAction(subscriber, command.getText());
                break;
        }
    }

    private void mailAction(Subscriber subscriber){
        mailListner = true;
    }

    private void sendMail(Update update){
        mailListner = false;
        String msg[] = update.getMessage().getText().split(";");

        if (msg.length == 3 && msg[0].equals("q")){
            sendMessage(new Subscriber(Long.parseLong(msg[1])), msg[2],false);
        }

    }



    private void handleCallback(CallbackQuery callbackQuery){
        switch (callbackQuery.getData()){
            case "VC":
                try {
                    editMessage(callbackQuery.getMessage(), makeTextFormList(Parser.parse(Journals.VC.getRssUrl(),
                            Constants.defaultAmountOfHoursForParse)), true);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                break;
            case "TJ":
                try {
                    editMessage(callbackQuery.getMessage(), makeTextFormList(Parser.parse(Journals.TJ.getRssUrl(),
                            Constants.defaultAmountOfHoursForParse)), true);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                break;
            case "KOD":
                try {
                    editMessage(callbackQuery.getMessage(), makeTextFormList(Parser.parse(Journals.KOD.getRssUrl(),
                            Constants.defaultAmountOfHoursForParse)), true);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                break;
            default:
                System.out.println("Error with callBackQuerry interpretation. handleCallback method " +
                        this.getClass().getName());
        }
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
            sendMessage(DataBase.getSubscriber(message.getChatId()),newText.substring(0, 4096),false);
            sendMessage(DataBase.getSubscriber(message.getChatId()),newText.substring(4096, tempLength),true);

        } else {

            try {
                execute(editedMessage);
            } catch (TelegramApiException exception) {
                System.out.println("Edit message exception " + this.getClass().getName());
            }
        }

    }



    private void newsAction(Subscriber subscriber, Integer hoursForParse){
        try {

            sendMessage(subscriber, makeTextFormList(Parser.parse(Journals.VC.getRssUrl(),
                    hoursForParse)), true);
        } catch (Exception exception) {
            exception.printStackTrace();
            System.out.println("newsAction");
        }

    }



    public void mailingForAllSubs(List<SyndEntry> feedNewsList){

        for (Subscriber sub : DataBase.getSubscribersList()){
            sendMessage(sub, makeTextFormList(feedNewsList), true);
        }
    }


    private String makeTextFormList(List<SyndEntry> list){

        StringBuilder returnedString = new StringBuilder();
        if (list.size()!=0) {

            for (SyndEntry syndEntry : list) {
                returnedString.append(Constants.dateFormat.format(syndEntry.getPublishedDate()) + "\n<b>" +
                        syndEntry.getTitle() + "</b>\n" + "<a href=\"" + syndEntry.getLink() + "\">" + "в источник" + "</a>\n\n");
            }
        } else {
            returnedString.append("К сожалению, за последний настроенный промежуток новостей по этому ресурсу нет :(\n");
        }


        return returnedString.toString();
    }



    private void sendMessage(Subscriber subscriber, String messageText, Boolean hasInlines){

        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(subscriber.getChatID());
        sendMessage.setText(messageText);
        sendMessage.disableWebPagePreview();
        sendMessage.setParseMode(ParseMode.HTML);

        if (hasInlines)
        {
            sendMessage.setReplyMarkup(makeInlineMarkup());
        }

        if (messageText.length()>4096){
            int tempLength = messageText.length();
            sendMessage(subscriber,messageText.substring(0, 4096),false);
            sendMessage(subscriber,messageText.substring(4096, tempLength),true);

        } else {

            try {
                execute(sendMessage);
            } catch (TelegramApiException exception) {

                if (exception.toString().contains("chat not found")){
                    if (DataBase.hasInSubscribers(subscriber)){
                        DataBase.deleteSubscriber(subscriber);
                    }
                }

            }
        }
    }


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


    private void helpAction(Subscriber subscriber){

        String subList = "Current subs (ID):\n\n";

        for (Subscriber sub : DataBase.getSubscribersList()){
            subList += sub.getChatID() + "\n";
        }
        sendMessage(subscriber, "/news - for instant news\n\n" + subList, false);

    }



    private void noncommandAction(Subscriber subscriber, String text){


        if (text.contains("/news")){
            String[] temp = text.split(" ");
            if (temp.length == 2){
                newsAction(subscriber, Integer.parseInt(temp[1]));
            }
        } else {

            sendMessage(subscriber, "Try to use:\n/news - to get instant news" +
                    "\n/help - for help", false);
        }

    }




}
