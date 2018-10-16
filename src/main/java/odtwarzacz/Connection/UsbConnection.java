/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz.Connection;

import odtwarzacz.InfoLabel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Pregiel
 */
public class UsbConnection extends Connection {

    private Socket socket;

    public static final int CONNECTION_PERIOD = 250, CONNECTION_MAX_RETRY = 20;

    private static final String ADB_PATH = "C:\\adb\\platform-tools\\adb.exe";
    private static final String ACTION_USB_CONNECT = "com.pregiel.odtwarzacz_pilot.UsbConnectReceiver.ACTION_USB_CONNECT";
    private static final int PORT = 38300;

    private boolean retryConnect = false;
    private int retryCount;

    @Override
    public void connect() {

        Thread connect = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Runtime rt = Runtime.getRuntime();
                    rt.exec(ADB_PATH + " forward tcp:" + PORT + " tcp:" + PORT);


                    switch (isPilotOpened()) {
                        case 0:
                            getConnectionInfo().setInfoText(InfoLabel.CONNECTION_WAITFORCLIENT_USB);
                            retryConnect = true;

                            Timer t = new Timer();
                            t.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    if (retryConnect && retryCount < CONNECTION_MAX_RETRY) {
                                        retryCount++;
                                        try {
                                            setConnected(false);
                                            socket = new Socket("localhost", PORT);

                                            setConnected(true);

                                            setStreams(socket.getInputStream(), socket.getOutputStream());
                                        } catch (IOException ex) {
                                            Logger.getLogger(UsbConnection.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    } else {
                                        cancel();
                                    }
                                }
                            }, 0, CONNECTION_PERIOD);
                            //adb shell am broadcast -a com.pregiel.odtwarzacz_pilot.UsbConnectReceiver.ACTION_USB_CONNECT
                            String[] commands = {ADB_PATH, "shell", "am", "broadcast", "-a", ACTION_USB_CONNECT};
                            rt.exec(commands);
                            break;


                        default:
                            getConnectionInfo().setInfoText(InfoLabel.CONNECTION_USB_NOPILOT);
                    }

                } catch (IOException e) {
                    getConnectionInfo().setInfoText(InfoLabel.CONNECTION_USB_NOPILOT);
                    e.printStackTrace();
                }
            }
        });
        connect.setDaemon(true);
        connect.start();

    }

    public void setRetryConnect(boolean retryConnect) {
        this.retryConnect = retryConnect;
    }

    public boolean isRetryConnect() {
        return retryConnect;
    }

    private int isPilotOpened() {
        try {
            Runtime rt = Runtime.getRuntime();
            String[] commands = {ADB_PATH, "shell", "dumpsys", "activity", "recents"};
            Process proc = rt.exec(commands);

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(proc.getInputStream()));
            String s;
            boolean connected = false;
            while ((s = stdInput.readLine()) != null) {
                connected = true;
                if (s.contains("A=com.pregiel.odtwarzacz_pilot")) {
                    if (s.contains("Recent #0:")) {
                        return 0;
                    }
                }
            }
            if (connected) {
                return 1;
            }
            return 1;

        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        }
    }


    @Override
    public void disconnect() {
        super.disconnect();
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
