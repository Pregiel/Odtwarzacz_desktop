/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz.Connection;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import odtwarzacz.*;

import javax.imageio.ImageIO;

/**
 * @author Pregiel
 */
public abstract class Connection {

    public static final String PLAY = "PLAY";
    public static final String FORWARD = "FORWARD";
    public static final String BACKWARD = "BACKWARD";
    public static final String TIME = "TIME";
    public static final String VOLUME = "VOLUME";
    public static final String MUTE = "MUTE";
    public static final String UNMUTE = "UNMUTE";
    public static final String DEVICE_NAME = "DEVICE_NAME";
    public static final String PLAYLIST_SEND = "PLAYLIST_SEND";
    public static final String PLAYLIST_UPDATE = "PLAYLIST_UPDATE";
    public static final String PLAYLIST_PLAY = "PLAYLIST_PLAY";
    public static final String PLAYLIST_PLAYING_INDEX = "PLAYLIST_PLAYING_INDEX";
    public static final String FORWARD_PRESSED = "FORWARD_PRESSED";
    public static final String FORWARD_RELEASED = "FORWARD_RELEASED";
    public static final String BACKWARD_PRESSED = "BACKWARD_PRESSED";
    public static final String BACKWARD_RELEASED = "BACKWARD_RELEASED";
    public static final String FILECHOOSER_DIRECTORY_TREE = "FILECHOOSER_DIRECTORY_TREE";
    public static final String FILECHOOSER_SHOW = "FILECHOOSER_SHOW";
    public static final String FILECHOOSER_DRIVE_LIST = "FILECHOOSER_DRIVE_LIST";
    public static final String FILECHOOSER_PLAY = "FILECHOOSER_PLAY";
    public static final String FILECHOOSER_PLAYLIST_ADD = "FILECHOOSER_PLAYLIST_ADD";
    public static final String SNAPSHOT = "SNAPSHOT";
    public static final String SNAPSHOT_REQUEST = "SNAPSHOT_REQUEST";

    private static final int SNAPSHOT_WIDTH = 200;
    private static final int SNAPSHOT_HEIGHT = 200;

    public static final String SEPARATOR = "::";

    private DataInputStream DIS;
    private DataOutputStream DOS;

    private boolean connected;

    private InfoLabel connectionInfo;

    private String connectedDeviceName;

    private MediaFXMLController mediaController;

    private MainFXMLController mainFXMLController;

    public Connection() {
        this.connected = false;
    }

    public MainFXMLController getMainFXMLController() {
        return mainFXMLController;
    }

    public void setMainFXMLController(MainFXMLController mainFXMLController) {
        this.mainFXMLController = mainFXMLController;
    }

    public MediaFXMLController getMediaController() {
        return mediaController;
    }

    public void setMediaController(MediaFXMLController mediaController) {
        this.mediaController = mediaController;
    }

    public String getConnectedDeviceName() {
        return connectedDeviceName;
    }

    public void setConnectedDeviceName(String connectedDeviceName) {
        this.connectedDeviceName = connectedDeviceName;
    }

    public void setConnectionInfo(InfoLabel connectionInfo) {
        this.connectionInfo = connectionInfo;
    }

