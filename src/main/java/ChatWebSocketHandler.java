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

    private String sender, msg;
    private Bot bot = new Bot();

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception{

    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason){
        String name = Chat.getNickName(user);
        String room = Chat.getRoom(user);
        Chat.userUsernameMap.remove(user);
        Chat.userRoomMap.remove(user);
        Chat.broadcastMessage("Server", name + " left the chat", room);
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String incomingData){
        System.out.println(incomingData);

        Message message = MessageGenerator.generateMessage(incomingData);

        MessageHandler messageHandler = new MessageHandler(message, user);

        messageHandler.actOnMessage();

    }

}
