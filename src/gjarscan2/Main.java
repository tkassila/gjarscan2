package gjarscan2;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.web.WebView;

import java.io.File;
import java.util.Optional;

public class Main extends Application {

//    static String [] runArgs;
    static final int MINTABINDEX = -1;
    private int tabIndex = MINTABINDEX;
    protected static GJarSanTab currentGJarSanTab = null;
    static final boolean bDebug = false;

    @FXML
    Button newJarScanButton;
    @FXML
    Button removeJarScanButton;
    @FXML
    Button copyJarScanButton;
    @FXML
    TabPane tabPaneJarScan;
    @FXML
    Button buttonTogleCurrentTabSplitPanel;
    @FXML
    MenuItem aboutMenuItem;
    @FXML
    MenuItem buttonQuit;
    @FXML
    MenuItem helpMenuItem;

    @FXML
    public void helpMenuItemClicked()
    {
        WebView webView = new WebView();
        File helpFile = new File("help.html");
        String filURI = "file://" +helpFile.getAbsolutePath(); // .toURI().toString().replace("file:/", "file:///");
        /*
        if (File.separatorChar == '\\')
            filURI = filURI.replaceAll("/{1,1}","\\\\");
        //webView.getEngine().load("file://C:\\java\\project\\javafx\\gjarscan2\\src\\gjarscan2\\help.html"); // filURI
         */
        webView.getEngine().load(filURI); //
        // Dialog helpDialog = new Dialog();
        Alert helpDialog = new Alert(AlertType.INFORMATION);
        helpDialog.setTitle("Help");
        // alert.setHeaderText("GJarScan application 2.0");
        VBox vBox = new VBox(webView);
        // Scene scene = new Scene(vBox, 960, 600);
        helpDialog.getDialogPane().setContent(webView);
        helpDialog.show();
    }

