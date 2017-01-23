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
import Bot.Bot;
/**
 * Created by Jan on 21.01.2017.
 */
public class MessageHandler {
    private Chat chat;

    public MessageHandler(){
        this.chat = new Chat();
    }

    public void actOnMessage(Message message, Session session){
        switch(message.getType()){
            case "nickname":
                handleNickname(message, session);
                break;
            case "room":
                handleRoom(message, session);
                break;
            case "message":
                handleMessage(message, session);
                break;
            case "leave":
                handleLeave(message, session);
                break;
            case "getnames":
                handleNameList(message, session);
                break;
            case "onclose":
                handleOnClose(message, session);
                break;
            default:
                break;
        }
    }

    private void handleNickname(Message message, Session session){
        chat.addUserNickname(session, message.getText());
        chat.sendRoomList(session);
    }

    private void handleOnClose(Message message, Session session){
        String name = chat.getNickName(session);
        String room = chat.getRoom(session);
        chat.serverSaysUserLeft(session);
        chat.removeUser(session);
    }

    private void handleNameList(Message message, Session session){
        chat.sendTakenNicknamesToUser(session);
    }

    private void handleRoom(Message message, Session session){
        chat.addUserToRoom(session, message.getText());
        chat.serverSaysUserJoined(session);
    }

    private void handleMessage(Message message, Session session){
        chat.broadcastMessage(session, message.getText());
        if(chat.getRoom(session).equals("chatbot")){
            chat.botAnswer(session, message.getText());
        }
    }

    private void handleLeave(Message message, Session session){
        chat.serverSaysUserLeft(session);
        chat.sendRoomList(session);
    }

}
