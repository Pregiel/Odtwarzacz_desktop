/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz.Connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import odtwarzacz.InfoLabel;
import odtwarzacz.MainFXMLController;
import odtwarzacz.Utils.Utils;

import javax.rmi.CORBA.Util;

/**
 *
 * @author Pregiel
 */
public class WifiConnection extends Connection {

    private final static int PORT = 1755;
    private final static String SEARCHING = "SEARCHING";
    private final static String CONNECTING = "CONNECTING";

    private ServerSocket serverSocket;
    private Socket clientSocket;


    @Override
    public void connect() {
        getConnectionInfo().setInfoText(InfoLabel.CONNECTION_WAITFORCLIENT_WIFI);

        Thread connect = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean next = false;
                    DataInputStream DIS = null;
                    DataOutputStream DOS = null;

                    serverSocket = new ServerSocket(PORT);
                    while (!next) {
                        clientSocket = serverSocket.accept();
                        DIS = new DataInputStream(clientSocket.getInputStream());
                        DOS = new DataOutputStream(clientSocket.getOutputStream());

                        String msg = DIS.readUTF();
                        if (msg.contains(SEARCHING)) {
                            DOS.writeUTF(Utils.getComputerName());

                            DIS.close();
                            DOS.close();
                            clientSocket.close();
                        } else if (msg.contains(CONNECTING)) {
                            next = true;
                        }
                    }

                    setConnected(true);
                    setStreams(DIS, DOS);
                } catch (IOException ex) {
                    Logger.getLogger(WifiConnection.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        connect.setDaemon(true);
        connect.start();

    }

    @Override
    public void disconnect() {
        super.disconnect();
        try {
            serverSocket.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
