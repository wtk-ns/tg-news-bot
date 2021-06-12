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

    private final String BOT_NAME;
    private final String BOT_TOKEN;
    private final List<String> markupButtons = Arrays.asList("VC","TJ","KOD");
    private final List<Long> subscribers = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("E, H:mm");
    private final List<String> rssFeedList = Arrays.asList("https://vc.ru/rss", "https://journal.tinkoff.ru/feed/", "https://kod.ru/rss/");
    private Boolean settingsLisner = false;
    private Integer amountOfHoursForNewsParsing = 12;

    public final ZoneId zone;




    public Bot(String BOT_NAME, String BOT_TOKEN){
        this.BOT_NAME=BOT_NAME;
        this.BOT_TOKEN=BOT_TOKEN;
        zone = ZoneId.of("Europe/Moscow");
        mailingThreadStart();

    }

    /*
    Старт потока таймера, который будет рассылать новости всем подписанным (внесенным в масиив сабов)
     */

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
        } else if (!settingsLisner){
            handleMessage(update.getMessage());
        } else {
            setupApp(update.getMessage());
            settingsLisner = false;
        }

    }


    /*
    Обработчик комманд
     */
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
            case "/settings":
                settingsAction(command.getChatId());
                break;
            default:
                noncommandAction(command.getChatId());
                break;
        }
    }

    private void setupApp(Message settings){


        Integer temp = 0;
        try {
            temp = Integer.parseInt(settings.getText());
            if (temp>=1 && temp<=24){
                amountOfHoursForNewsParsing = temp;
                sendMessage(settings.getChatId(), "Settings applied successfully",false);
            } else {
                sendMessage(settings.getChatId(), "Incorrect interval\n/settings again", false);
            }
        } catch (Exception e){
            sendMessage(settings.getChatId(), "Incorrect format\n/settings again", false);
        }


    }

    /*
    Обработчик нажатий Inline кнопок
     */

    private void handleCallback(CallbackQuery callbackQuery){

        Parser parser = makeParser(-amountOfHoursForNewsParsing);

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
    /*
    Действие при вводе комманды /settings. Если человек подписан, то включается settingsListner в методе onUpdateRecieved
    Следующим сообщением вводятся настройки (количество часов, за которые парсим новости)
     */

    private void settingsAction(Long chatID){
        if (!subscribers.contains(chatID)){
            sendMessage(chatID, "You need /start before getting access to settings", false);
        } else {
            settingsLisner = true;
            sendMessage(chatID, "Ok, enter the number of hours for which the news will be displayed (1-24)\nDefault value: 12", false);
        }

    }

    /*
    Редактор существующего сообщения
    @param message - существующее сообщение
    @param newText - новый текст сообщения

    @args hasInLines - наличие в отредактированном сообщении инлайн кнопок (пока что всегда true)
     */

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

    /*
    Действие для команды /start

    @param chatID - ID чата, из которого пришло сообщение
    @param msgDate - дата отправки сообщения с командой /start (UNIX)
     */

    private void startAction(Long chatID){

        if (!subscribers.contains(chatID)) {
            subscribers.add(chatID);
            sendMessage(chatID, "Congrats! You are now subscriber", false);
        } else {
            sendMessage(chatID, "You are already subscribed.\n/news - for instant news", false);
        }

    }

    /*
    Действия для команды /news

    @param chatID - ID чата, из которого была отправлена команда /news
     */
    private void newsAction(Long chatID){
        if (subscribers.contains(chatID)) {
            Parser parser = makeParser(-amountOfHoursForNewsParsing);
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


    /*
    Создает объект класса парсер, в который передается календарь, содержащий дату и время, С КОТОРОГО
    будут парсится новости (т.е. парсятся все новости, которые позже calendar)

    @param amountOfHoursBefore - количество часов, за которые парсятся новости
     */
    public Parser makeParser(int amountOfHoursBefore){
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeZone(TimeZone.getTimeZone(zone));
        calendar.add(Calendar.HOUR_OF_DAY, amountOfHoursBefore);
        Parser parser = new Parser(calendar);

        return parser;
    }


    /*
    Рассылка новостей по всем чатам, ID которых находится в массиве subscribers (все, кто ввел комманду /start
    со времени запуска бота)

    @param feedNewsList - массив сущностей RSS ленты
     */

    public void mailingForAllSubs(List<SyndEntry> feedNewsList){
        for (Long chatID : subscribers){
            sendMessage(chatID, makeTextFormList(feedNewsList), true);
        }
    }

    /*
    Превращение массива RSS сущностей в текстовое сообщение необходимого формата

    @param list - массив сущностей RSS ленты
     */

    private String makeTextFormList(List<SyndEntry> list){

        StringBuilder returnedString = new StringBuilder();


        if (list.size()!=0) {

            for (SyndEntry syndEntry : list) {
                returnedString.append(dateFormat.format(syndEntry.getPublishedDate()) + "\n<b>" + syndEntry.getTitle() + "</b>\n" +
                        "<a href=\"" + syndEntry.getLink() + "\">" + "в источник" + "</a>\n\n");

            }

        } else {

            returnedString.append("К сожалению, за последние " + getAmmountOfHoursForNewsParsing() + " часов новостей по этому ресурсу нет :(\n" +
                    "Но в /settings можно изменить интервал парса на бОльший");
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


    /*
    Действия при вводе команды /help

    @param chatID - ID чата, в котором была написана команда
     */
    private void helpAction(Long chatID){
        if (subscribers.contains(chatID))
        {

            String subList = "Current subs (ID):\n\n";
            for (Long id : subscribers){
                subList += id + "\n";
            }
            sendMessage(chatID, "/news - for instant news\n/settings - for settings\n\n" + subList, false);

        } else {
            sendMessage(chatID,"Please /start to subscribe",false);
        }
    }


    /*
    Действия при вводе любого текста, не соответствующего командам

    @param chatID - ID чата, в котором был введен текст
     */
    private void noncommandAction(Long chatId){

        if (subscribers.contains(chatId)){
            sendMessage(chatId, "Try to use:\n/start - to subscribe\n/news - to get instant news" +
                    "\n/help - for help\n/settings - for settings", false);
        } else {
            sendMessage(chatId, "Please /start to subscribe", false);
        }
    }



    public String getRssFromList(int index){
        return rssFeedList.get(index);
    }

    public Integer getAmmountOfHoursForNewsParsing(){
        return amountOfHoursForNewsParsing;
    }



}
