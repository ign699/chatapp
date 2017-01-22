import com.google.gson.Gson;

/**
 * Created by Jan on 22.01.2017.
 */
public final class MessageGenerator {

    public static Message generateMessage(String data){
        Gson gson = new Gson();
        return gson.fromJson(data, Message.class);
    }
}
