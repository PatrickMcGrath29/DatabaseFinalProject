package cs3200;

import cs3200.Controller.DatabaseController;
import cs3200.Controller.IController;
import cs3200.Model.FrontEndModel;
import cs3200.View.GUIFrame;

import java.io.InputStreamReader;

/**
 * Main Class for the Database Connector
 */
public class Main {

    public static void main(String[] args) {
        IController controller = new DatabaseController(new GUIFrame(), new FrontEndModel());
        controller.run();
    }

}
