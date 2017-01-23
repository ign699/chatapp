import Bot.Bot;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONObject;

/**
 * Created by Jan on 10.01.2017.
 */
@WebSocket
public class ChatWebSocketHandler {

    private MessageHandler messageHandler = new MessageHandler();

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception{

    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason){
        Message message = new Message();
        message.setText("");
        message.setType("onclose");
        messageHandler.actOnMessage(message, user);
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String incomingData){
        System.out.println(incomingData);
        Message message = MessageGenerator.generateMessage(incomingData);
        messageHandler.actOnMessage(message, user);
    }

}
