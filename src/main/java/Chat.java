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
    public static String getRoom(Session user){
        return userRoomMap.get(user);
    }
    public static String getNickName(Session user){
        return userUsernameMap.get(user);
    }

    public static void main(String[] args) {
        staticFileLocation("/public");
        roomList.add("chatbot");
        webSocket("/chat", ChatWebSocketHandler.class);
        init();
    }






}
