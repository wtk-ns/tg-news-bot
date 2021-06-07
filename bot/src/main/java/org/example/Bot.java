package org.example;


import com.rometools.rome.feed.synd.SyndEntry;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.swing.text.html.HTML;
import java.lang.ref.SoftReference;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public class Bot extends TelegramLongPollingBot {

    private String NAME, TOKEN;
    private Long chatID;
    private SimpleDateFormat df = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat df2 = new SimpleDateFormat("HH:mm, dd MMMM");
    private List<SyndEntry> listVC = new ArrayList<>();
    private List<String> listOfResources = new ArrayList<>();



    public Calendar forInstantNews = new GregorianCalendar();


    protected Bot(String NAME, String TOKEN) {
        this.NAME = NAME;
        this.TOKEN = TOKEN;
        listOfResources.add("VC");
        listOfResources.add("TJ");
        listOfResources.add("KOD");
    }

    @Override
    public String getBotUsername() {
        return NAME;
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        //тут 4 варика - старт, хелп, нювс и нонкоманд
        //если чел нажал старт - вывести сообщение, во сколько новости появляются и за какой период времени
        //вывести инстант ньювс
        if (!update.hasCallbackQuery()) {
            commandHandler(update);
        } else {
            callBackHandler(update.getCallbackQuery());

        }


    }

    private void callBackHandler(CallbackQuery callbackQuery)
    {

        EditMessageText em = new EditMessageText();
        em.setChatId(callbackQuery.getMessage().getChatId());
        em.setMessageId(callbackQuery.getMessage().getMessageId());
        em.disableWebPagePreview();
        em.setReplyMarkup(makemurkup());



        if (callbackQuery.getData().equals("TJ"))
        {
            em.setText(parseNews("https://journal.tinkoff.ru/feed/", forInstantNews));

        } else if (callbackQuery.getData().equals("KOD")) {
            em.setText(parseNews("https://kod.ru/rss/", forInstantNews));
        } else
        {
            em.setText(parseNews("https://vc.ru/rss", forInstantNews));
        }

        try {
            execute(em);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    private void commandHandler (Update upd){
        Long ID = upd.getMessage().getChatId();
        String command = upd.getMessage().getText();


        if (command.equals("/start")) {
            if (chatID == null) {
                Date date = new Date();

                NewsSender newsSender = new NewsSender(date, this, ID);
                Thread newsThread = new Thread(newsSender);
                chatID = ID;
                newsThread.start();
            } else {
                sendMsg(chatID, "You are already subscribed.\nIf u have prblms - reload bot by recreating chat", false);
            }
        } else if (command.equals("/news")) {
            if (chatID != null) {
                sendMsg(chatID, parseNews("https://vc.ru/rss",forInstantNews), true);

            } else {

            }

        }

    }

    public String parseNews(String FEED, Calendar newsFromPoint)
    {
        Parser parser = new Parser(FEED, newsFromPoint);

        try {
            listVC = parser.parse();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String text = "Новости с " + df2.format(newsFromPoint.getTime()) + "\n\n";
        for (SyndEntry a: listVC)
        {
            text += df.format(a.getPublishedDate()) + " " + a.getTitle() + "\n" + a.getLink() + "\n\n";
        }
        return text;
    }



    /*
     при вводе команды старт - определить сколько времени
     обнулить секунды, минуты и взять день и месяц

     определить в каком временном промежутке находимся: 00-8, 8 - 14, 14 - 20, 20 - 00,
                                                           0    1       2       3
     всего временных промежутков 4
     принять за  точку отсчета ближайшее большее время
     отправить поток на сон до нее


     найти в каком временном промежутке мы находимся
     */

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }

    public void sendMsg(Long chatID, String text, boolean haveInline)
    {


        SendMessage sm = new SendMessage();
        sm.setChatId(chatID);
        sm.setText(text);
        sm.disableWebPagePreview();

        if (haveInline)
        {
            sm.setReplyMarkup(makemurkup());
        }

        try {
            execute(sm);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup makemurkup(){
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton VCbutton = new InlineKeyboardButton();
        VCbutton.setText("VC");
        VCbutton.setCallbackData("VC");
        row.add(VCbutton);

        InlineKeyboardButton TJbutton = new InlineKeyboardButton();
        TJbutton.setText("TJ");
        TJbutton.setCallbackData("TJ");
        row.add(TJbutton);

        InlineKeyboardButton KODbutton = new InlineKeyboardButton();
        KODbutton.setText("KOD");
        KODbutton.setCallbackData("KOD");
        row.add(KODbutton);

        List<List<InlineKeyboardButton>> forMarkup = new ArrayList<>();
        forMarkup.add(row);
        markup.setKeyboard(forMarkup);

        return markup;

    }








}
