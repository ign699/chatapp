/**
 * Created by Jan on 10.01.2017.
 */

var webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/chat/");

document.getElementById("send").style.display="none";
document.getElementById("userlist").style.display="none";
document.getElementById("leave").style.display="none";
var nicknames = [];
var nickname;
webSocket.onmessage = function (msg) {
    var data = JSON.parse(msg.data);
    if(data.type==="roomlist"){
        displayRoomList(data)
    }
    if(data.type==="message"){
        updateChat(data);
    }
    if(data.type==="userlist"){
        nicknames = data.userlist;
        sendNickname();
    }
};

webSocket.onclose = function () {
    alert("WebSocket connection closed");
};

webSocket.onopen = function () {
    sendMessage({
        type:"getnames",
        text:""
    });
};


function checkIfAvalible(nickname){
    for(var i = 0; i < nicknames.length; i++){
        if(nicknames[i] === nickname){
            return false;
        }
    }
    return true;
}
function getNickname() {
    if(document.cookie === "") {
        return prompt("Name", "Your name");
    }
    else{
        return document.cookie;
    }
}

function sendNickname(){
    nickname = getNickname();
    if(checkIfAvalible(nickname)){
        document.cookie=nickname;
        sendMessage({
            type:"nickname",
            text:nickname
        })
    }
    else{
        document.cookie="";
        sendNickname();
    }
}
id("send").addEventListener("click", function(){
    sendMessage({
        type:"message",
        text:id("message").value
    });
});

id("message").addEventListener("keypress", function(e){
    if (e.keyCode === 13){
        if(document.getElementById("send").style.display==="none"){
            sendMessage({
                type:"room",
                text:e.target.value
            });
            showChat()
        }
        else{
        sendMessage({
        type:"message",
        text:e.target.value
        });}
    }
});


id("room").addEventListener("click", function (e) {
    sendMessage({
        type:"room",
        text:id("message").value
    });
    showChat();
});
function showChat() {
    id("message").placeholder="Type your message";
    document.getElementById("send").style.display="block";
    document.getElementById("userlist").style.display="block";
    document.getElementById("room").style.display="none";
    document.getElementById("roomlist").style.display="none";
    document.getElementById("leave").style.display="block";
}
function showRooms() {
    id("message").placeholder="Room name";
    id("chat").innerHTML="";
    document.getElementById("send").style.display="none";
    document.getElementById("userlist").style.display="none";
    document.getElementById("room").style.display="block";
    document.getElementById("roomlist").style.display="block";
    document.getElementById("leave").style.display="none";
}
id("roomlist").addEventListener('click', function (e) {
    if(e.target.nodeName==="LI"){
        sendMessage({
            type:"room",
            text:e.target.innerText
        })
    }
    showChat();
});

id("leave").addEventListener('click', function (e) {
    sendMessage({
        type:"leave",
        text:""
    })
    showRooms();
});

function sendMessage(message) {
    if(message !== ""){
        webSocket.send(JSON.stringify(message));
        id("message").value = "";
    }
}
function displayRoomList(data) {
    id("roomlist").innerHTML="";
    data.roomlist.forEach(function(room){
        insert("roomlist", "<li>" + room + "</li>");
    });
}
function updateChat(data) {
    insert("chat", data.userMessage);
    id("userlist").innerHTML = "";
    data.userlist.forEach(function(user){
        insert("userlist", "<li>" + user + "</li>");
    });
}


function insert(targetId, message) {
    id(targetId).insertAdjacentHTML("afterbegin", message);
}

function id(id) {
    return document.getElementById(id);
}
