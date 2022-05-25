/**
 * Created by yu on 2017/1/15.
 */
var socket = null;
var isAuth = false;
var userNick = null;
var userCount = 0;
$(function () {
    $("#menuModal").modal('show');
    var height = $(window).height();
    $('#content').css("height", height - $('#top').height() - $('#opt').height() - 40);

    $('#loginBtn').click(function(){
        userLogin();
    });

    $('#faceBtn').qqFace({
        id: 'facebox',
        assign: 'mess',
        path: 'arclist/'	//表情存放的路径
    });

    $('#sendBtn').click(function () {
        var mess = $("#mess").val().trim();
        if(mess){
            sendMess(mess);
            $("#mess").val('');
        }
    }).keyup(function(e){
        var keyCode = e.which || e.keyCode;
        if(keyCode===13){
            $("#sendBtn").click();
        }
    });
});


$(function(){
    $("#send-btn").on("click", sendFunc);
});

// 文件上传
let sendFunc = function(){
    let formData = new FormData($('#upload')[0]);
    $.ajax({
        url:"http://localhost:8080/upload",
        type:"post",
        data:formData,
        async: false,
        cache: false,
        // 告诉jQuery不要去处理发送的数据
        processData : false,
        // 告诉jQuery不要去设置Content-Type请求头
        contentType : false,
        success:function(res){
            debugger;
            if(res){
                alert("上传成功！");
            }
            console.log(res);
        },
        error:function(jqXHR, textStatus, errorThrown){
            debugger;
            alert("网络连接失败,稍后重试", errorThrown);
        }

    });
}

function sendMess(mess) {
    send(true, "{'code':10086,'mess':'"+mess+"'}");
}
;


function userLogin() {
    if (!userNick) {
        userNick = $('#nick').val().trim();
    }
    if (userNick) {
        if (!window.WebSocket) {
            window.WebSocket = window.MozWebSocket;
        }
        if (window.WebSocket) {
            window.socket = new WebSocket("ws://192.168.10.76:9688/websocket");
            window.socket.onmessage = function (event) {
                var data = eval("(" + event.data + ")");
                console.log("onmessage data: " + JSON.stringify(data));
                switch (data.uri) {
                    case 1 << 8 | 220: // ping message
                    case 2 << 8 | 220: // pong message
                        console.log("ping message: " + JSON.stringify(data));
                        pingInvake(data);
                        break;
                    case 3 << 8 | 220: // system message
                        console.log("system message: " + JSON.stringify(data));
                        sysInvoke(data);
                        break;
                    case 4 << 8 | 220: // error message
                        console.log("error message: " + JSON.stringify(data));
                        closeInvoke(null);
                        break;
                    case 5 << 8 | 220: // auth message
                        console.log("auth message: " + JSON.stringify(data));
                        break;
                    case 6 << 8 | 220: // broadcast message
                        console.log("broadcast message: " + JSON.stringify(data));
                        broadcastInvake(data);
                        break;

                }
            };
            window.socket.onclose = function (event) {
                console.log("connection close!!!");
                closeInvoke(event);
            };
            window.socket.onopen = function (event) {
                console.log("connection success!!");
                openInvoke(event);
            };
        } else {
            alert("您的浏览器不支持WebSocket！！！");
        }
    } else {
        $('#tipMsg').text("请输入昵称");
        $('#tipModal').modal('show');
    }
}

function send(auth, mess) {
    if (!window.socket) {
        return;
    }
    if (socket.readyState == WebSocket.OPEN || auth) {
        console.log("send: " + mess);
        window.socket.send(mess);
    } else {
        $('#tipMsg').text("连接没有成功，请重新登录");
        $('#tipModal').modal('show');
    }
}
;

function openInvoke(event) {
    var obj = {};
    obj.code = 10000;
    obj.nick = $('#nick').val().trim();
    send(true, JSON.stringify(obj));
}
;


function closeInvoke(event) {
    window.socket = null;
    window.isAuth = false;
    window.userCount = 0;
    $('#tipMsg').text("登录失败，网络连接异常");
    $('#tipModal').modal('show');
}
;

/**
 * 处理系统消息
 * @param data
 */
function sysInvoke(data) {
    switch (data.extend.code) {
        case 20001: // user count
            console.log("current user: " + data.extend.mess);
            userCount = data.extend.mess;
            $('#userCount').text(userCount);
            break;
        case 20002: // auth
            console.log("auth result: " + data.extend.mess);
            isAuth = data.extend.mess;
            if (isAuth) {
                $("#menuModal").modal('hide');
                $('#chatWin').show();
                $('#content').append('ODHWIH*￥（！@H%_%！！');
                // $('#content').scrollTop($('#content')[0].scrollHeight);
            }
            break;
        case 20003: // system message
            console.log("system message: " + data.extend.mess);
            break;
    }
}
;

/**
 * 处理广播消息
 * @param data
 */
function broadcastInvake(data) {
    var mess = data.body;
    var nick = data.extend.nick;
    var uid = data.extend.uid;
    var time = data.extend.time;
    mess = replace_em(mess);
    var html = '<div class="title">'+nick+'&nbsp;('+uid+') &nbsp;'+time+'</div><div class="item">'+mess+'</div>';
    $("#content").append(html);
    $('#content').scrollTop($('#content')[0].scrollHeight);

}
;

function erorInvake(data) {

}
;

/**
 * 处理ping消息
 * @param data
 */
function pingInvake(data) {
    //发送pong消息响应
    send(isAuth, "{'code':10016}");
}
;
//查看结果
function replace_em(str) {
    str = str.replace(/\</g, '&lt;');
    str = str.replace(/\>/g, '&gt;');
    str = str.replace(/\n/g, '<br/>');
    str = str.replace(/\[em_([0-9]*)\]/g, '<img src="arclist/$1.gif" border="0" />');
    return str;
};