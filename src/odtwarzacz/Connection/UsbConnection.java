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
    private static final int PORT = 38300;

    private boolean retryConnect = false;
    private int retryCount;

    @Override
    public void connect() {
        System.out.println("Connecting");
        getConnectionInfo().setInfoText(InfoLabel.CONNECTION_WAITFORCLIENT_USB);

        Thread connect = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Runtime.getRuntime().exec(ADB_PATH + " forward tcp:" + PORT + " tcp:" + PORT);


                    if (isPilotOpened()) {

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

                    } else {
                        System.out.println("wlacz pilot");
                    }

                } catch (ConnectException e) {
                    System.out.println("nie podlaczony");
                } catch (IOException e) {
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

    private boolean isPilotOpened() {
        try {
            Runtime rt = Runtime.getRuntime();
            String[] commands = {ADB_PATH, "shell", "dumpsys", "activity", "recents"};
            Process proc = rt.exec(commands);

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(proc.getInputStream()));
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                if (s.contains("A=com.pregiel.odtwarzacz_pilot")) {
                    if (s.contains("Recent #0:")) {
                        return true;
                    }
                }
            }
            return false;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public void disconnect() {
        super.disconnect();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