    public InfoLabel getConnectionInfo() {
        return connectionInfo;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public void setStreams(InputStream inputStream, OutputStream outputStream) {
        this.DIS = new DataInputStream(inputStream);
        this.DOS = new DataOutputStream(outputStream);
        executorService = Executors.newFixedThreadPool(10);
//        connectionInfo = mediaController.getConnectionInfo();
        getMessage();
        if (mediaController != null) {
            mediaController.setConnection(this);
        }

        try {
            sendMessage(DEVICE_NAME, InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public abstract void connect();

    public Connection getConnection() {
        return this;
    }

    public void getMessage() {
        if (isConnected()) {
            Thread connect = new Thread(new Runnable() {
                String msg_received = "";

                @Override
                public void run() {

                    try {
                        msg_received = DIS.readUTF();
                        System.out.println("Message: " + msg_received);
                        pilotController(msg_received);
                        getMessage();
                    } catch (IOException ex) {
                        disconnect();
//                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            connect.setDaemon(true);
            connect.start();
        }
    }

    public void pilotController(String msg) {
        String[] message = msg.split(SEPARATOR);

        switch (message[0]) {
            case TIME:
                final double currentTimeMilis = Double.parseDouble(message[1]);
//            final double mediaTimeMilis = Double.parseDouble(message[2]);
                if (mediaController != null) {

                    mediaController.getTimeSlider().moveTo(currentTimeMilis);
                }
                break;

            case VOLUME:
                if (mediaController != null) {
                    final double volumeValue = Double.parseDouble(message[1]);

                    mediaController.getVolSlider().setVolume(volumeValue);
                }
                break;

            case PLAY:
                if (mediaController != null) {
                    mediaController.playPauseButton(null);
                }
                break;

            case FORWARD:
                if (mediaController != null) {
                    mediaController.forwardButton(null);
                }
                break;

            case BACKWARD:
                if (mediaController != null) {
                    mediaController.backwardButton(null);
                }
                break;

            case MUTE:
                if (mediaController != null) {
                    mediaController.getVolSlider().mute();
                }
                break;

            case UNMUTE:
                if (mediaController != null) {
                    mediaController.getVolSlider().mute();
                }
                break;

            case DEVICE_NAME:
                setConnectedDeviceName(message[1]);
                ResourceBundle resourceBundle = ResourceBundle.getBundle("Translations.MessagesBundle", MyLocale.getLocale(),
                        ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES));
                System.out.println(resourceBundle.getString("connection.connectedwith") + message[1]);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        connectionInfo.setInfoText(InfoLabel.CONNECTION_CONNECTED, message[1]);
                    }
                });

                sendMessage(PLAYLIST_SEND, MainFXMLController.getPlaylist().toMessage());
                break;

            case PLAYLIST_SEND:
                sendMessage(PLAYLIST_SEND, MainFXMLController.getPlaylist().toMessage());
                sendMessage(PLAYLIST_PLAYING_INDEX, MainFXMLController.getPlaylist().getPlaylistIndex());
                break;

            case FORWARD_PRESSED:
                mediaController.forwardButtonPressed(null);
                break;

            case FORWARD_RELEASED:
                mediaController.forwardButtonReleased(null);
                break;

            case BACKWARD_PRESSED:
                mediaController.backwardButtonPressed(null);
                break;

            case BACKWARD_RELEASED:
                mediaController.backwardButtonReleased(null);
                break;

            case PLAYLIST_PLAY:
                MainFXMLController.getPlaylist().play(Integer.parseInt(message[1]) + 1);
                break;

            case FILECHOOSER_DIRECTORY_TREE:
                sendMessage(FILECHOOSER_DIRECTORY_TREE, Utils.getDirectoryTree(new File(message[1])));
                break;

            case FILECHOOSER_SHOW:
                sendMessage(FILECHOOSER_SHOW, Utils.getDriveList());
                break;

            case FILECHOOSER_DRIVE_LIST:
                sendMessage(FILECHOOSER_DRIVE_LIST, Utils.getDriveList());
                break;

            case FILECHOOSER_PLAY:
                mainFXMLController.loadFile(new File(message[1]));
                break;

            case FILECHOOSER_PLAYLIST_ADD:
                MainFXMLController.getPlaylist().add(new File(message[1]));
                break;

            case SNAPSHOT_REQUEST:
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                sendSnapshot();

                            }
                        });

                    }
                });
        }
    }

    private ExecutorService executorService;

    public void sendMessage(Object... messages) {
        if (isConnected()) {
            try {
                StringBuilder stringBuilder = new StringBuilder();
                for (Object o : messages) {
                    stringBuilder.append(o).append(SEPARATOR);
                }
                DOS.writeUTF(stringBuilder.toString());
            } catch (IOException ex) {
                disconnect();
//                    Logger.getLogger(WifiConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


    public void sendSnapshot() {
        if (isConnected() && mediaController.getMediaView() != null) {
            Image image = Utils.scale(mediaController.getMediaView().snapshot(new SnapshotParameters(), null),
                    SNAPSHOT_WIDTH,
                    SNAPSHOT_HEIGHT,
                    true);

            ByteArrayOutputStream s = new ByteArrayOutputStream();
            byte[] res;
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", s);
                res = s.toByteArray();
                sendMessage(Connection.SNAPSHOT, res.length);
                DOS.write(res);

                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect() {
        if (isConnected()) {
            setConnected(false);
            DIS = null;
            DOS = null;
            Platform.runLater(() -> {
                connectionInfo.setInfoText(InfoLabel.CONNECTION_DISCONNECTED, getConnectedDeviceName());
            });
            executorService.shutdown();
        }

    }
}
