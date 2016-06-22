package cs3200.View;

/**
 * Interface for the View of this Database Connector
 */
public interface IView {

    /**
     * Makes the frame visible, also intializing any additional features for the specific frame
     */
    void initialize();


    void setPanel(GUIFrame.PanelType panel);

}
