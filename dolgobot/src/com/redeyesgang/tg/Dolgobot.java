package com.redeyesgang.tg;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Contact;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

//a ()

/**
 * Created by User on 05.03.2018.
 */
public class Dolgobot extends TelegramLongPollingBot {
    private Properties props;

    public Dolgobot () {
        super ();
        props = new Properties ();
        try {
            props.load (
                new FileInputStream (
                    "C:\\Users\\User\\IdeaProjects\\dolgobot\\src\\com\\redeyesgang\\tg\\props"));
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }

    @Override
    public String getBotUsername () {
        return props.getProperty ("bot_name");
    }

    @Override
    public String getBotToken () {
        return props.getProperty ("token");
    }

    @Override
    public void onUpdateReceived (Update update) {
        Message in = update.getMessage ();
        String in_text = in.getText ();
        if (in_text.equals ("/start")) { sendMsg (in.getChatId (), ""); };
    }

    void sendMsg (Long chatId, String text) {
        SendMessage out = new SendMessage (chatId, text);
        try {
            sendMessage (out);
        } catch (TelegramApiException e) {
            e.printStackTrace ();
        }
    }
}
