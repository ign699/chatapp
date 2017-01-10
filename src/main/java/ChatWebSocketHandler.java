import Bot.Bot;
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
        String username = "User" + Chat.nextUserNumber++;
        Chat.userUsernameMap.put(user, username);
        Chat.broadcastMessage(sender = "Server", msg = (username + " joined the chat"));
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason){
        String username = Chat.userUsernameMap.get(user);
        Chat.userUsernameMap.remove(user);
        Chat.broadcastMessage(sender = "server", msg = (username + " left the chat"));
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message){
        Chat.broadcastMessage(sender = Chat.userUsernameMap.get(user), msg = message);
        if(message.equals("!pogoda")){
            Chat.broadcastMessage(sender = "Bot", msg = bot.sayWeather());
        }
        if(message.equals("!godzina")){
            Chat.broadcastMessage(sender = "Bot", msg = bot.sayHour());
        }
        if(message.equals("!dzien")){
            Chat.broadcastMessage(sender = "Bot", msg = bot.sayDay());
        }
    }

}
