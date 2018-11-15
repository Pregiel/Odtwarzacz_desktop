package odtwarzacz;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;
import odtwarzacz.Connection.Connection;
import odtwarzacz.Metadata.Metadata;
import odtwarzacz.Metadata.MetadataAudio;
import odtwarzacz.Metadata.MetadataVideo;
import odtwarzacz.Sliders.TimeSlider;
import odtwarzacz.Sliders.VolumeSlider;
import odtwarzacz.Utils.ExpandableTimeTask;
import odtwarzacz.Utils.Utils;

import java.io.File;
import java.net.URL;
import java.util.*;

import static odtwarzacz.IconFont.ICON_PAUSE;
import static odtwarzacz.IconFont.ICON_PLAY;
import static odtwarzacz.MainFXMLController.getPlaylist;

/**
 * FXML Controller class
 *
 * @author Pregiel
 */
public class MediaFXMLController implements Initializable {

    public static final double VOLUME_CLICK_VALUE = 0.05d;

    public Button playlistButton;
    public BorderPane parent;
    public Button fullscreenButton;
    public ToggleButton randomToggleButton;
    public Label mediaLabel;
    public GridPane mediaBar;
    public Label timeBoxLabel;
    public AnchorPane timeBox;
    @FXML
    private BorderPane pane;
    @FXML
    private HBox controllers;
    @FXML
    private Label timeLabel1, timeLabel2, volLabel;
    @FXML
    private MediaView mediaView;
    @FXML
    private Button playButton;

    private MediaPlayer mediaPlayer;

    private boolean atEndOfMedia = false;
    private Duration duration;
    private boolean repeat, random;

    @FXML
    private Button volButton;
    @FXML
    private AnchorPane volBox;
    @FXML
    private AnchorPane volBackTrack;

    private VolumeSlider volSlider;
    @FXML
    private Pane volTrack;
    @FXML
    private AnchorPane timeBackTrack;
    @FXML
    private Pane timeTrack;

    private TimeSlider timeSlider;

    private Connection connection;
    @FXML
    private Button backwardButton;
    @FXML
    private Button forwardButton;

    @FXML
    private ToggleButton repeatToggleButton;

    private InfoLabel fileInfoLabel;

    private Timer previewTimer;

    private Metadata metadata;

    private ExpandableTimeTask hideMediaBarTask;


    public void setConnection(Connection connection) {
        this.connection = connection;
        if (connection != null) {
            connection.setMediaController(this);
            if (volSlider != null) {
                volSlider.setConnection(connection);
            }
            if (metadata != null) {
                sendFileLabel(metadata);
                getPlaylist().sendNextFile();
            }
        }
    }

    public void setFileInfoLabel(InfoLabel fileInfoLabel) {
        this.fileInfoLabel = fileInfoLabel;
    }

    /**
     * Initializes the controller class.
     */

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        repeat = Boolean.parseBoolean(String.valueOf(Odtwarzacz.getConfig().get("repeat")));

        if (repeat) {
            repeatToggleButton.setSelected(true);
        }

        random = Boolean.parseBoolean(String.valueOf(Odtwarzacz.getConfig().get("random")));

        if (random) {
            randomToggleButton.setSelected(true);
        }

