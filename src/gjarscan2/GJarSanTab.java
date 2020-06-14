package gjarscan2;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GJarSanTab extends Tab {

    // buttonSearchDir
    private int ind = 0;
    private boolean bExecuting = false;
    private GJarScanController controller = new GJarScanController();
   // FileChooser fileChooserForSearchDir = new FileChooser();

    public GJarSanTab(int p_ind)
    {
        super("" +p_ind);
        this.ind = p_ind;
        try {
            // Parent tabConten = FXMLLoader.load(getClass().getResource("tabgjarscan.fxml"));
            BorderPane tab_pane = new BorderPane();

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("tabgjarscan.fxml"));
            // fxmlLoader.setRoot(tab_pane);
            fxmlLoader.setController(controller);
            Parent tabConten = fxmlLoader.load();

           // grid.setMaxSize(-1, -1);
           // tab_pane.setMaxSize(-1, -1);
          //  grid.prefWidthProperty().bind(hBox.widthProperty());
          //  ((BorderPane)tabConten).prefWidthProperty().bind(tab_pane.widthProperty());
           // tab_pane.getChildren().addAll(tabConten);
         //   tab_pane.prefWidthProperty().bind(this.getProperties().widthProperty());
          // tab_pane.setPrefSize(25000, 25000);
          //  ((AnchorPane
            //        )tabConten).setPrefSize(25000, 25000);
            // setContent(new Rectangle(200,200, Color.LIGHTSTEELBLUE));
          //  tabConten.

            this.setContent(tabConten );
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void togleSplitPanel()
    {
        this.controller.togleSplitPanel();
    }
}
