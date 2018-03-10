package com.redeyesgang.tg;

import com.redeyesgang.DB.Transaction;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by User on 05.03.2018.
 */
public class Dolgobot extends TelegramLongPollingBot {
    private Properties props;
    private Map <Integer, User> users;

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
        users = new HashMap<> ();
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
        if (update.hasMessage () && update.getMessage ().hasText ()) {
            textProcessing (update);
        } else if (update.hasCallbackQuery ()) {
            confirmationProcessing (update);
        }
    }

    private void confirmationProcessing (Update update) {
        String callbackData = update.getCallbackQuery ().getData ();
        int messageId = update.getCallbackQuery ().getMessage ().getMessageId ();
        long chatId = update.getCallbackQuery ().getMessage ().getChatId ();

        EditMessageText editedMessage =
            new EditMessageText ().setMessageId (messageId).setChatId (chatId);
        if (callbackData.equals ("1")) {
            editedMessage.setText (update.getCallbackQuery ().getMessage ().getText () + "\nПодтверждено");
        } else if (callbackData.equals ("0")) {
            editedMessage.setText (update.getCallbackQuery ().getMessage ().getText () + "\nОтклонено");
        }

        Execute (editedMessage);
    }
    private void textProcessing (Update update) {
        Message in = update.getMessage ();
        String in_text = in.getText ();
        int userId = in.getFrom ().getId ();
        User temp = users.get (userId);
        if (temp == null) {
            switch (in_text) {
                case "/start":
                    Execute (
                        new SendMessage ()
                            .setChatId (in.getChatId ())
                            .setText ("Привет! Я - Эдик-долгобот, и я занимаюсь учетом долгов. \n" +
                                "Чтобы начать знакомство со мной, придумайте логин."));
                    users.put (userId, new User ().setState (User.State.SENDS_LOGIN));
                    break;
                case "/addtr":
                    User newUser = new User ().setState (User.State.SENDS_DEST_USER);
                    newUser.getTransaction ().setFromId (userId);
                    users.put (userId, newUser);
                    Execute (
                        new SendMessage ()
                            .setChatId (in.getChatId ())
                            .setText ("Какому пользователю вы хотите отправить сообщение?")
                            /*.setReplyMarkup (getUserListKeyboard (userId))*/);
                    break;
                case "/addgrouptr":
                    break;
                case "/help":
                    Execute (
                        new SendMessage ()
                            .setChatId (in.getChatId ())
                            .setText ("Возможные команды:\n" +
                                " Узнать про все, связанные с Вами, задолженности /show" +
                                " Добавить новую транзакцию /addtr\n" +
                                " Добавить групповую транзакцию /addgrouptr " +
                                "(в этом случае указанная Вами сумма будет " +
                                "равномерно распределена между всеми участниками группы)\n" +
                                " Создать новую группу /newgroup\n" +
                                " Удалить группу /deletegroup"));
                    break;
            }
        } else {
            switch (temp.getState ()) {
                case SENDS_LOGIN:
                    if (in_text.length () > 30) {
                        Execute (
                            new SendMessage ()
                                .setChatId (in.getChatId ())
                                .setText ("Слишком много символов. Придумайте новый логин."));
                    } else {
                        /*try {
                            //new user to db
                            Execute (
                                new SendMessage ()
                                    .setChatId (in.getChatId ())
                                    .setText ("Отлично! Чтобы узнать, что я могу отправьте /help"));
                            users.remove (userId);
                        } catch (OnCreateException e) {
                            Execute (
                                new SendMessage ()
                                    .setChatId (in.getChatId ())
                                    .setText (e.getMessage ()));
                        }*/
                    }
                    break;
                case SENDS_DEST_USER:
                    break;
                case SENDS_AMOUNT:
                    int amount = Integer.valueOf (in_text);
                    if (amount <= 0) {
                        Execute (
                            new SendMessage ()
                                .setChatId (in.getChatId ())
                                .setText ("Некорректная сумма."));
                    } else {
                        temp.getTransaction ().setAmount (amount);
                        //send trnsction to dtbse
                    }
                    break;
            }
        }
    }

    private void Execute (BotApiMethod action) {
        try {
            execute (action);
        } catch (TelegramApiException e) {
            e.printStackTrace ();
        }
    }
    private ReplyKeyboard getConfirmationKeyboard (Transaction transaction) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(
            new InlineKeyboardButton()
                .setText("Принять")
                .setCallbackData("1"));
        row.add(
            new InlineKeyboardButton()
                .setText("Отклонить")
                .setCallbackData("0"));
        rows.add(row);
        keyboard.setKeyboard(rows);

        return keyboard;
    }
    private ReplyKeyboard getUserListKeyboard (int userId) {
        return null;
    }
}
