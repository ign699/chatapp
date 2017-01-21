import Bot.Bot;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

/**
 * Created by Jan on 10.01.2017.
 */
@WebSocket
public class ChatWebSocketHandler {

    private String sender, msg;
    private Bot bot = new Bot();

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception{

    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason){
        /*String username = Chat.userUsernameMap.get(user);
        Chat.userUsernameMap.remove(user);
        Chat.broadcastMessage(sender = "server", msg = (username + " left the chat"));*/
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String incomingData){
        Gson gson = new Gson();
        System.out.println(incomingData);
        Message message = gson.fromJson(incomingData, Message.class);
        MessageHandler messageHandler = new MessageHandler(message, user);
        messageHandler.handleMessage();
        //Chat.broadcastMessage(sender = Chat.userUsernameMap.get(user), msg = message);

    }

}
