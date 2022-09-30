package utils;

import javafx.scene.control.Alert;
import javafx.stage.StageStyle;

import java.io.*;
import java.util.HashMap;
import java.util.Properties;

public class AppConfig {
    public static final String APP_NAME = "EmailTest";
    public static final String SUB_APP_NAME = "dev by M4NN3";
    public static final String EMAIL_PROPERTIES_FILEPATH = System.getProperty("user.home")
            + File.separator + "documents"
            + File.separator + "email_params.manne";
    public static final String OUTLOOK_DEF_HOST = "smtp.office365.com";
    public static final String GMAIL_DEF_HOST = "smtp.gmail.com";

    public static void toast(String title, String msg, Alert.AlertType alertTye){
        Alert alert = new Alert(alertTye);
        alert.setTitle(String.format("%s | %s", title, APP_NAME));
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.initStyle(StageStyle.UTILITY);
        alert.showAndWait();
    }
    public static Properties getEmailParams(){
        File storedProp = new File(EMAIL_PROPERTIES_FILEPATH);
        if (!storedProp.exists()) {
            return null;
        }
        Properties prop = new Properties();
        InputStream in = null;
        try {
            in = new FileInputStream(storedProp);
            prop.load(in);
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException io) {
                    io.printStackTrace();
                }
            }
        }
        return prop;
    }
    public static void saveEmailParams(String host, String port, String username, String pwd,
                                        String from, String to, String subject, String message){
        OutputStream outputStream = null;
        try{
            Properties prop = new Properties();
            outputStream = new FileOutputStream(new File(EMAIL_PROPERTIES_FILEPATH));
            prop.setProperty("host", host);
            prop.setProperty("port", port);
            prop.setProperty("username", username);
            prop.setProperty("pwd", pwd);
            prop.setProperty("from", from);
            prop.setProperty("to", to);
            prop.setProperty("subject", subject);
            prop.setProperty("msg", message);
            prop.store(outputStream, SUB_APP_NAME);

            System.out.println("Email params saved!!");
        }catch (Exception ex){
            System.out.println("Error saving params");
            ex.printStackTrace();
        }finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