        pane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                fullscreened = false;
                fullscreenButton.setText(IconFont.ICON_FULLSCREEN);
            }
        });

        mediaLabel.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                playPauseButton(null);
            }
        });

        hideMediaBarTask = new ExpandableTimeTask(() -> {
            mediaBar.setVisible(false);
            if (fullscreened) {
                pane.setCursor(Cursor.NONE);
            }
        }, 2000);

        hideMediaBarTask.start();

        mediaLabel.setOnMouseMoved(event -> {
            if (hideMediaBarTask.isFinished() || !hideMediaBarTask.isStarted()) {
                hideMediaBarTask.start();
            } else {
                hideMediaBarTask.expand();
            }
            if (fullscreened && pane.getCursor() == Cursor.NONE) {
                pane.setCursor(Cursor.DEFAULT);
            }

            if (!mediaBar.isVisible()) {
                mediaBar.setVisible(true);
            }
        });

        mediaBar.setOnMouseEntered(event -> {
            hideMediaBarTask.stop();
        });

        mediaBar.setOnMouseExited(event -> {
            hideMediaBarTask.resume();
        });

        timeBackTrack.setOnMouseMoved(event -> {
            Bounds paneBounds = pane.localToScene(pane.getBoundsInLocal());
            Bounds backTrackBounds = timeBackTrack.localToScene(timeBackTrack.getBoundsInLocal());

            double position = event.getSceneX() / backTrackBounds.getMaxX();

            timeBoxLabel.setText(durationToTime(duration.multiply(position)));

            double x = event.getSceneX() - paneBounds.getMinX();
            double y = backTrackBounds.getMinY() - paneBounds.getMinY();

            timeBox.setTranslateX(x);
            timeBox.setTranslateY(y - timeBox.getHeight() - 6);
        });

        timeBackTrack.setOnMouseEntered(event -> {
            timeBox.setVisible(true);
            if (hideInTimeBox.isStarted() && !hideInTimeBox.isFinished()) {
                hideInTimeBox.stop();
            }

        });

        timeBackTrack.setOnMouseExited(event -> {
            if (!hideInTimeBox.isStarted() || hideInTimeBox.isFinished()) {
                hideInTimeBox.start();
            } else {
                hideInTimeBox.resume();
            }
        });

        hideInTimeBox = new ExpandableTimeTask(() -> {
            timeBox.setVisible(false);
        }, 500);

        Timer T = new Timer();
        T.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (connection != null) {
                    if (connection.isConnected()) {
                        if (mediaPlayer != null) {
                            connection.sendMessage(Connection.TIME, mediaPlayer.getCurrentTime().toMillis());
                        }
                    }
                }
            }
        }, 0, 5000);

    }

    private ExpandableTimeTask hideInTimeBox;

    private Pane centerPane;

    public void setScaling(Pane centerPane) {
//        timeSlider.setScalingPane(centerPane);
        this.centerPane = centerPane;

        mediaView.setPreserveRatio(true);
        mediaView.fitWidthProperty().bind(pane.widthProperty());
        mediaView.fitHeightProperty().bind(pane.heightProperty());
    }

    public void changeFile(File file) {
        mediaPlayer.dispose();
        changeMediaPlayer(file);
        mediaView.setMediaPlayer(mediaPlayer);
    }


    public void changeMediaPlayer(File file) {
        Media media = new Media(file.toURI().toString());
        MediaPlayer mp = new MediaPlayer(media);

        mp.setAutoPlay(true);

        mp.currentTimeProperty().addListener((observable) -> {
            updateValues();
        });

        mp.setOnPlaying(() -> {
            playButton.setText(ICON_PAUSE);
            if (connection != null)
                connection.sendMessage(Connection.PLAY, mp.getCurrentTime().toMillis());
        });

        mp.setOnPaused(() -> {
            if (!timeSlider.isChanging()) {
                playButton.setText(ICON_PLAY);
            }
            if (connection != null)
                connection.sendMessage(Connection.PAUSE, mp.getCurrentTime().toMillis());
        });

        mp.setOnReady(() -> {
            duration = mp.getMedia().getDuration();

            MAX_MOVETO_VALUE_SPEED = (int) (duration.toSeconds() * 0.05);
            START_MOVETO_VALUE = (int) (duration.toSeconds() * 0.01);
            if (duration.toSeconds() < 10) {
                MAX_MOVETO_VALUE_SPEED = 5;
                START_MOVETO_VALUE = 2;
            } else {
                MAX_MOVETO_VALUE_SPEED = 30;
                START_MOVETO_VALUE = 5;
            }

            if (connection != null)
                connection.sendMessage(Connection.DURATION, duration.toMillis(), mediaPlayer.getStatus().toString());

            updateValues();
        });

        mp.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 1);
        mp.setOnEndOfMedia(() -> {
            updateValues();
            if (!repeat) {
                if (MainFXMLController.getPlaylist().getPlaylistIndex() == -1) {
                    playButton.setText(ICON_PLAY);
                    atEndOfMedia = true;
                } else {
                    MainFXMLController.getPlaylist().playNext();
                }
            }
        });

        mediaView.setMediaPlayer(mp);

        setupVolume(mp);

        timeSlider = new TimeSlider(timeBackTrack, timeTrack, mp);

        mediaPlayer = mp;

