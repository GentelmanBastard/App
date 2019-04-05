package sample;

import com.jcraft.jsch.*;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import sun.nio.ch.ChannelInputStream;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    @FXML
    private TextArea log;
    @FXML
    private TextField boxIp;
    @FXML
    private TextField username;
    @FXML
    private TextField pass;
    @FXML
    private RadioButton monitoring;
    @FXML
    private RadioButton startover;
    @FXML
    private RadioButton processing;
    @FXML
    private RadioButton time;

    public void onButtonClicked() {
        String host = "172.30.176.114";  //boxIp.getText();
        String user = "root"; //username.getText();
        String password = "performance"; //pass.getText();
        String command1 = "ls -ltr";
        StringBuilder outputBuffer = new StringBuilder();
        StringBuilder errorBuffer = new StringBuilder();

        List<String> lista = new ArrayList<String>();
        lista.add("su - prosys");
        lista.add(command1);
        lista.add("mxvision");

//        if(monitoring.isSelected()){
//            command1 = "mxvision";
//        }
//        else if(startover.isSelected()){
//            command1 = "startover";
//        }
//        else if(processing.isSelected()){
//            command1 = "processing";
//        }
//        else if(time.isSelected()){
//            command1 = "ls -ltr";
//        }
        Task<Void> timer = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }
                return null;
            }
        };
        try {
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, 22);
            session.setPassword(password);
            session.setConfig(config);
            session.setConfig("PreferredAuthentications",
                    "publickey,keyboard-interactive,password");
            session.connect();
            log.appendText("Connected\n");

            // Rzutowanie elementu channel and klasę ChannelExec i uruchamiamy metodę setCommand
//            if(command1.equals("startover") || command1.equals("processing") || command1.equals("time")) {
//                channel.disconnect();
//                session.disconnect();
//                log.appendText("DONE\n");
//                return;
//            }
            for (String command : lista) {
                log.appendText("Command executed: " + command + "\n");
                Channel channel = session.openChannel("exec");
//                channel.setInputStream(null);
//                ((ChannelExec) channel).setErrStream(System.err);
                ((ChannelExec) channel).setCommand(command);
                InputStream in = channel.getInputStream();
                InputStream err = channel.getExtInputStream();
                channel.connect();
                byte[] tmp = new byte[1024];
                while (true) {
                    while (in.available() > 0) {
                        int i = in.read(tmp, 0, 1024);
                        if (i < 0) break;
                        outputBuffer.append(new String(tmp, 0, i));
                    }
                    while (err.available() > 0) {
                        int i = err.read(tmp, 0, 1024);
                        if (i < 0) break;
                        errorBuffer.append(new String(tmp, 0, i));
                    }
                    if (channel.isClosed()) {
                        if ((in.available() > 0) || (err.available() > 0)) continue;
                        System.out.println("exit-status: " + channel.getExitStatus());
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (Exception ee) {
                    }
                }
                System.out.println("output: " + outputBuffer.toString());
                log.appendText("DONE\n");
                channel.disconnect();
            }
            session.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