    @FXML
    public void buttonQuitClicked()
    {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Exit application");
        String s = "Confirm to quit the application !";
        alert.setContentText(s);
        Optional<ButtonType> result = alert.showAndWait();
        if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
            System.exit(0);
        }
    }

    @FXML
    public void aboutMenuItemClicked()
    {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("GJarScan application 2.0");
        String s ="The application is used to find jar files below a given file directory where given class name exists.\nThis version of app is made JavaFx library.\n(c) Tuomas Kassila";
        alert.setContentText(s);
        alert.show();
    }

    @FXML
    public void newJarScanButtonClicked(){
        if (Main.bDebug)
            System.out.println("newJarScanButtonClicked");
        this.tabIndex++;
        GJarSanTab gJarSanTab = new  GJarSanTab(this.tabIndex);

        BackgroundFill background_fill = new BackgroundFill(Color.PINK,
                CornerRadii.EMPTY, Insets.EMPTY);
        Background background = new Background(background_fill);
      //  this.tabPaneJarScan.setBackground(background);
        this.tabPaneJarScan.getTabs().addAll(gJarSanTab);
        this.tabPaneJarScan.getSelectionModel().selectLast();
        this.removeJarScanButton.setDisable(false);
        this.copyJarScanButton.setDisable(false);
        this.buttonTogleCurrentTabSplitPanel.setDisable(false);
    }

    @FXML
    public void copyJarScanButtonClicked()
    {
        if (currentGJarSanTab == null)
            return;
        Object oldcurrentGJarSanTab = currentGJarSanTab;

        // get old search values from currentGJarSanTab at first:
        String strTextSearchDir = currentGJarSanTab.getGJarScanController().textfieldSearchDir.getText();
        String strTtextfieldSearchClass = currentGJarSanTab.getGJarScanController().textfieldSearchClass.getText();
        int indSelectedSearchType = currentGJarSanTab.getGJarScanController().searchType.getSelectionModel().getSelectedIndex();
        boolean checkboxNosubdir = currentGJarSanTab.getGJarScanController().checkboxNosubdir.isSelected();
        boolean checkboxNoSearchList = currentGJarSanTab.getGJarScanController().checkboxNoSearchList.isSelected();
        boolean checkboxHelp = currentGJarSanTab.getGJarScanController().checkboxHelp.isSelected();
        boolean checkboxNoSearchList2 = currentGJarSanTab.getGJarScanController().checkboxNoSearchList2.isSelected();
        boolean checkboxZip = currentGJarSanTab.getGJarScanController().checkboxZip.isSelected();
        boolean checkboxRemoveEmptyLines = currentGJarSanTab.getGJarScanController().checkboxRemoveEmptyLines.isSelected();

        // then call newtabl method:
        this.newJarScanButtonClicked();
        // at last: set old search values into just created currenttab:
        if (oldcurrentGJarSanTab != currentGJarSanTab)
        {
            currentGJarSanTab.getGJarScanController().textfieldSearchDir.setText(strTextSearchDir);
            currentGJarSanTab.getGJarScanController().textfieldSearchClass.setText(strTtextfieldSearchClass);
            if (indSelectedSearchType != -1)
                currentGJarSanTab.getGJarScanController().searchType.getSelectionModel().select(indSelectedSearchType);
            currentGJarSanTab.getGJarScanController().checkboxNosubdir.setSelected(checkboxNosubdir);
            currentGJarSanTab.getGJarScanController().checkboxNoSearchList.setSelected(checkboxNoSearchList);
            currentGJarSanTab.getGJarScanController().checkboxHelp.setSelected(checkboxHelp);
            currentGJarSanTab.getGJarScanController().checkboxNoSearchList2.setSelected(checkboxNoSearchList2);
            currentGJarSanTab.getGJarScanController().checkboxZip.setSelected(checkboxZip);
            currentGJarSanTab.getGJarScanController().checkboxRemoveEmptyLines.setSelected(checkboxRemoveEmptyLines);
        }
    }

    @FXML
    public void removeJarScanButtonClicked(){
        if (Main.bDebug)
            System.out.println("removeJarScanButtonClicked");
        if (currentGJarSanTab != null)
        {
            this.tabPaneJarScan.getTabs().removeAll(currentGJarSanTab);
            if (this.tabPaneJarScan.getTabs().size() < 1) {
                Main.currentGJarSanTab = null;
                this.removeJarScanButton.setDisable(true);
                this.copyJarScanButton.setDisable(true);
                this.tabIndex = MINTABINDEX;
                this.buttonTogleCurrentTabSplitPanel.setDisable(true);
            }
            else {
                this.tabPaneJarScan.getSelectionModel().selectLast();
                Main.currentGJarSanTab = (GJarSanTab) this.tabPaneJarScan.getSelectionModel().getSelectedItem();
                this.tabIndex = getHighestTabValue(this.tabPaneJarScan.getSelectionModel());
            }
        }
        else {
            this.removeJarScanButton.setDisable(true);
            this.copyJarScanButton.setDisable(true);
            this.tabIndex = MINTABINDEX;
        }
    }

    private int getHighestTabValue(javafx.scene.control.SingleSelectionModel<Tab> model)
    {
        if (model.isEmpty())
            return MINTABINDEX;
        int ret = this.tabPaneJarScan.getSelectionModel().getSelectedIndex();
        if (ret < 0)
            ret = MINTABINDEX;
        int currTabInt = 0;
        if (ret > MINTABINDEX && this.tabPaneJarScan.getTabs().size() == 1)
            return ret;
        for (Tab tab : this.tabPaneJarScan.getTabs()) {
            try {
                currTabInt = Integer.parseInt(tab.getText());
                if (currTabInt > ret)
                    ret = currTabInt;
            }catch (Exception e){
                    e.printStackTrace();
            }
        }
        return ret;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("gjarscan2.fxml"));
        primaryStage.setTitle("Search a class inside of jar or .zip files");
        Scene scene = new Scene(root, 950, 640);
        // Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

 //   @Override
    // public void initialize(URL location, ResourceBundle resources) {
    public void init() {
    }

    public void initialize()
    {
        /*
        this.checkBoxResultDialog.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (Main.bDebug)
                System.out.println(e.getStateChange() == ItemEvent.SELECTED
                        ? "SELECTED" : "DESELECTED");
            }
        });

         */

        this.buttonTogleCurrentTabSplitPanel.setDisable(true);
        this.removeJarScanButton.setDisable(true);
        this.copyJarScanButton.setDisable(true);
        this.tabPaneJarScan.getTabs().clear();
        this.tabPaneJarScan.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> ov, Tab tabOld, Tab tabNew) {
              //  System.out.println("Tab Selection changed");
                Main.currentGJarSanTab = (GJarSanTab)tabNew;
            }
        });
       this.newJarScanButtonClicked();
    };

    @FXML
    public void togleSplitPanelOfcurrentGJarSanTab()
    {
        if (this.currentGJarSanTab == null)
            return;

        this.currentGJarSanTab.togleSplitPanel();
    }

    public static void main(String[] args) {
     //   Main.runArgs = args;
        launch(args);
    }

    /* not ready:
    static String getClassPathRunArg()
    {
        String [] args = Main.runArgs;
        int max = args.length;
        boolean founded = false;
        String classpathvalue = null;

        for(int i = 0; i < max; i++ )
        {
            if (args[i] == null || args[i].trim().length() == 0)
                continue;
            if (founded)
            {
                classpathvalue = args[i];
                break;
            }
            else
            if (args[i].toLowerCase().contains("-cp")) {
                founded = true;
                continue;
            }
        }
        return classpathvalue;
    }
     */
}
