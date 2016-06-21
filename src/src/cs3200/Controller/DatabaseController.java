package cs3200.Controller;

import cs3200.Model.IModel;
import cs3200.View.IView;

import java.util.Formatter;
import java.util.Scanner;

/**
 * Created by patrickmcgrath on 6/21/16.
 */
public class DatabaseController implements IController {

    private String username, password;
    private Scanner sc;
    private Formatter ap;
    private IView view;
    private IModel model;

    public DatabaseController(Appendable ap, Readable rd) {
        this.sc = new Scanner(rd);
        this.ap = new Formatter(ap);

        this.ap.format("Enter the username for the DB\n");
        this.username = sc.nextLine();
        this.ap.format("Enter the password for the DB\n");
        this.password = sc.nextLine();
    }



    @Override
    public void initialize(IView view, IModel model) {
        this.view = view;
        this.model = model;

        this.view.initialize();
        this.view.getUserPass();

    }



}
