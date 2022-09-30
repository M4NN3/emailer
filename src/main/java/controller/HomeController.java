package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import utils.AppConfig;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.net.URL;
import java.util.Date;
import java.util.Properties;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    //<editor-fold desc="FXML vars">
    @FXML
    private Hyperlink link25;

    @FXML
    private Hyperlink link465;

    @FXML
    private Hyperlink link587;
    @FXML
    private Hyperlink linkOutlook;

    @FXML
    private Hyperlink linkGmail;

    @FXML
    private TextField txtSMTPServer;

    @FXML
    private TextField txtPort;

    @FXML
    private CheckBox chkAuth;

    @FXML
    private CheckBox chkSSL;

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private TextField txtFrom;

    @FXML
    private TextField txtTo;

    @FXML
    private TextField txtSubject;

    @FXML
    private TextArea txtMessage;

    @FXML
    private Button btnTest;
    //</editor-fold>
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadEmailParams();
        btnTest.setOnAction(event -> {
            //testEmail();
            if (validate()){
                AppConfig.toast("Empty values", "All values are required", Alert.AlertType.WARNING);
                return;
            }
            btnTest.setDisable(true);
            btnTest.setText("Testing email...");
            new Thread(this::testEmail).start();
        });
        linkGmail.setOnAction(event -> txtSMTPServer.setText(AppConfig.GMAIL_DEF_HOST));
        linkOutlook.setOnAction(event -> txtSMTPServer.setText(AppConfig.OUTLOOK_DEF_HOST));

        link25.setOnAction(event -> txtPort.setText("25"));
        link465.setOnAction(event -> txtPort.setText("465"));
        link587.setOnAction(event -> txtPort.setText("587"));


    }
    private void saveEmailParams(){
        AppConfig.saveEmailParams(txtSMTPServer.getText(), txtPort.getText(),txtUsername.getText(),txtPassword.getText(),
                txtFrom.getText(),txtTo.getText(),txtSubject.getText(),txtMessage.getText());
    }
    private void loadEmailParams(){
        Properties prop = AppConfig.getEmailParams();
        if (prop == null)
            return;
        txtSMTPServer.setText(prop.getProperty("host"));
        txtPort.setText(prop.getProperty("port"));
        txtUsername.setText(prop.getProperty("username"));
        txtPassword.setText(prop.getProperty("pwd"));
        txtFrom.setText(prop.getProperty("from"));
        txtTo.setText(prop.getProperty("to"));
        txtSubject.setText(prop.getProperty("subject"));
        txtMessage.setText(prop.getProperty("msg"));
    }
    private boolean validate(){
        return txtSMTPServer.getText().isEmpty() || txtPort.getText().isEmpty() ||
                txtUsername.getText().isEmpty() || txtPassword.getText().isEmpty() ||
                txtFrom.getText().isEmpty() || txtTo.getText().isEmpty() ||
                txtSubject.getText().isEmpty() || txtMessage.getText().isEmpty();
    }
    private void testEmail(){
        try {
            Properties prop = System.getProperties();
            prop.put("mail.smtp.auth", chkAuth.isSelected());
            prop.put("mail.smtp.starttls.enable", chkSSL.isSelected());
            prop.put("mail.smtp.host", txtSMTPServer.getText());
            prop.put("mail.smtp.port", txtPort.getText());
            Session session = Session.getInstance(prop, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                            txtUsername.getText(),
                            txtPassword.getText());
               }
            });
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(txtFrom.getText()));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(txtTo.getText()));
            message.setSubject(txtSubject.getText());
            String msg = txtMessage.getText();

            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(msg, "text/html; charset=utf-8");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);
            message.setContent(multipart);
            message.setSentDate(new Date());

            Transport.send(message);
            Platform.runLater(()->{
                AppConfig.toast("Ok","Email Sent Successfully!!", Alert.AlertType.INFORMATION);
                saveEmailParams();
            });
        }catch (Exception ex){
            Platform.runLater(()->{
                AppConfig.toast("Error!!",ex.getMessage(), Alert.AlertType.ERROR);
            });
        }
        finally {
            Platform.runLater(()->{
                btnTest.setText("Test email");
                btnTest.setDisable(false);
            });
        }
    }
}
