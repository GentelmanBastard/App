package sample;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.InputStream;

public class Controller {
    @FXML
    private TextArea log;
    @FXML
    private TextField username;
    @FXML
    private TextField pass;
    @FXML
    private TextField boxIp;

    public void onButtonClicked() {
        String host = boxIp.getText();
        String user = username.getText();
        String password = pass.getText();
        String command1 = "ls -ltr";
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
            Channel channel = session.openChannel("exec");
            // Rzutowanie elementu channel and klasę ChannelExec i uruchamiamy metodę setCommand
            ((ChannelExec) channel).setCommand(command1);
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);
            InputStream in = channel.getInputStream();
            channel.connect();
            byte[] tmp = new byte[1024];
            int i = in.read(tmp, 0, 1024);
            log.appendText(new String(tmp, 0, i));
            channel.disconnect();
            session.disconnect();
            log.appendText("DONE\n");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