//        volSlider.setVolume(mediaPlayer.getVolume());

        metadata = generateMetadata(file);

        metadata.generate(file, () -> {
            sendFileLabel(metadata);
            fileInfoLabel.setInfoText(InfoLabel.FILE_OPEN, metadata.generateLabel());
        });

        sendFileLabel(metadata);
        fileInfoLabel.setInfoText(InfoLabel.FILE_OPEN, metadata.generateLabel());
        timeSlider.setScalingPane(centerPane);

    }

    private ExpandableTimeTask volBoxDisappear;
    private boolean waitForReleaseVolumeSlider = false, mouseInBox = false;

    private void setupVolume(MediaPlayer mp) {
        volSlider = new VolumeSlider(volBackTrack, volTrack, mp, volLabel, volButton);
        volSlider.setConnection(connection);

        Platform.runLater(() -> {
            volSlider.setVolume(Double.parseDouble(Odtwarzacz.getConfig().getProperty("volume")));
            volSlider.setSliderPosition(mediaPlayer.getVolume());
        });

        Runnable disappear = () -> volBox.setVisible(false);

        volBoxDisappear = new ExpandableTimeTask(disappear, 1000);

        volButton.setOnMouseEntered((event) -> {
            volBox.setVisible(true);
            if (!volBoxDisappear.isFinished() && volBoxDisappear.isStarted()) {
                volBoxDisappear.stop();
            }
            waitForReleaseVolumeSlider = false;
            mouseInBox = true;
        });

        volButton.setOnMouseExited(event -> {
            if (volBoxDisappear.isFinished() || !volBoxDisappear.isStarted()) {
                volBoxDisappear.start();
            } else {
                volBoxDisappear.resume();
            }
            mouseInBox = false;
        });

        volBox.setOnMouseEntered(event -> {
            volBoxDisappear.stop();
            waitForReleaseVolumeSlider = false;
            mouseInBox = true;
        });

        volBox.setOnMouseExited(event -> {
            if (event.isPrimaryButtonDown()) {
                waitForReleaseVolumeSlider = true;
            } else {
                volBoxDisappear.resume();
            }
            mouseInBox = false;
        });

        pane.setOnMouseReleased(event -> {
            if (waitForReleaseVolumeSlider) {
                volBoxDisappear.resume();
                waitForReleaseVolumeSlider = false;
            }
        });

        pane.setOnScroll(event -> {
            volBox.setVisible(true);
            if (volBoxDisappear.isFinished() || !volBoxDisappear.isStarted()) {
                volBoxDisappear.start();
            } else {
                if (!mouseInBox) {
                    volBoxDisappear.resume();
                }
            }
            volSlider.addVolume(Math.signum(event.getDeltaY()) * 0.02);
        });
    }

    public Metadata generateMetadata(File file) {
        Metadata metadata = null;

        if (Arrays.asList(MainFXMLController.SUPPORTED_AUDIO).contains("*." + Utils.getExtension(file.getAbsolutePath()).toUpperCase())) {
            metadata = new MetadataAudio();
        } else if (Arrays.asList(MainFXMLController.SUPPORTED_VIDEO).contains("*." + Utils.getExtension(file.getAbsolutePath()).toUpperCase())) {
            metadata = new MetadataVideo();
        }

        return metadata;
    }

    private void sendFileLabel(Metadata metadata) {
        if (connection != null) {
            if (metadata instanceof MetadataAudio) {
                if (((MetadataAudio) metadata).getArtist() != null) {
                    connection.sendMessage(Connection.FILE_NAME, metadata.getTitle(), ((MetadataAudio) metadata).getArtist());
                } else {
                    connection.sendMessage(Connection.FILE_NAME, metadata.generateLabel());
                }
            } else {
                connection.sendMessage(Connection.FILE_NAME, metadata.generateLabel());
            }
            if (mediaPlayer != null) {
                connection.sendMessage(Connection.DURATION, mediaPlayer.getTotalDuration().toMillis(), mediaPlayer.getStatus().toString());
            }
        }
    }

    private boolean pilotTimeSliderMoving = false;

    public void setPilotTimeSliderMoving(boolean pilotTimeSliderMoving) {
        this.pilotTimeSliderMoving = pilotTimeSliderMoving;
    }

    public boolean isPilotTimeSliderMoving() {
        return pilotTimeSliderMoving;
    }

    private void updateValues() {
        Platform.runLater(() -> {
            Duration currentTime = mediaPlayer.getCurrentTime();
            timeLabel1.setText(durationToTime(currentTime));
            timeLabel2.setText(durationToTime(duration));


            if (duration.greaterThan(Duration.ZERO) && !timeSlider.isChanging()) {
                timeSlider.setSliderPosition(currentTime.divide(
                        duration.toMillis()).toMillis() * timeSlider.getBackTrack().getWidth()
                        / timeSlider.getBackTrack().getWidth());
            }


//            if (connection != null) {
//                if (!pilotTimeSliderMoving) {
//                    connection.sendMessage(Connection.TIME, currentTime.toMillis(), duration.toMillis());
//                }
//            }
        });
    }

    private String durationToTime(Duration duration) {
        int hours = 0, minutes = 0, seconds = 0;
        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) Math.floor(duration.toSeconds());
            hours = intDuration / (60 * 60);
            minutes = (intDuration - hours * 60 * 60) / 60;
            seconds = intDuration - hours * 60 * 60 - minutes * 60;
        }

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    @FXML
    public void playPauseButton(ActionEvent event) {
        if (!pilotTimeSliderMoving) {
            Status status = mediaPlayer.getStatus();
            System.out.println(mediaPlayer.getStatus());

            switch (status) {
                case UNKNOWN:
                case HALTED:
                    System.out.println("Blad");
                    break;
                case PAUSED:
                case READY:
                case STOPPED:
                    if (atEndOfMedia) {
                        mediaPlayer.seek(mediaPlayer.getStartTime());
                        atEndOfMedia = false;
                    }
                    mediaPlayer.play();
                    break;
                case PLAYING:
                    if (atEndOfMedia) {
                        mediaPlayer.seek(mediaPlayer.getStartTime());
                        atEndOfMedia = false;
                    } else {
                        mediaPlayer.pause();
                    }
                    break;
                default:
                    mediaPlayer.pause();
                    break;
            }
        }

    }

    public void pause() {
        mediaPlayer.pause();
    }

    public void play() {
        if (atEndOfMedia) {
            mediaPlayer.seek(mediaPlayer.getStartTime());
            atEndOfMedia = false;
        }
        mediaPlayer.play();
    }

    @FXML
    private void muteButton(ActionEvent event) {
        volSlider.mute();
    }

    @FXML
    public void backwardButton(ActionEvent event) {
//        timeSlider.moveTo(BACKWARD_VALUE);
    }

    public void forwardButton(MouseEvent mouseEvent) {
//        timeSlider.moveTo(FORWARD_VALUE);
//        System.out.println("forwardButton");
    }

    private Timer moveToTimer;
    private int moveToValueJump;
    private MouseEvent startPosition;

    private int START_MOVETO_VALUE = 5;
    private int MAX_MOVETO_VALUE_SPEED = 30;
    private boolean clicked = false;

    public void forwardButtonClick(MouseEvent mouseEvent) {
        if (clicked) {
            getPlaylist().playNext();
        }
    }

    public void forwardButtonPressed(MouseEvent mouseEvent) {
        clicked = true;
        moveToValueJump = 0;

        setTimeBox("+", forwardButton);

        moveToTimer = new Timer();
        moveToTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Duration value = getMoveToValue(1);
                timeSlider.moveTo(value);
                Platform.runLater(() ->
                {
                    timeBox.setVisible(true);
                    timeBoxLabel.setText("+" + (long) value.toSeconds() + "s");
                });
                clicked = false;
            }
        }, 200, 500);
        startPosition = mouseEvent;
    }

    public void forwardButtonReleased(MouseEvent mouseEvent) {
        timeBox.setVisible(false);
        moveToTimer.cancel();
        moveToTimer.purge();
    }

    public void forwardButtonDrag(MouseEvent mouseEvent) {
        int maxPosition = (int) (timeSlider.getWidth() - startPosition.getSceneX());

        double mouseX = mouseEvent.getSceneX() - startPosition.getSceneX();

        int maxValue = MAX_MOVETO_VALUE_SPEED - START_MOVETO_VALUE;

        double factor;
        if (mouseX >= 0) {
            factor = ((mouseEvent.getSceneX() - startPosition.getSceneX()) / maxPosition) * maxValue;
            moveToValueJump = (int) factor;
        } else {
            factor = (mouseEvent.getSceneX() / startPosition.getSceneX());

            if (factor - 0.5 < 0) {
                factor = 0;
            } else {
                factor = 2 * factor - 1;
            }

            moveToValueJump = (int) (factor * START_MOVETO_VALUE) - START_MOVETO_VALUE;
        }

        if (moveToValueJump > maxValue) {
            moveToValueJump = maxValue;
        } else if (moveToValueJump < 1 - START_MOVETO_VALUE) {
            moveToValueJump = 1 - START_MOVETO_VALUE;
        }
    }


    private Duration getMoveToValue(int sign) {
        return Duration.seconds(sign * (START_MOVETO_VALUE + moveToValueJump));
    }

    private void setTimeBox(String sign, Node node) {
        Platform.runLater(() ->
                timeBoxLabel.setText(sign + START_MOVETO_VALUE + "s"));

        Bounds paneBounds = pane.localToScene(pane.getBoundsInLocal());
        Bounds buttonBounds = node.localToScene(node.getBoundsInLocal());
        double x = buttonBounds.getMinX() - paneBounds.getMinX();
        double y = buttonBounds.getMinY() - paneBounds.getMinY();

        timeBox.setTranslateY(y - timeBox.getHeight() - 4);
        timeBox.setTranslateX(x + (buttonBounds.getWidth() / 2) - (timeBox.getWidth() / 2));
    }

    public void backwardButtonClick(MouseEvent mouseEvent) {
        if (clicked) {
            getPlaylist().playPrev();
        }
    }

    public void backwardButtonPressed(MouseEvent mouseEvent) {
        clicked = true;
        moveToValueJump = 0;

        setTimeBox("-", backwardButton);

        moveToTimer = new Timer();
        moveToTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Duration value = getMoveToValue(-1);
                timeSlider.moveTo(value);
                Platform.runLater(() ->
                {
                    timeBox.setVisible(true);
                    timeBoxLabel.setText((long) value.toSeconds() + "s");
                });
                clicked = false;
            }
        }, 200, 500);
        startPosition = mouseEvent;
    }

    public void backwardButtonReleased(MouseEvent mouseEvent) {
        timeBox.setVisible(false);
        moveToTimer.cancel();
        moveToTimer.purge();
    }

    public void backwardButtonDrag(MouseEvent mouseEvent) {
        int maxPosition = (int) (timeSlider.getWidth() - startPosition.getSceneX());

        double mouseX = mouseEvent.getSceneX() - startPosition.getSceneX();

        int maxValue = MAX_MOVETO_VALUE_SPEED - START_MOVETO_VALUE;

        double factor;
        if (mouseX >= 0) {
            factor = ((mouseEvent.getSceneX() - startPosition.getSceneX()) / maxPosition);

            if (factor > 0.5) {
                factor = 1;
            } else {
                factor = factor * 2;
            }

            moveToValueJump = 1 - (int) (factor * START_MOVETO_VALUE) - 2;
        } else {
            factor = (Math.abs(mouseX) / startPosition.getSceneX()) * maxValue;
            moveToValueJump = (int) factor;
        }

        if (moveToValueJump > maxValue) {
            moveToValueJump = maxValue;
        } else if (moveToValueJump < 1 - START_MOVETO_VALUE) {
            moveToValueJump = 1 - START_MOVETO_VALUE;
        }
    }


    public VolumeSlider getVolSlider() {
        return volSlider;
    }

    public TimeSlider getTimeSlider() {
        return timeSlider;
    }

    @FXML
    private void showHidePlaylist(ActionEvent event) {
        MainFXMLController.getPlaylist().toogle();
    }

    @FXML
    private void repeatToggleButton(ActionEvent event) {
        repeat = repeatToggleButton.isSelected();

        Odtwarzacz.getConfig().setProperty("repeat", String.valueOf(repeat));
        Odtwarzacz.getConfig().save();

        mediaPlayer.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 1);
        if (connection != null) {
            connection.sendMessage(repeat ? Connection.REPEAT_ON : Connection.REPEAT_OFF);
        }

