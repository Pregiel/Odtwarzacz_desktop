package odtwarzacz.Utils;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import odtwarzacz.Connection.Connection;

import java.awt.*;
import java.io.File;
import java.util.ResourceBundle;

public class Utils {
    public static String getDirectoryTree(File directory) {
        StringBuilder tree = new StringBuilder();
        File[] list = directory.listFiles();
        if (list != null) {
            for (File file : list) {
                tree.append(file.getAbsolutePath()).append(Connection.SEPARATOR);
            }
        }
        return tree.toString();
    }

    public static String getDriveList() {
        StringBuilder list = new StringBuilder();
        File[] rootDrive = File.listRoots();

        for (File sysDrive : rootDrive) {
//            list.append(sysDrive).append(FileSystemView.getFileSystemView().getSystemDisplayName(sysDrive)).append(Connection.SEPARATOR);

            list.append(sysDrive).append(Connection.SEPARATOR);
        }

        return list.toString();
    }

    public static Image scale(Image source, int targetWidth, int targetHeight, boolean preserveRatio) {
        ImageView imageView = new ImageView(source);
        imageView.setPreserveRatio(preserveRatio);
        imageView.setFitWidth(targetWidth);
        imageView.setFitHeight(targetHeight);
        return imageView.snapshot(null, null);
    }

    public static String getExtension(String file) {
        return file.substring(file.lastIndexOf(".") + 1);
    }

    private static ResourceBundle translationsBundle;

    public static ResourceBundle getTranslationsBundle() {
        return translationsBundle;
    }

    public static void initTranslationsBundle() {
        translationsBundle = ResourceBundle.getBundle("Translations.MessagesBundle", MyLocale.getLocale(),
                ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES));
    }

    public static String getString(String key) {
        return translationsBundle.getString(key);
    }

    public static float[] rgbToHsb(String rgb) {
        return rgbToHsb(Integer.valueOf(rgb.substring(1, 3), 16),
                Integer.valueOf(rgb.substring(3, 5), 16),
                Integer.valueOf(rgb.substring(5, 7), 16));
    }

    private static float[] rgbToHsb(int red, int green, int blue) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(red, green, blue, hsb);
        return hsb;


    }

    public static void print(Object... value) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Object s : value) {
            stringBuilder.append(s.toString()).append(" ");
        }
        System.out.println(stringBuilder.toString());
    }

    public static void println(Object... value) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Object s : value) {
            stringBuilder.append(s.toString()).append("\n");
        }
        System.out.println(stringBuilder.toString());
    }
}
