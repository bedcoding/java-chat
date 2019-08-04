// Node.js 파일 실행방법:
// 1. Node.js를 설치한다.
// 2. 바탕화면에 AhnServer 파일을 넣는다.
// 3. 콘솔 창에서 node AhnServer라고 입력한다.


var net = require('net');
var sockets=[];


var server = net.createServer(function(client) {
    client.setTimeout(1000);
    client.setEncoding('utf8');

    sockets.push(client);    
    client.on('data', function (data) {

        // 1. (InputStream) 서버가 클라이언트로부터 데이터 받음 
        console.log('[서버 ← 클라이언트 (서버가 메시지 받음)] ' + data);



        // 2. (OutputStream) 서버가 클라이언트로 데이터 전송 (클라이언트 net.socket객체를 사용하여 데이터를 반환)
        dataMap=JSON.stringify({
            'aaa':data});
      
        for(var i = 0 ; i < sockets.length; i++){
            sockets[i].write(data);
        }
        console.log("[서버 → 클라이언트 (서버가 메시지 전달)] " + data);
    })
}).listen(2222, function () 
{
    console.log('Server on');
});