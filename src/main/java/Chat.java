import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static j2html.TagCreator.*;
import static spark.Spark.init;
import static spark.Spark.staticFileLocation;
import static spark.Spark.webSocket;

/**
 * Created by Jan on 10.01.2017.
 */
public class Chat {
    static Map<Session, String> userUsernameMap = new ConcurrentHashMap<Session, String>();
    static int nextUserNumber = 1;

    public static void main(String[] args) {
        staticFileLocation("/public");
        webSocket("/chat", ChatWebSocketHandler.class);
        init();
    }


    public static void broadcastMessage(String sender, String message){
        userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session ->{
            try{
                session.getRemote().sendString(String.valueOf(new JSONObject()
                    .put("userMessage", createHtmlMessageFromSender(sender, message))
                    .put("userlist", userUsernameMap.values())
                ));
            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    private static String createHtmlMessageFromSender(String sender, String message){
        return article().with(
                b(sender + " says:"),
                p(message),
                span().withClass("timestamp").withText(new SimpleDateFormat("HH:mm:ss").format(new Date()))
        ).render();
    }



}
