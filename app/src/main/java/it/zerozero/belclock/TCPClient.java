package it.zerozero.belclock;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by David on 07/01/2017.
 */

public class TCPClient {

    public static final int CLIENT_NOT_CONNECTED = 10001;
    public static final int CLIENT_CONNECTED = 10002;
    public static final int CLIENT_DONE = 10005;
    public static final int CLIENT_ERROR = 10007;
    public int clientStatus = 10000;
    private String mReply;
    private static String mServerIP = "0.0.0.0";
    private static int mServerPort = 2201;
    private onMessageReceived mListener = null;
    private boolean clientGo;
    private Socket sock;

    public TCPClient(onMessageReceived listener) {
        this.mListener = listener;
    }

    public TCPClient() {
        Log.i("TCPClient", "running empty constructor.");
    }

    public String sendReceiveStr(String ip, int port, String sendStr) {
        mServerIP = ip;
        mServerPort = port;
        clientGo = true;
        String replyStr = null;
        PrintWriter out = null;
        BufferedReader in = null;
        clientStatus = CLIENT_NOT_CONNECTED;

        try {
            InetAddress serverAddr = InetAddress.getByName(mServerIP);
            sock = new Socket(serverAddr, mServerPort);
            Log.i("Socket connected", sock.toString());
            clientStatus = CLIENT_CONNECTED;
        }
        catch (Exception e) {
            e.printStackTrace();
            clientStatus = CLIENT_ERROR;
        }
        // -----------------------------------------------------------------------------------------
        if (sock != null) {
            try {
                out = new PrintWriter(sock.getOutputStream(), true); // TODO: 26/03/2017 check new "true" parameter
                in = new BufferedReader(new InputStreamReader(sock.getInputStream(), "UTF-8"));
            } catch (IOException e) {
                Log.e("sendString", "Error creating I/O streams.");
                e.printStackTrace();
            }
            // -------------------------------------------------------------------------------------
            if (!sock.isClosed()) {
                try {
                    out.println(sendStr);
                    out.flush();
                    Log.i("sendString", "send >>");
                    replyStr = in.readLine().toString(); // Forse il toString non serve.
                    Log.i("sendString", "receive <<");
                    if(replyStr == null){
                        Log.e("sendStr", "replyStr is NULL.");
                    }
                    out.close();
                    clientStatus = CLIENT_DONE;
                } catch (IOException e) {
                    Log.e("sendString", "IOException occurred.");
                    e.printStackTrace();
                    clientStatus = CLIENT_ERROR;
                } catch (Exception e) {
                    Log.e("sendString", "Other exception occurred.");
                    e.printStackTrace();
                    clientStatus = CLIENT_ERROR;
                }
            } else {
                Log.e("sendString()", "socket is closed.");
            }
        } else {
            Log.e("sendString", "sock is null.");
        }
        // -----------------------------------------------------------------------------------------
        if(replyStr == null){
            return "<null>";
        } else {
            Log.i("replyStr", replyStr);
            return replyStr;
        }
    }

    public void disconnect(){
        if (sock != null) {
            try {
                sock.close();
                Log.i("Socket sock", "close()");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface onMessageReceived {

        // TODO: Implement in MainActivity using AsyncTask
        public void messageReceived(String mess);

    }
}