//        getPlaylist().setNextPlaylistIndex();
    }

    public void repeatToggle() {
        repeatToggleButton.fire();
    }

    public MediaView getMediaView() {
        return mediaView;
    }


    private boolean fullscreened = false;
    private Stage fullscreenStage;
    private SplitPane splitPane;

    public void fullscreenToggle(ActionEvent event) {
        if (fullscreened) {
            ((Stage) parent.getScene().getWindow()).show();

            fullscreenStage.close();

            parent.setCenter(splitPane);
            fullscreened = false;
            fullscreenButton.setText(IconFont.ICON_FULLSCREEN);

            Platform.runLater(() -> {
                pane.setPrefHeight(Odtwarzacz.PLAYER_MIN_HEIGHT);
            });
        } else {
            getPlaylist().hide();
            parent = (BorderPane) pane.getParent().getParent().getParent().getParent();
            splitPane = (SplitPane) pane.getParent().getParent().getParent();

            parent.setCenter(null);

            AnchorPane anchorPane = new AnchorPane();
            anchorPane.getChildren().add(splitPane);
            AnchorPane.setLeftAnchor(splitPane, 0.0);
            AnchorPane.setTopAnchor(splitPane, 0.0);
            AnchorPane.setRightAnchor(splitPane, 0.0);
            AnchorPane.setBottomAnchor(splitPane, 0.0);

            anchorPane.setStyle(Theme.getStyleConst(Theme.MAIN_FXML));
            anchorPane.getStylesheets().add(String.valueOf(this.getClass().getResource("/Layouts/Styles/MainFXMLStyle.css")));

            fullscreenStage = new Stage();
            fullscreenStage.setScene(new Scene(anchorPane));
            fullscreenStage.show();
            fullscreenStage.setFullScreen(true);

            fullscreened = true;
            fullscreenButton.setText(IconFont.ICON_FULLSCREEN_RESTORE);

            parent.getScene().getWindow().hide();
        }
    }

    public void randomToggleButton(ActionEvent event) {
        random = randomToggleButton.isSelected();

        Odtwarzacz.getConfig().setProperty("random", String.valueOf(random));
        Odtwarzacz.getConfig().save();


        if (connection != null) {
            connection.sendMessage(randomToggleButton.isSelected() ? Connection.RANDOM_ON : Connection.RANDOM_OFF);
        }

        getPlaylist().setNextPlaylistIndex();
    }

    public void randomToggle() {
        randomToggleButton.fire();
    }

    public boolean isRepeat() {
        return repeat;
    }

    public boolean isRandom() {
        return random;
    }

    public void stop() {
        mediaPlayer.stop();
    }
}
