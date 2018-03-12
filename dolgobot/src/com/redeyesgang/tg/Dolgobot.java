package com.redeyesgang.tg;

import com.redeyesgang.DB.DBWorker;
import com.redeyesgang.DB.OnCreateException;
import com.redeyesgang.DB.Transaction;
import com.redeyesgang.DB.TransactionException;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMessages_zh_CN;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
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
import java.sql.SQLException;
import java.util.*;

/**
 * Created by User on 05.03.2018.
 */
public class Dolgobot extends TelegramLongPollingBot {
    private DBWorker dbObj;
    private Properties props;
    private Map <Long, User> users;

    public Dolgobot () {
        super ();
        try {
            dbObj = new DBWorker ();
        } catch (Exception e) {
            e.printStackTrace ();
            System.out.println ("ВСЕ ПОШЛО ПО ПИЗДЕ");
            System.exit (0);
        }
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

    /**
     * @param update
     * completed
     */
    private void confirmationProcessing (Update update) {
        String callbackData = update.getCallbackQuery ().getData ();
        int messageId = update.getCallbackQuery ().getMessage ().getMessageId ();
        long chatId = update.getCallbackQuery ().getMessage ().getChatId ();
        char key = callbackData.toCharArray ()[0];
        long transactionId = Long.valueOf (callbackData.substring (1));

        EditMessageText editedMessage =
            new EditMessageText ().setMessageId (messageId).setChatId (chatId);
        if (key == '1') {
            try {
                Transaction transaction = dbObj.validate (transactionId);
                editedMessage.setText (update.getCallbackQuery ().getMessage ().getText () + "\nПодтверждено");
                Execute (
                    new SendMessage (
                        transaction.getFromId (),
                        "Транзакция\n" + transaction.toString () + "\nподтверждена."));
            } catch (SQLException e) {
                editedMessage.setText ("Ошибка в базе данных. Попробуйте позже.");
                e.printStackTrace ();
            } catch (TransactionException e) {
                editedMessage.setText ("Неизвестная ошибка на сервере.");
                //Execute (new DeleteMessage (chatId, messageId));
                return;
            }
        } else if (key == '0') {
            try {
                Transaction transaction = dbObj.cancel (transactionId);
                editedMessage.setText (update.getCallbackQuery ().getMessage ().getText () + "\nОтклонено");
                Execute (
                    new SendMessage (
                        transaction.getFromId (),
                        "Транзакция\n" + transaction.toString () + "\nотклонена."));
            } catch (SQLException e) {
                editedMessage.setText ("Ошибка в базе данных. Попробуйте позже.");
                e.printStackTrace ();
            } catch (TransactionException e) {
                editedMessage.setText ("Неизвестная ошибка. Попробуйте позже.");
                e.printStackTrace ();
            }
        }

        Execute (editedMessage);
    }
    private void textProcessing (Update update) {
        Message in = update.getMessage ();
        String in_text = in.getText ();
        long tgId = in.getFrom ().getId ();
        User temp = users.get (tgId);
        if (temp == null) {
            switch (in_text) {
                case "/start":
                    try {
                        dbObj.getLoginByTelegramID (tgId);
                        dbObj.updateChatID (tgId, in.getChatId ());
                        String dolgobotLogin = dbObj.getLoginByTelegramID (tgId);
                        List <Transaction> transactions = dbObj.getTransactions (tgId);
                        Execute (
                            new SendMessage ()
                                .setChatId (in.getChatId ())
                                .setText ("С возвращением, " + dolgobotLogin + "!\n" +
                                    "У Вас " + transactions.size () + " неподтвержденных транзакций."));
                        for (Transaction tr : transactions) {
                            Execute (
                                new SendMessage ()
                                    .setChatId (in.getChatId ())
                                    .setText ("Новая транзакция\n" + tr.toString ())
                                    .setReplyMarkup (getConfirmationKeyboard (tr.getTransactID ())));
                        }
                    } catch (SQLException e) {
                        Execute (new SendMessage (tgId, "Неизвестная ошибка. Попробуйте позже."));
                    } catch (OnCreateException e) {
                        Execute (
                            new SendMessage ()
                                .setChatId (in.getChatId ())
                                .setText ("Привет! Я - Эдик-долгобот, и я занимаюсь учетом долгов. \n" +
                                    "Чтобы начать знакомство со мной, придумайте логин (не более 30 символов)"));
                        users.put (tgId, new User (User.State.SENDS_LOGIN));
                    } catch (TransactionException e) {
                        e.printStackTrace ();
                    }
                    break;
                case "/addtr":
                    users.put (tgId, new User (User.State.SENDS_DEST_USER).initTransaction (tgId));
                    Execute (
                        new SendMessage ()
                            .setChatId (in.getChatId ())
                            .setText ("Какому пользователю вы хотите отправить сообщение?")
                            /*.setReplyMarkup (getUserListKeyboard (userId))*/);
                    break;
                case "/addgrouptr":
                    users.put (tgId, new User (User.State.SENDS_GROUP_NAME_TR));
                    sendMsg (in.getChatId (), "Введите название группы.");
                    break;
                case "/help":
                    sendMsg (
                        in.getChatId (),
                        "=Возможные команды=\n" +
                            " # Узнать про все, связанные с Вами, задолженности /show" +
                            " # Добавить новую транзакцию /addtr\n" +
                            " # Добавить групповую транзакцию /addgrouptr " +
                            "(в этом случае указанная Вами сумма будет " +
                            "равномерно распределена между всеми участниками группы)\n" +
                            " # Создать новую группу /newgroup\n" +
                            " # Удалить группу /deletegroup\n" +
                            " # Прервать любое действие /break");
                    break;
                default:
                    sendMsg (
                        in.getChatId (),
                        "Я был создан для благородной цели, а не для развлечения.\n" +
                            "Чтобы узнать мои возможности, отправьте /help");
                    break;
            }
        } else {
            if (in_text.equals ("/break")) {
                switch (temp.getState ()) {
                    case SENDS_DEST_USER:
                        sendMsg (in.getChatId (), "Создание транзакции отменено.");
                        break;
                    case SENDS_AMOUNT:
                        sendMsg (in.getChatId (), "Создание транзакции отменено.");
                        break;
                    case SENDS_DESCRIPTION:
                        sendMsg (in.getChatId (), "Создание транзакции отменено.");
                        break;
                    case SENDS_GROUP_NAME:
                        sendMsg (in.getChatId (), "Создание группы отменено.");
                        break;
                    case SENDS_GROUP_MEMBERS:
                        sendMsg (in.getChatId (), "Создание группы отменено.");
                        break;
                    case SENDS_GROUP_NAME_TR:
                        sendMsg (in.getChatId (), "Создание групповой транзакции отменено.");
                        break;
                    case SENDS_AMOUNT_GR_TR:
                        sendMsg (in.getChatId (), "Создание групповой транзакции отменено.");
                        break;
                }
                users.remove (tgId);
            }
            switch (temp.getState ()) {
                case SENDS_LOGIN:
                    if (in_text.length () > 30) {
                        Execute (
                            new SendMessage ()
                                .setChatId (in.getChatId ())
                                .setText ("Слишком много символов! Придумайте новый логин."));
                    } else {
                        try {
                            dbObj.createUser (tgId, in.getChatId (), in.getFrom ().getFirstName (), in_text, in.getFrom ().getLastName ());
                            Execute (
                                new SendMessage ()
                                    .setChatId (in.getChatId ())
                                    .setText ("Отлично! Чтобы узнать, что я могу, отправьте /help"));
                            users.remove (tgId);
                        } catch (OnCreateException e) {
                            Execute (
                                new SendMessage ()
                                    .setChatId (in.getChatId ())
                                    .setText (e.getMessage ()));
                        } catch (SQLException e) {
                            e.printStackTrace ();
                            Execute (
                                new SendMessage ()
                                    .setChatId (in.getChatId ())
                                    .setText ("Упс! Что-то случилось с базой данный. Попробуйте позже."));
                        }
                    }
                    break;
                case SENDS_DEST_USER:
                    try {
                        temp.getTransaction ().setToId (dbObj.getTelegramIDbyLogin (in_text));
                        temp.setState (User.State.SENDS_AMOUNT);
                        Execute (
                            new SendMessage ()
                                .setChatId (in.getChatId ())
                                .setText ("Введите сумму."));
                    } catch (SQLException e) {
                        e.printStackTrace ();
                        Execute (
                            new SendMessage ()
                                .setChatId (in.getChatId ())
                                .setText ("Упс! Что-то случилось с базой данный. Попробуйте позже."));
                    } catch (OnCreateException e) {
                        Execute (
                            new SendMessage ()
                                .setChatId (in.getChatId ())
                                .setText ("Такого пользователя нет. Проверьте правильность введенного имени."));
                        e.printStackTrace ();
                    }
                    break;
                case SENDS_AMOUNT:
                    try {
                        int amount = Integer.valueOf (in_text);
                        if (amount <= 0) {
                            Execute (
                                new SendMessage ()
                                    .setChatId (in.getChatId ())
                                    .setText ("Некорректная сумма. Это должно быть целое положительное число."));
                        } else {
                            temp.setState (User.State.SENDS_DESCRIPTION);
                            temp.getTransaction ().setAmount (amount);
                            Execute (
                                new SendMessage ()
                                    .setChatId (in.getChatId ())
                                    .setText ("Добавьте описание транзакции."));
                        }
                    } catch (NumberFormatException e) {
                        Execute (
                            new SendMessage ()
                                .setChatId (in.getChatId ())
                                .setText ("Некорректная сумма. Это должно быть целое положительное число."));
                    }
                    break;
                case SENDS_DESCRIPTION:
                    temp.getTransaction ().setDescription (in_text);
                    try {
                        long transactionId = dbObj.addTransaction (temp.getTransaction ());
                        Execute (
                            new SendMessage ()
                                .setChatId (tgId)
                                .setText (
                                    "Готово! Вы получите уведомление, когда пользователь подтвердит Вашу транзакцию."));
                        Execute (
                            new SendMessage ()
                                .setChatId (dbObj.getChatIDbyTgUID (temp.getTransaction ().getToId ()))
                                .setText ("Новая транзакция\n" + temp.getTransaction ().toString ())
                                .setReplyMarkup (getConfirmationKeyboard (transactionId)));
                    } catch (SQLException e) {
                        Execute (
                            new SendMessage ()
                                .setChatId (in.getChatId ())
                                .setText ("Ошибка в базе данных. Побробуйте позже."));
                    } catch (OnCreateException e) {
                        e.printStackTrace ();
                        Execute (
                            new SendMessage ()
                                .setChatId (in.getChatId ())
                                .setText ("Неизвестная ошибка. Побробуйте позже."));
                    }
                    users.remove (tgId);
                    break;
                case SENDS_GROUP_NAME:
                    try {
                        dbObj.createGroup (tgId, in_text);
                        sendMsg (
                            in.getChatId (),
                            "Группа создана!\n" +
                            "Теперь отправьте логин пользователя, которому вы хотите выслать приглашение. " +
                            "Когда он подтвердит или отклонит Ваше приглашение, Вы получите уведомление.\n" +
                            "Кого вы хотите пригласить?");
                    } catch (SQLException e) {
                        sendMsg (in.getChatId (), "Внутренняя ошибка сервера. Попробуйте позже.");
                        users.remove (tgId);
                        e.printStackTrace ();
                    } catch (OnCreateException e) {
                        sendMsg (in.getChatId (), "Группа с таким названием уже существует. Придумайте другое.");
                        e.printStackTrace ();
                    }
                    break;
                case SENDS_GROUP_MEMBERS:
                    long newMemberTgId = 0;
                    try {
                        newMemberTgId = dbObj.getTelegramIDbyLogin (in_text);
                    } catch (SQLException e) {
                        e.printStackTrace ();
                    } catch (OnCreateException e) {
                        sendMsg (
                            in.getChatId (),
                            "Такого пользователя не существует. Хотите пригласить кого-то другого?\n" +
                            "Завершить создание /done\nОтменить создание /break");
                    }
                    try {
                        dbObj.addUserToGroup (newMemberTgId, temp.getGroupName ());
                    } catch (SQLException e) {
                        e.printStackTrace ();
                    } catch (OnCreateException e) {
                        e.printStackTrace ();
                    }
                    break;
            }
        }
    }

    private void sendMsg (long chatId, String text) {
        try {
            execute (
                new SendMessage ()
                    .setChatId (chatId)
                    .setText (text));
        } catch (TelegramApiException e) {
            e.printStackTrace ();
        }
    }
    private void Execute (BotApiMethod action) {
        try {
            execute (action);
        } catch (TelegramApiException e) {
            e.printStackTrace ();
        }
    }
    private ReplyKeyboard getConfirmationKeyboard (long transactionId) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(
            new InlineKeyboardButton()
                .setText("Принять")
                .setCallbackData("1" + transactionId));
        row.add(
            new InlineKeyboardButton()
                .setText("Отклонить")
                .setCallbackData("0" + transactionId));
        rows.add(row);
        keyboard.setKeyboard(rows);

        return keyboard;
    }
    private ReplyKeyboard getUserListKeyboard (int userId) {
        return null;
    }
}
