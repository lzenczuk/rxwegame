/**
 * Created by dev on 10/07/17.
 */

$(document).ready(function () {
    $("#msg").text("Hello js")

    var ws = new WebSocket("ws://localhost:8088/ws");

    ws.onerror = function (ev) {
        console.log("Error: "+ev)
    };

    ws.onmessage = function (ev) {
        console.log("Message: "+JSON.stringify(ev))
    };

    ws.onclose =  function (ev) {
        console.log("Close: "+JSON.stringify(ev))
    };

    ws.onopen = function (ev) {
        console.log("Open: "+JSON.stringify(ev));

        var bytearray = new Uint8Array(1);
        bytearray[0] = 0;
        ws.send(bytearray.buffer);

        bytearray[0] = 1;
        ws.send(bytearray.buffer);

        bytearray[0] = 2;
        ws.send(bytearray.buffer);

        window.setTimeout(function () {
            bytearray[0] = 127;
            ws.send(bytearray.buffer);
        }, 10000);


        console.log("-----> Done")
    };
});
