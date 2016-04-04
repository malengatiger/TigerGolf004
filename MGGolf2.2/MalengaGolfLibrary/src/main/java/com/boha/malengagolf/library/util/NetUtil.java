package com.boha.malengagolf.library.util;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.boha.malengagolf.library.data.RequestDTO;
import com.boha.malengagolf.library.data.ResponseDTO;
import com.boha.malengagolf.library.volley.toolbox.BaseVolley;
import com.google.gson.Gson;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

/**
 * Utility class to manage web socket communications for the application
 * Created by aubreyM on 2014/08/10.
 */
public class NetUtil {
    public interface NetListener {
        public void onMessage(ResponseDTO response);
        public void onClose();
        public void onError(String message);

    }

    static NetListener netListener;
    static RequestDTO request;
    static Context ctx;
    static long start, end;

    public static void disconnectSession() {
        if (mWebSocketClient != null) {
            mWebSocketClient.close();
            Log.e(LOG, "@@@@@@@@ webSocket session disconnected");
        }
    }

//    public static void sendRequest(Context c, final String suffix, RequestDTO req, NetListener listener) {
//        Log.e(LOG,"&&&&&&&&& about to start communications with websocket endpoint: " + suffix);
//        start = System.currentTimeMillis();
//        netListener = listener;
//        request = req;
//        ctx = c;
//        TimerUtil.startTimer(new TimerUtil.TimerListener() {
//            @Override
//            public void onSessionDisconnected() {
//                try {
//                    connectWebSocket(suffix, request);
//                    return;
//                } catch (URISyntaxException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        try {
//            if (mWebSocketClient == null) {
//                connectWebSocket(suffix);
//            } else {
//                String json = gson.toJson(req);
//                mWebSocketClient.send(json);
//                Log.d(LOG, "########### web socket message sent\n" + json);
//            }
//        } catch (WebsocketNotConnectedException e) {
//            try {
//                Log.e(LOG, "WebsocketNotConnectedException. Problems with web socket", e);
//                connectWebSocket(suffix, req);
//            } catch (URISyntaxException e1) {
//                Log.e(LOG, "Problems with web socket", e);
//                netListener.onError("Problem starting server socket communications\n" + e1.getMessage());
//            }
//        } catch (URISyntaxException e) {
//            Log.e(LOG, "Problems with web socket", e);
//            netListener.onError("Problem starting server socket communications");
//        }
//    }

    private static void connectWebSocket(String socketSuffix, final RequestDTO request) throws URISyntaxException {
        URI uri = new URI(Statics.WEBSOCKET_URL + socketSuffix);
        Log.e(LOG, "************ connectWebSocket, uri: " + uri.toString());
        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.w(LOG, "########## WEBSOCKET Opened: " + serverHandshake.getHttpStatusMessage() + " elapsed ms: " + (end - start));
                String json = gson.toJson(request);
                mWebSocketClient.send(json);
                Log.d(LOG, "########### web socket request sent after onOpen\n" + json);
            }

            @Override
            public void onMessage(String response) {
                end = System.currentTimeMillis();
                TimerUtil.killTimer();
                Log.i(LOG, "########## onMessage, length: " + response.length() + " elapsed: " + getElapsed()
                        + "\nString: " + response);
                try {
                    ResponseDTO r = gson.fromJson(response, ResponseDTO.class);
                    if (r.getStatusCode() == 0) {
                        if (r.getSessionID() != null) {
                            SharedUtil.setSessionID(ctx, r.getSessionID());
                            String json = gson.toJson(request);
                            Log.w(LOG, "########### websocket about to send message sfter getting sessionid\n" + json);
                            mWebSocketClient.send(json);
                            Log.w(LOG, "########### websocket message sent\n" + json);
                        } else {
                            netListener.onMessage(r);
                        }
                    } else {
                        netListener.onError(r.getMessage());
                    }
                } catch (Exception e) {
                    Log.e(LOG, "Failed to parse response from server", e);
                    netListener.onError("Failed to parse response from server");
                }

            }

