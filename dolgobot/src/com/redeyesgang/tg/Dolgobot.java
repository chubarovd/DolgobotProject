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
                editedMessage.setText (update.getCallbackQuery ().getMessage ().getText () + "\nПОДТВЕРЖДЕНО");
                try {
                    sendMsg (transaction.getFromId (),
                        "Ваша транзакция на сумму " + transaction.getAmount () +
                            " для пользователя " + dbObj.getLoginByTelegramID (transaction.getToId ())
                            + " ПОДТВЕРЖДЕНА.");
                } catch (OnCreateException e) {
                    e.printStackTrace ();
                }
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
                editedMessage.setText (update.getCallbackQuery ().getMessage ().getText () + "\nОТКЛОНЕНО");
                try {
                    sendMsg (transaction.getFromId (),
                        "Ваша транзакция на сумму " + transaction.getAmount () +
                            " для пользователя " + dbObj.getLoginByTelegramID (transaction.getToId ())
                            + " ОТКЛОНЕНА.");
                } catch (OnCreateException e) {
                    e.printStackTrace ();
                }
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
                                    .setText ("Транзакция на сумму \n" + tr.getAmount () +
                                    " от пользователя " + dbObj.getLoginByTelegramID (tr.getFromId ()))
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
                case "/help":
                    sendMsg (
                        in.getChatId (),
                        ":arrow_forward::arrow_forward:ЧТО Я МОГУ:arrow_back::arrow_back:\n" +
                            ":arrow_forward:/addtr добавление новой транзакции.\n" +
                            ":arrow_forward:/addgrouptr добавление групповой транзакции " +
                            "(в этом случае сумма будет равномерно распределена между участниками группы)\n" +
                            ":arrow_forward:/total список ваших долгов " +
                            "(если Вы должны Пете 300, то будет написано \"№. Петя -300\", " +
                            "если же Петя должен Вам, то будет \"№. Петя 300\")\n" +
                            ":arrow_forward:/creategroup создание группы\n" +
                            ":arrow_forward:/adduserstogroup добавление пользователей в группу.\n" +
                            ":arrow_forward:/grouplist список групп, в которых Вы состоите.\n" +
                            ":arrow_forward:/usersfromgroup\n" +
                            ":arrow_forward:/deletegroup удаление группы.\n" +
                            ":arrow_forward:/break прерывание любого действия.\n");
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
                case "/total":
                    break;
                case "/creategroup":
                    users.put (tgId, new User (User.State.SENDS_GROUP_NAME_CREATION));
                    sendMsg (in.getChatId (), "Придумайте название группы (не более 30 символов)");
                    break;
                case "/adduserstogroup":
                    users.put (tgId, new User (User.State.SENDS_GROUP_NAME_USERS_ADDITION));
                    sendMsg (in.getChatId (), "Введите название группы.");
                    break;
                case "/grouplist":

                    break;
                case "/usersfromgroup":
                    users.put (tgId, new User (User.State.SENDS_GROUP_NAME_USERS_LIST));
                    sendMsg (in.getChatId (), "Введите название группы.");
                    break;
                case "/deletegroup":
                    users.put (tgId, new User (User.State.SENDS_GROUP_NAME_DELETE));
                    sendMsg (in.getChatId (), "Введите название группы.");
                    break;
                case "/break":
                    sendMsg (in.getChatId (), "Вам сейчас нечего прервать.");
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
                    case SENDS_GROUP_NAME_CREATION:
                        sendMsg (in.getChatId (), "Создание группы отменено.");
                        break;
                    case SENDS_GROUP_MEMBERS:
                        sendMsg (in.getChatId (), "Создание группы отменено.");
                        try {
                            dbObj.deleteGroup (tgId, temp.getGroupName ());
                        } catch (Exception e) {
                            e.printStackTrace ();
                        }
                        break;
                    case SENDS_GROUP_NAME_TR:
                        sendMsg (in.getChatId (), "Создание групповой транзакции отменено.");
                        break;
                    case SENDS_AMOUNT_GROUP_TR:
                        sendMsg (in.getChatId (), "Создание групповой транзакции отменено.");
                        break;
                    case SENDS_GROUP_NAME_USERS_ADDITION:
                        sendMsg (in.getChatId (), "Готово!");
                        break;
                }
                users.remove (tgId);
                return;
            }
            switch (temp.getState ()) {
                case SENDS_LOGIN:
                    if (in_text.length () > 30) {
                        Execute (
                            new SendMessage ()
                                .setChatId (in.getChatId ())
                                .setText ("Слишком много символов! Придумайте другой логин."));
                    } else {
                        try {
                            dbObj.createUser (tgId, in.getChatId (), in.getFrom ().getFirstName (), in_text, in.getFrom ().getLastName ());
                            sendMsg (in.getChatId (), "Отлично! Чтобы узнать, что я могу, отправьте /help");
                            users.remove (tgId);
                        } catch (OnCreateException e) {
                            sendMsg (in.getChatId (), "Кажется, этот логин уже занят. Придумайте другой.");
                            e.printStackTrace ();
                        } catch (SQLException e) {
                            sendMsg (in.getChatId (), "Упс! Что-то случилось с базой данный. Попробуйте позже.");
                            e.printStackTrace ();
                        }
                    }
                    break;
                case SENDS_DEST_USER:
                    try {
                        temp.getTransaction ().setToId (dbObj.getTelegramIDbyLogin (in_text));
                        temp.setState (User.State.SENDS_AMOUNT);
                        sendMsg (in.getChatId (), "Введите сумму.");
                    } catch (SQLException e) {
                        sendMsg (in.getChatId (), "Упс! Что-то случилось с базой данный. Попробуйте позже.");
                        e.printStackTrace ();
                    } catch (OnCreateException e) {
                        sendMsg (in.getChatId (), "Такого пользователя нет. Проверьте правильность введенного имени.");
                        e.printStackTrace ();
                    }
                    break;
                case SENDS_AMOUNT:
                    try {
                        int amount = Integer.valueOf (in_text);
                        if (amount <= 0) {
                            sendMsg (in.getChatId (), "Некорректная сумма. Это должно быть целое положительное число.");
                        } else {
                            temp.setState (User.State.SENDS_DESCRIPTION);
                            temp.getTransaction ().setAmount (amount);
                            sendMsg (in.getChatId (), "Добавьте описание транзакции.");
                        }
                    } catch (NumberFormatException e) {
                        sendMsg (in.getChatId (), "Некорректная сумма. Это должно быть целое положительное число.");
                        e.printStackTrace ();
                    }
                    break;
                case SENDS_DESCRIPTION:
                    temp.getTransaction ().setDescription (in_text);
                    try {
                        long transactionId = dbObj.addTransaction (temp.getTransaction ());
                        sendMsg (in.getChatId (), "Готово! Вы получите уведомление, когда пользователь подтвердит Вашу транзакцию.");
                        Execute (
                            new SendMessage ()
                                .setChatId (dbObj.getChatIDbyTgUID (temp.getTransaction ().getToId ()))
                                .setText ("Транзакция на сумму " + temp.getTransaction ().getAmount () +
                                " от пользователя " + dbObj.getLoginByTelegramID (temp.getTransaction ().getFromId ()))
                                .setReplyMarkup (getConfirmationKeyboard (transactionId)));
                    } catch (SQLException e) {
                        sendMsg (in.getChatId (), "Ошибка в базе данных. Побробуйте позже.");
                        e.printStackTrace ();
                    } catch (OnCreateException e) {
                        sendMsg (in.getChatId (), "Неизвестная ошибка. Побробуйте позже.");
                        e.printStackTrace ();
                    }
                    users.remove (tgId);
                    break;
                case SENDS_GROUP_NAME_CREATION:
                    try {
                        if (in_text.length () > 30) {
                            sendMsg (in.getChatId (), "Слишком много символов. Придумайте название покороче.");
                        } else {
                            dbObj.createGroup (tgId, in_text);
                            sendMsg (in.getChatId (),
                                "Группа создана!\n" +
                                    "Теперь Вы - администратор группы " + in_text + "." +
                                    "Вы можете приглашать пользователей в группу и удалять ее.\n" +
                                    "Чтобы пригласить пользователей, отправьте команду /adduserstogroup");
                            users.remove (tgId);
                        }
                    } catch (SQLException e) {
                        sendMsg (in.getChatId (), "Внутренняя ошибка сервера. Попробуйте позже.");
                        users.remove (tgId);
                        e.printStackTrace ();
                    } catch (OnCreateException e) {
                        sendMsg (in.getChatId (), "Группа с таким названием уже существует. Придумайте другое.");
                        e.printStackTrace ();
                    }
                    break;
                case SENDS_GROUP_NAME_USERS_ADDITION:
                    try {
                        dbObj.getGroupInfo (in_text);
                        temp.setState (User.State.SENDS_GROUP_MEMBERS);
                        sendMsg (in.getChatId (), "Кого Вы хотите пригласить?");
                    } catch (SQLException e) {
                        e.printStackTrace ();
                    } catch (OnCreateException e) {
                        sendMsg (in.getChatId (), "Такой группы не существует. Попробуйте еще раз.");
                        e.printStackTrace ();
                    }
                    break;
                case SENDS_GROUP_MEMBERS:
                    long newMemberTgId = 0;
                    try {
                        newMemberTgId = dbObj.getTelegramIDbyLogin (in_text);
                    } catch (SQLException e) {
                        sendMsg (in.getChatId (), "Ошибка в базе данных. Побробуйте позже.");
                        users.remove (tgId);
                        e.printStackTrace ();
                    } catch (OnCreateException e) {
                        sendMsg (in.getChatId (),
                            "Такого пользователя не существует. Кого еще хотите пригласить?\n" +
                            "Завершить добавление /break");
                        break;
                    }
                    try {
                        dbObj.addUserToGroup (newMemberTgId, temp.getGroupName ());
                        sendMsg (in.getChatId (),
                            in_text + " получит приглашение в группу. Кого еще хотите пригласить?\n" +
                                "Завершить добавление /break");
                    } catch (SQLException e) {
                        sendMsg (in.getChatId (), "Ошибка в базе данных. Побробуйте позже.");
                        users.remove (tgId);
                        e.printStackTrace ();
                    } catch (OnCreateException e) {
                        e.printStackTrace ();
                    }
                    break;
                case SENDS_GROUP_NAME_TR:
                    try {
                        dbObj.getGroupInfo (in_text);
                        sendMsg (in.getChatId (), "Введите сумму");
                    } catch (SQLException e) {
                        sendMsg (in.getChatId (), "Ошибка в базе данных. Побробуйте позже.");
                        e.printStackTrace ();
                    } catch (OnCreateException e) {
                        sendMsg (in.getChatId (), "Группа с таким названием уже существует. Придумайте другое.");
                        e.printStackTrace ();
                    }
                    break;
                case SENDS_AMOUNT_GROUP_TR:

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
    private ReplyKeyboard getInvitationKeyboard () {
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
