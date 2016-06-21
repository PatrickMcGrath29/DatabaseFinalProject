package cs3200.View;

/**
 * Interface for the View of this Database Connector
 */
public interface IView {


    /**
     * Initalizes the current view for the user.
     */
    void initialize();

    /**
     * Initalizes the first view, getting the username and password for the user. If the user
     * does not have an account already then they will be able to create one.
     */
    void getUserPass();


}
