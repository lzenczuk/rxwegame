/**
 * Created by dev on 10/07/17.
 */

var UP = 38;
var DOWN = 40;
var LEFT = 37;
var RIGHT = 39;
var ESC = 27;

$(document).ready(function () {

    var reader = new FileReader();
    reader.onload = function() {
        console.log(reader.result);
        var dvX = new DataView(reader.result, 0, 4);
        var x = dvX.getInt32();
        console.log(x);
        var dvY = new DataView(reader.result, 4, 4);
        var y = dvY.getInt32();
        console.log(y);

        $("#box").css({top: y+'px', left: x+'px'})
    };

    var ws = new WebSocket("ws://localhost:8088/ws");

    ws.onerror = function (ev) {
        console.log("Error: "+ev)
    };

    ws.onmessage = function (ev) {
        if(typeof ev.data === "string" ) {
            console.log("Received data string");
        }else if(ev.data instanceof Blob){
            console.log("Received data blob");
            reader.readAsArrayBuffer(ev.data)
        }else if(ev.data instanceof ArrayBuffer){
            console.log("Received data ArrayBuffer");
        }else{
            console.log("Received data WTF");
        }
    };

    ws.onclose =  function (ev) {
        console.log("Close: "+JSON.stringify(ev))
    };

    ws.onopen = function (ev) {
        console.log("Open: "+JSON.stringify(ev));

        var bytearray = new Uint8Array(2);

        $(document).keydown(function(e){
            console.log("Key press: "+e.which)
            if(e.which===DOWN){
                bytearray[0] = 1;
                bytearray[1] = 1;
                ws.send(bytearray.buffer);
            }else if(e.which===UP){
                bytearray[0] = 1;
                bytearray[1] = 0;
                ws.send(bytearray.buffer);
            }else if(e.which===LEFT){
                bytearray[0] = 1;
                bytearray[1] = 2;
                ws.send(bytearray.buffer);
            }else if(e.which===RIGHT){
                bytearray[0] = 1;
                bytearray[1] = 3;
                ws.send(bytearray.buffer);
            }else if(e.which===ESC){
                var terByteArray = new Uint8Array(1);
                terByteArray[0] = 0;
                ws.send(terByteArray.buffer);
            }
        });

        /*var bytearray = new Uint8Array(2);

        bytearray[0] = 1;
        bytearray[1] = 1;
        ws.send(bytearray.buffer);

        bytearray[0] = 1;
        bytearray[1] = 1;
        ws.send(bytearray.buffer);

        bytearray[0] = 1;
        bytearray[1] = 0;
        ws.send(bytearray.buffer);

        bytearray[0] = 1;
        bytearray[1] = 3;
        ws.send(bytearray.buffer);

        bytearray[0] = 1;
        bytearray[1] = 3;
        ws.send(bytearray.buffer);

        bytearray[0] = 1;
        bytearray[1] = 1;
        ws.send(bytearray.buffer);

        bytearray[0] = 1;
        bytearray[1] = 2;
        ws.send(bytearray.buffer);

        bytearray[0] = 1;
        bytearray[1] = 3;
        ws.send(bytearray.buffer);

        var terByteArray = new Uint8Array(1);
        bytearray[0] = 0;
        ws.send(bytearray.buffer);*/

        console.log("-----> Done")
    };
});
