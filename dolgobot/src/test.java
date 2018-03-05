import com.redeyesgang.DB.Transaction;
import com.redeyesgang.DB.TransactionException;
import com.redeyesgang.tg.Dolgobot;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 * Created by User on 05.03.2018.
 */
public class test {
    public static void main (String[] args) {
        ApiContextInitializer.init ();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi ();
        Dolgobot bot = new Dolgobot ();
        try {
            telegramBotsApi.registerBot (bot);
            System.out.print ("Bot registered\n");
        } catch (TelegramApiException e) {
            e.printStackTrace ();
        }
    }
}
