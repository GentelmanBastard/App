package sample;

import java.io.InputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


public class Controller {
    @FXML
    private TextArea log;
    @FXML
    private TextField username;
    @FXML
    private TextField pass;
    @FXML
    private TextField boxIp;
    public void onButtonClicked(){
        String host=boxIp.getText();
        String user=username.getText();
        String password=pass.getText();
        String command1="ls -ltr";
        try{

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            JSch jsch = new JSch();
            Session session=jsch.getSession(user, host, 22);
            session.setPassword(password);
            session.setConfig(config);
            session.setConfig("PreferredAuthentications",
                    "publickey,keyboard-interactive,password");
            session.connect();
            log.appendText("Connected");
//            System.out.println("Connected");

            Channel channel=session.openChannel("exec");
            // Rzutowanie elementu channel and klasę ChannelExec i uruchamiamy metodę setCommand
            ((ChannelExec)channel).setCommand(command1);
            channel.setInputStream(null);
            ((ChannelExec)channel).setErrStream(System.err);

            InputStream in=channel.getInputStream();
            channel.connect();
            byte[] tmp=new byte[1024];
            int i=in.read(tmp, 0, 1024);
            log.appendText(new String(tmp, 0, i));
//
//            while(true){
//                while(in.available()>0){
//                    int i=in.read(tmp, 0, 1024);
//                    if(i<0)break;
//                    System.out.print(new String(tmp, 0, i));
//                }
//                if(channel.isClosed()){
//                    System.out.println("exit-status: "+channel.getExitStatus());
//                    break;
//                }
//                try{Thread.sleep(1000);}catch(Exception ee){}
//            }
            channel.disconnect();
            session.disconnect();
            log.appendText("DONE");
//            System.out.println("DONE");
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}