            @Override
            public void onMessage(ByteBuffer bb) {
                TimerUtil.killTimer();
                Log.i(LOG, "########## onMessage (retried)... got ByteBuffer, capacity: " + bb.capacity());
                try {
                    ZipUtil.unpack(bb, netListener);
                } catch (ZipUtil.ZipException e) {
                    onError(e);
                }
                end = System.currentTimeMillis();


            }


            @Override
            public void onClose(final int i, String s, boolean b) {
                Log.e(LOG, "########## WEBSOCKET onClose, status code:  " + i + " boolean: " + b);
                netListener.onClose();
            }

            @Override
            public void onError(final Exception e) {
                Log.e(LOG, "----------> onError connectWebSocket", e);
                netListener.onError("Server communications failed. Please try again");


            }
        };

        Log.d(LOG, "#### #### -------------> starting mWebSocketClient.connect ...");
        mWebSocketClient.connect();
    }

    private static void connectWebSocket(String socketSuffix) throws URISyntaxException {
        URI uri = new URI(Statics.WEBSOCKET_URL + socketSuffix);
        Log.e(LOG, "----------- connectWebSocket, uri: " + uri.toString());
        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.w(LOG, "########## WEBSOCKET Opened: " + serverHandshake.getHttpStatusMessage());
                String json = gson.toJson(request);
                mWebSocketClient.send(json);
                Log.d(LOG, "########### web socket request sent after onOpen\n" + json);
            }

            @Override
            public void onMessage(String response) {
                TimerUtil.killTimer();
                end = System.currentTimeMillis();
                Log.i(LOG, "########## onMessage, length: " + response.length() + " elapsed: " + getElapsed()
                        + "\n" + response);
                try {
                    ResponseDTO r = gson.fromJson(response, ResponseDTO.class);
                    if (r.getStatusCode() == 0) {
                        if (r.getSessionID() != null) {
                            SharedUtil.setSessionID(ctx,r.getSessionID());
                        }
                    } else {
                        netListener.onError(r.getMessage());
                    }
                } catch (Exception e) {
                    Log.e(LOG, "Failed to parse response from server", e);
                    netListener.onError("Failed to parse response from server");
                }

            }

            @Override
            public void onMessage(ByteBuffer bb) {
                Log.i(LOG, "########## onMessage ... got ByteBuffer, capacity: " + bb.capacity());
                TimerUtil.killTimer();
                try {
                    ZipUtil.unpack(bb, netListener);
                } catch (ZipUtil.ZipException e) {
                    onError(e);
                }
            }


            @Override
            public void onClose(final int i, String s, boolean b) {
                Log.e(LOG, "########## WEBSOCKET onClose, status code:  " + i + " boolean: " + b);
                //netListener.onClose();
                Log.e(LOG, "### #### onClose: re-starting mWebSocketClient.connect ...");
                mWebSocketClient.connect();

            }

            @Override
            public void onError(final Exception e) {
                Log.e(LOG, "---------> onError connectWebSocket", e);
                netListener.onError("Server communications failed. Please try again");


            }
        };

        Log.w(LOG, "### #### -------------> starting mWebSocketClient.connect ...");
        mWebSocketClient.connect();
    }


    static WebSocketClient mWebSocketClient;
    static final String LOG = NetUtil.class.getSimpleName();
    static final Gson gson = new Gson();

    public static String getElapsed() {
        BigDecimal m = new BigDecimal(end - start).divide(new BigDecimal(1000));

        return "" + m.doubleValue() + " seconds";
    }

    public static void sendRequest(Context c, final String suffix, RequestDTO req, final NetListener listener) {
        Log.e(LOG,"&&&&&&&&& about to start communications with http endpoint: " + suffix);
        start = System.currentTimeMillis();
        netListener = listener;
        request = req;
        ctx = c;

        BaseVolley.sendRequest(suffix, req, c, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(ResponseDTO response) {
                if (response.getStatusCode() == 0) {
                    listener.onMessage(response);
                } else {
                    listener.onError(response.getMessage());
                }
            }

            @Override
            public void onVolleyError(VolleyError error) {
                listener.onError(error.getMessage());
            }
        });
    }
}
