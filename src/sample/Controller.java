package sample;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import sample.net.Communicator;
import sample.net.TestServer;

import java.awt.event.ActionEvent;
import java.io.IOException;

public class Controller implements Communicator {
    @FXML
    public TextField textField;
    @FXML
    public TextArea textArea;

    TestServer testServer;
    public Controller()  {

    }
    @FXML
    public  void  initialize(){
        try {
            testServer = new TestServer(this);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    @Override
    public void getMessage(String msg) {
        textArea.appendText(msg);
    }
    @FXML
    public void onBtnSend(javafx.event.ActionEvent actionEvent) {
        testServer.sendAll(textField.getText());
    }

    @FXML
    public void exitApplication(ActionEvent event) {
        Platform.exit();
    }

    public void stop(){
        System.out.println("Stage is closing");
        // Save file
    }
}
