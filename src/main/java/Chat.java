import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.StringJoiner;
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
    static Map<Session, String> userRoomMap = new ConcurrentHashMap<>();
    static LinkedList<String> roomList = new LinkedList<>();


    public static void main(String[] args) {
        staticFileLocation("/public");
        roomList.add("chatbot");
        webSocket("/chat", ChatWebSocketHandler.class);
        init();
    }


    public static void broadcastMessage(String sender, String message, String room){
        LinkedList<String> users = createRoomUsersList(room);
        userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session ->{
            try{
                if (userRoomMap.containsKey(session)) {
                    if (userRoomMap.get(session).equals(room)) {
                        session.getRemote().sendString(String.valueOf(new JSONObject()
                                .put("type", "message")
                                .put("userMessage", createHtmlMessageFromSender(sender, message))
                                .put("userlist", users))
                        );
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }
    private static LinkedList<String> createRoomUsersList(String room){
        LinkedList<String> users = new LinkedList<>();
        userUsernameMap.keySet().forEach(
                user -> {
                    if (userRoomMap.containsKey(user)) {
                        if (userRoomMap.get(user).equals(room)) {
                            users.add(userUsernameMap.get(user));
                        }
                    }
                }
        );
        return users;
    }
    public static String getRoom(Session user){
        return userRoomMap.get(user);
    }
    public static String getNickName(Session user){
        return userUsernameMap.get(user);
    }
    public static void sendRoomList(Session session){
        try{
            session.getRemote().sendString(String.valueOf(new JSONObject()
            .put("type", "roomlist")
            .put("roomlist", roomList.toArray())
            ));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private static String createHtmlMessageFromSender(String sender, String message){
        return article().with(
                b(sender + " says:"),
                p(message),
                span().withClass("timestamp").withText(new SimpleDateFormat("HH:mm:ss").format(new Date()))
        ).render();
    }



}
