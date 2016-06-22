package cs3200;

import cs3200.Controller.DatabaseController;
import cs3200.Model.FrontEndModel;
import cs3200.View.GUIFrame;

/**
 * Main Class for the Database Connector
 */
public class Main {

    public static void main(String[] args) {
        DatabaseController controller = new DatabaseController(new FrontEndModel());
        controller.setView(new GUIFrame(controller));
        controller.run();
    }

}
