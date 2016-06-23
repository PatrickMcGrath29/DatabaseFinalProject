package cs3200.View;

import javax.swing.*;

/**
 * Interface for the View of this Database Connector
 */
public interface IView {

    /**
     * Makes the frame visible, also intializing any additional features for the specific frame
     */
    void initialize();


    void setPanel(JPanel panel);

}
