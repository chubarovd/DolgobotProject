package com.redeyesgang.tg;

import com.redeyesgang.DB.Transaction;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by User on 05.03.2018.
 */
public class Dolgobot extends TelegramLongPollingBot {
    private Properties props;
    private List<User> users;

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
        if (in_text.equals ("/start")) { sendConfirmation (in.getChatId (), null); };
    }

    void sendMsg (Long chatId, String text) {
        SendMessage out = new SendMessage (chatId, text);
        try {
            sendMessage (out);
        } catch (TelegramApiException e) {
            e.printStackTrace ();
        }
    }

    void sendConfirmation (Long chatId, Transaction transaction) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(
            new InlineKeyboardButton()
                .setText("Confirm")
                .setCallbackData("1"));
        row.add(
            new InlineKeyboardButton()
                .setText("Reject")
                .setCallbackData("0"));
        rows.add(row);
        keyboard.setKeyboard(rows);

        SendMessage notification =
            new SendMessage (
                chatId,
                "You have new transaction. Please, confirm or reject it.\n/*transaction data*/");
        notification.setReplyMarkup(keyboard);

        try {
            sendMessage (notification);
        } catch (TelegramApiException e) {
            e.printStackTrace ();
        }
    }
}
