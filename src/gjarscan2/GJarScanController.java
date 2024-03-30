package gjarscan2;

import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Clipboard;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;
import javafx.scene.text.Font;

import java.io.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.AudioSystem;

public class GJarScanController {
    final Clipboard clipboard = Clipboard.getSystemClipboard();
    final ClipboardContent content = new ClipboardContent();
    private static final String NEWLINE = System.getProperty("line.separator");
    private ObservableList<String> searchTypeList = FXCollections.observableArrayList(
      "-class", "-package", "-files"
    );

    static final int ROW_HEIGHT = 20;
    // private Font defaultFont = Font.font("Verdana", FontWeight.EXTRA_BOLD, 14);
    private Font defaultFont = Font.font(18);

    private class BooleanValue {
        private boolean bValue = false;
        public void setBoolean(boolean p_value) { this.bValue = p_value; }
        public boolean booleanValue() { return this.bValue; }
    }

    protected final BooleanValue isJarScanSearchOk = new BooleanValue();

    /**
        This class will spicify how a listvies controll 'row' should a cell visually show. Is a subprocess to
        scan jar files successfully or not. And where found class row begins, find next search text within a founded
        jar result, previous and next buttons are also give 'commands' into this class instance indirectly by setting
        userData of listResult ListCell instances. To this setting is used in a search founded instancies of the class
       ResultTextPosition.
     */
   /*static */ class CellFactory extends ListCell<ListResultData>
    {
        private BooleanValue isJarScanSearchOk;
        private Pattern patternStart = Pattern.compile("\\d+\\s+start");
        /*
        private BackgroundFill startBGFill = new BackgroundFill(Color.YELLOW
                , new CornerRadii(1),
                new Insets(0.0,0.0,0.0,0.0));// or null for the padding
         */
        //then you set to your node or container or layout
        //myContainer.setBackground(new Background(myBF));
        // private Font defaultFont = Font.font("Verdana", FontWeight.EXTRA_BOLD, 14);
        public CellFactory(BooleanValue p_isJarScanSearchOk, ListView p_listview)
        {
            isJarScanSearchOk = p_isJarScanSearchOk;
            listview = p_listview;
        }

        private ListView listview;

        @Override
        public void updateItem(ListResultData item, boolean empty) {
            /*
            if (item != null && item.contains("start"))
            {
            if (Main.bDebug)
                System.out.println("start");
            }
             */
            // super.updateItem(item, empty);
            this.setFont(defaultFont);

            if (!empty)
            {
                Object objUserData = this.getUserData();
                this.setTextFill(Color.BLACK); // set TextColor Black
                if (item == null | item.toString().trim().length() == 0) {
                    this.setStyle("-fx-font-weight: normal");    //(2)
               //     this.setFont(defaultFont);
                }
                else
                if (!isJarScanSearchOk.booleanValue()) {
                    this.setStyle(
                            "-fx-font-weight: bold;"); // -fx-cell-size: " +ROW_HEIGHT +"px"
                 //   this.setFont(defaultFont);
                }
                else
                {
                    if (item == null | item.toString().trim().length() == 0) {
                        this.setStyle("-fx-font-weight: normal");    //(2)
                   //     this.setFont(defaultFont);
                    }
                    else {
                        ResultTextPosition searchPostion = item.getResultTextPosition();
                        if (searchPostion != null)
                        {
                            if (searchPostion.isSearchCursor())
                            {
                                this.setStyle("-fx-font-weight: bold;"); // -fx-cell-size: " +ROW_HEIGHT +"px"
                                this.setTextFill(Color.WHITE);
                                this.setStyle("-fx-background-color: black");
                     //           this.setFont(defaultFont);
                            }
                            else {
                                this.setTextFill(Color.WHITE);
                               // super.setTextFill(Color.RED); // set TextColor
                                // super.setStyle("-fx-font-weight: bold; -fx-cell-size: " +ROW_HEIGHT +"px;");
                                //super.setTextFill(Color.YELLOW);
                                this.setStyle("-fx-background-color: blue");
                       //         this.setFont(defaultFont);
                            }
                        }
                        else
                        if (item.toString().trim().startsWith("Class:") || item.toString().trim().startsWith("Library Path:")) {
                            this.setStyle("-fx-font-weight: bold;"); // -fx-cell-size: " +ROW_HEIGHT +"px"
                         //   this.setFont(defaultFont);
                        }
                        else
                        {
                            boolean foundMatch = false;
                            Matcher matcher;
                            try {
                                matcher = patternStart.matcher(item.toString());
                                foundMatch = matcher.find();
                            } catch (PatternSyntaxException ex) {
                                // Syntax error in the regular expression
                                ex.printStackTrace();
                                foundMatch = false;
                            }
                            if (!foundMatch) {
                                this.setStyle("-fx-font-weight: normal");
                           //     this.setFont(defaultFont);
                            }
                            else { // setBackground(new Background(startBGFill));
                                this.setStyle("-fx-background-color: lightgray"); // darkgray darkseagreen
                             //   this.setFont(defaultFont);
                            }
                        }
                    }
                }
             //   super.setStyle("-fx-cell-size: 8px;");
                /*
                if (item != null && item.equals("GHI")) {
                    super.setStyle("-fx-font-weight: bold");    //(1)
                }
                 */
                if (item != null)
                    this.setText(new String(item.toString()));
                else {
                    this.setText(null);
                    setGraphic(null);
                }
                super.updateItem(item, empty);
            }
            else{
                setGraphic(null);
                super.updateItem(item, empty);
                // super.setStyle("-fx-font-weight: normal");    //(2)
                // this.setFont(defaultFont);
            }
            /*
            if (item != null)
                super.setText(item.toString());
            else
                super.setText(null);
             */
        }
    }

    @FXML
    Button buttonSearchDir;
    @FXML
    TextField textfieldSearchDir;
    @FXML
    GridPane gridpane;
    @FXML
    BorderPane anchorPaneSearchDir;
    @FXML
    ComboBox searchType;
    @FXML
    TextField textfieldSearchClass;
    @FXML
    ListView listResult;
    @FXML
    Button buttonCancelExecution;
    @FXML
    Button buttonExecution;
    @FXML
    CheckBox checkboxZip;
    @FXML
    CheckBox checkboxHelp;
    @FXML
    CheckBox checkboxNoSearchList;
    @FXML
    CheckBox checkboxNosubdir;
    @FXML
    CheckBox checkboxNoSearchList2;
    @FXML
    CheckBox checkboxRemoveEmptyLines;
    @FXML
    Button buttonSearchResult;
    @FXML
    Button buttonNext;
    @FXML
    Button buttonPrevious;
    @FXML
    Label labelMsg;
    @FXML
    SplitPane splitPaneResult;
    @FXML
    TextField textfieldSearhFromResult;
    @FXML
    TitledPane titledPaneSearhFromResult;
    @FXML
    VBox anchorPaneUppder;
    @FXML
    VBox anchorPaneLower;
    @FXML
    private CheckBox checkBoxMaven;
    @FXML
    private CheckBox checkBoxGradle;

    private boolean bProssesRestarted = false;
    private boolean bExecuted = false;
    private final JarScanProcesses processes = new JarScanProcesses();
    final ObservableList listResultItems = FXCollections.observableArrayList("1", "2", "3");
    private String currentDuppelClickedItem = null;
    private Double dZeroDiverPosition = null;
    private Double dHalfDiverPosition = null;
    private DirectoryChooser directoryChooser = new DirectoryChooser();
    private File selectedSearhDirectory = null;
    private ListResultData [] arrResultTextRows = null;
    private ResultTextPosition [] arrSearchResults = null;
    private ResultTextPosition currentResultTextPositionCursor = null;
    private ResultTextPosition firstResultTextPosition = null;
    private ResultTextPosition lastResultTextPosition =  null;
    private int iIndextitledPaneSearhFromResult = -1;

    protected static boolean bUnderCheckBoxChange = false;

    private String getMavenDependencyOf(String currentDuppelClickedItem, boolean bGradle)
    {
        String ret = currentDuppelClickedItem;
        if (ret != null && ret.trim().length() > 0)
        {
            String strRegex = File.separatorChar == '/' ? "/" : "\\\\";
            String [] path_dirs = ret.split(strRegex);
            if (path_dirs != null && path_dirs.length>0)
            {
                int size = path_dirs.length;
                int lastIndex = size -1;
                int lastIndex3 = size -3;
                int lastIndex4 = size -4;
                int lastIndex5 = size -5;
                String lastFileName = path_dirs[lastIndex];
                String importPathValue = path_dirs[lastIndex3];
                String importPathValue4 = path_dirs[lastIndex4];
                String importPathValue5 = path_dirs[lastIndex5];
                int lastPoint = lastFileName.lastIndexOf(".");
                int lastStrike = lastFileName.lastIndexOf("-");
                String strGroup = "";
                if (lastPoint > -1 && lastStrike > -1)
                {
                    strGroup = importPathValue5 +"." +importPathValue4;
                }
                String strArtifact = importPathValue;
                String strVersion = lastFileName.substring(lastStrike +1, lastPoint);
                String strTemplate = "<dependency>\n<groupId>%s</groupId>\n" +
                        "<artifactId>%s</artifactId>\n<version>%s</version>\n</dependency>";
                if (bGradle)
                { //  implementation group: 'com.ibm.icu', name: 'icu4j', version: '74.2'
                    strTemplate = "implementation group: '%s', name: '%s', version: '%s'";
                }
                ret = String.format(strTemplate, strGroup, strArtifact, strVersion);

            }
        }
        return ret;
    }

    private String getJModPathOf(String selected)
    {
        String ret = selected;
        if (selected != null && selected.trim().length()>0)
        {
            int ind = selected.indexOf(": ");
            ret = selected.substring(ind+2).trim();
        }
        return ret;
    }

    @FXML
    public void initialize() {
        if (Main.bDebug)
            System.out.println("initialize");
        System.out.println("java.version=" +System.getProperty("java.version"));
        System.out.println("javafx.version=" +System.getProperty("javafx.version"));

        // ectoryChooser.setFileHidingEnabled(false);
      //  this.listResult.setFont(defaultFont);

        checkBoxMaven.selectedProperty().addListener(
              (ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
                  if (!bUnderCheckBoxChange && new_val && checkBoxGradle.isSelected())
                      Platform.runLater(new Runnable() {
                          @Override
                          public void run() {
                              bUnderCheckBoxChange = true;
                              checkBoxGradle.setSelected(false);
                              bUnderCheckBoxChange = false;
                          }
                      });
              });

        checkBoxGradle.selectedProperty().addListener(
                (ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
                    if (!bUnderCheckBoxChange && new_val && checkBoxMaven.isSelected())
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                bUnderCheckBoxChange = true;
                                checkBoxMaven.setSelected(false);
                                bUnderCheckBoxChange = false;
                            }
                        });
                });

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                textfieldSearchClass.requestFocus();
            }
        });
        int iChildrenSize = anchorPaneUppder.getChildren().size();
        iIndextitledPaneSearhFromResult = anchorPaneUppder.getChildren().indexOf(titledPaneSearhFromResult);
        this.listResult.setFixedCellSize(30);
        this.listResult.setStyle("-fx-font-size: 16px");
        this.buttonCancelExecution.setDisable(true);
        this.checkBoxMaven.setDisable(true);
        this.checkBoxGradle.setDisable(true);
        this.textfieldSearhFromResult.setText(" start");
       // this.buttonExecution.setDisable(true);
        this.buttonNext.setDisable(true);
        this.buttonPrevious.setDisable(true);
        this.buttonSearchResult.setDisable(true);

      // this.listResult.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        /*
        if (event.getButton() == MouseButton.PRIMARY
                        && event.getClickCount() == 2 /* &&
                     (event.getTarget() instanceof LabeledText || ((GridPane) event.getTarget()).getChildren().size() > 0
                     ) ) {
         */

        this.directoryChooser.setTitle("Select jar search directory (linux: cntrl+h to show hidden files)");

        this.listResult.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY
                        && event.getClickCount() == 2 )
                {
                    currentDuppelClickedItem = null;
                    Object selected = listResult.getSelectionModel().getSelectedItem();
                    if (selected != null) {
                        boolean bJarPathSeeked = true;
                        currentDuppelClickedItem = getJarPathValue(selected.toString());
                        if (currentDuppelClickedItem == null)
                        {
                            bJarPathSeeked = false;
                            currentDuppelClickedItem = getClassValue(selected.toString());
                        }
                        if (currentDuppelClickedItem == null && selected.toString().endsWith(".jmod"))
                            currentDuppelClickedItem = getJModPathOf(selected.toString());

                        if (currentDuppelClickedItem != null) {
                            if (bJarPathSeeked && (checkBoxMaven.isSelected() || checkBoxGradle.isSelected()))
                                currentDuppelClickedItem = getMavenDependencyOf(currentDuppelClickedItem, checkBoxGradle.isSelected());
                            {
                                content.putString(currentDuppelClickedItem);
                                clipboard.setContent(content);
                            }
                            /*
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    Toolkit.getDefaultToolkit()
                                            .getSystemClipboard()
                                            .setContents(
                                                    new StringSelection(new String(currentDuppelClickedItem)),
                                                    null
                                            );

                                }
                            });
                            */

                            if (bJarPathSeeked)
                            {
                                if (checkBoxMaven.isSelected())
                                    setLabelMsg("A maven dependency xml copied into clipboard");
                                else
                                if (checkBoxGradle.isSelected())
                                    setLabelMsg("A gradle implementation copied into clipboard");
                                else
                                    setLabelMsg("A jar file path copied into clipboard");
                            }
                            else
                            {
                                if (selected.toString().endsWith(".jmod"))
                                    setLabelMsg("A jmod path copied into clipboard");
                                else
                                    setLabelMsg("A class copied into clipboard");
                            }
                        }
                        else
                            setLabelMsg("");
                    }

                }
                else
                if (event.getButton() == MouseButton.SECONDARY
                        && event.getClickCount() == 1 ) {
                    // System.out.println("second button clie");
                    Object selected = listResult.getSelectionModel().getSelectedItem();
                    if (selected != null) {
                        if (!isRighJarVersion()) {
                            setLabelMsg("You have not the right java version >= java 9 and after!");
                            return;
                        }

                        boolean bJarPathSeeked = true;
                        currentDuppelClickedItem = getJarPathValue(selected.toString());
                        if (currentDuppelClickedItem != null) {
                            String strScanModuleName = getScanModuleName(currentDuppelClickedItem, false);
                            if (strScanModuleName != null && strScanModuleName.trim().length()>0)
                            {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {

                                        Dialog<String> dialog = new Dialog<String>();
                                        //Setting the title
                                        dialog.setTitle("Describe module name in selected jar");
                                        ButtonType type = new ButtonType("Close", ButtonType.NO.getButtonData());
                                        //Setting the content of the dialog
                                        TextArea ta = new TextArea(strScanModuleName);
                                        ta.setEditable(false);
                                        Pane pane = new Pane();
                                        pane.getChildren().add(ta);
                                        ScrollPane sp = new ScrollPane(ta);
                                        sp.setContent(pane);
                                        dialog.getDialogPane().setContent(sp);
                                        //Adding buttons to the dialog pane
                                        dialog.getDialogPane().getButtonTypes().add(type);
                                        //Setting the label
                                        // Text txt = new Text("Click the button to show the dialog");
                                        // Font font = Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 12);
                                        // txt.setFont(font);
                                        dialog.showAndWait();
                                        //Creating a vbox to hold the button and the label
                                        // HBox pane = new HBox(15);
                                        //Setting the space between the nodes of a HBox pane
                                        // pane.setPadding(new Insets(50, 150, 50, 60));
                                        // pane.getChildren().addAll(txt, button);

                                    }
                                });
                            }
                        }
                    }
                }//your code here
            }
        });

        /*
        this.listResultItems.addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                listResult.setPrefHeight(listResultItems.size() * ROW_HEIGHT + 2);
                listResult.setPrefHeight(listResultItems.size() * ROW_HEIGHT + 2);
            }
        });
         */

        processes.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
                System.out.println("done:" /* + t.getSource().getValue() */);
                if (bProssesRestarted)
                    return;
                String strResult = (String)t.getSource().getValue();
                listResult.getItems().clear();
                listResult.getItems().addAll(strResult.split("(\n|$)")); // TODO: TARKISTA TAMA!
                buttonExecution.setDisable(false);
                buttonCancelExecution.setDisable(true);
                checkBoxMaven.setDisable(true);
                checkBoxGradle.setDisable(true);
            }
        });

            listResult.setCellFactory(new Callback<ListView<ListResultData>, ListCell<ListResultData>>() {
            @Override
            public ListCell<ListResultData> call(ListView<ListResultData> list) {
                return new CellFactory(isJarScanSearchOk, listResult);
            }
        });

        /*
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(25);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(75);
        gridpane.getColumnConstraints().addAll(col1,col2);
         */

/*
        gridpane.setVgap(1);
        */
        // listResult.getItems().addAll("dfslkö s slfödsls dkdfdfdf lkffkdsf dsfffdsfdfdfddfdfdfdfdfdfsdfds ds df fdsfdsfdlfdlfdfldl dldsfldsf dsldlfdlfdlfdslfdlf");
        // create a background fill
       // BackgroundFill background_fill = new BackgroundFill(Color.PINK,
         //       CornerRadii.EMPTY, Insets.EMPTY);

        /*
        // create Background
        Background background = new Background(background_fill);
        anchorPaneSearchDir.setBackground(background);
        */
       // gridpane.setVgap(3);
       // gridpane.setPadding(new Insets(10, 10, 10, 10));
        this.searchType.setItems(this.searchTypeList);
        this.searchType.getSelectionModel().selectFirst();
        /*
        gridpane.prefWidth(anchorPaneSearchDir.getPrefHeight());
        gridpane.prefWidthProperty().bind(anchorPaneSearchDir.widthProperty());

         */
        // gridpane.
        //     tabPaneMain.getTabs().clear();
        // buttonNewJarScan.setDisable(true);
       // buttonSearchDirClicked();
    }

    private boolean isRighJarVersion()
    {
        String strScanModuleName = getScanModuleName(currentDuppelClickedItem, true);
        if (strScanModuleName != null && strScanModuleName.trim().length()>0)
        {
            Pattern p = Pattern.compile("(?s)version\\s\"(\\d+\\.\\d+\\.\\d+)\"\\s",
                    Pattern.DOTALL | Pattern.MULTILINE); // either this: ^\d+\).*\][\n\r]
            Matcher m = p.matcher(strScanModuleName);
            boolean find = m.find();
            if (find)
            {
                String strVersion = m.group(1);
                if (strVersion != null && strVersion.trim().length()>0)
                {
                    double dVersion = 0;
                    try {
                        int ind = strVersion.indexOf(".");
                        if (ind > -1) {
                            String strMainVersion = strVersion.substring(0, ind);
                            dVersion = Double.parseDouble(strMainVersion);
                            if (dVersion < 9)
                                return false;
                            return true;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        return false;
                    }
                }
            }
        }
        return false;
    }

    private String getScanModuleName(String jarPath, boolean bExecJavaVersion)
    {
        String strMessage = null;
        String ret = null;
        if (jarPath != null || bExecJavaVersion)
        {
            Process process = null;
            File jarfile = new File(".");
            if (!bExecJavaVersion) {
                jarfile = new File(jarPath);
                if (!jarfile.exists())
                    return null;
            }
            int exitValue = -1;
            StringProperty result = new SimpleStringProperty();
            result.setValue("");
            StringBuffer sb = new StringBuffer();
            StringBuilder resultError = new StringBuilder();
            resultError = new StringBuilder(80);
            String strCommand = "jar --describe-module --file " +jarfile.getAbsolutePath();
            if (bExecJavaVersion)
                strCommand = "java -version";

            try {
                if (Main.bDebug)
                    System.out.println(System.getProperty("java.home"));
                String strJavaHome = System.getProperty("java.home");
                String classpath = System.getProperty("java.class.path");
                classpath = JarScanProcesses.correctSpaceContainsClassPath(classpath);
                // classpath = ""; // classpath.replaceAll("\n", "");
                String javaBin = strJavaHome + File.separator + "bin" + File.separator + "";
                List<String> jvmArgs = new ArrayList<String>();
                String className = "com.inetfeedback.jarscan.JarScan21";
               // String strUpdatedCommand = "java -version"; // strCommand;
                String strUpdatedCommand = strCommand;
                // java.bin does nost havve jar app:
                if (javaBin != null && javaBin.length()>0)
                   strUpdatedCommand = javaBin +strUpdatedCommand;
                List<String> java_app_args = Arrays.asList(strUpdatedCommand.split(" "));
                List<String> command = new ArrayList<String>();
                // command.add(javaBin);
                // command.addAll(jvmArgs);
                // command.add("-cp");

                // File workingDir = new File(".");
                File workingDir = new File(javaBin);

                String preCP = new String("." +JarScanProcesses.getClassPathSeparator());
                // command.add(preCP +classpath);
                // command.add(className);
                command.addAll(java_app_args);

                String line = null;
                Map<java.lang.String, java.lang.String> mapEnv = System.getenv();
                String [] arrEnv = new String[mapEnv.size()];
                String value;
                int i = 0;
                for (String key : mapEnv.keySet())
                {
                    value = mapEnv.get(key);
                    arrEnv[i] = "" +key +"=" +value;
                    i++;
                }
                System.out.println(arrEnv.toString());
                String strExec = command.stream().collect(Collectors.joining(" "));
                // strExec = "C:\\java\\jdk1.8.0\\jre\\bin\\java -cp .;C:\\java\\project\\javafx\\gjarscan2\\out\\production\\gjarscan2;C:\\Users\\tkassila\\.IdeaIC2019.3\\system\\groovyHotSwap\\gragent.jar;C:\\Users\\tkassila\\.IdeaIC2019.3\\system\\captureAgent\\debugger-agent.jar com.inetfeedback.jarscan.JarScan21 -dir \\java\\project\\javafx\\gjarscan2 -class JarScan -donotask";
                System.out.println(strExec);
                process = Runtime.getRuntime().exec(strExec, arrEnv, workingDir);
                final BufferedReader bri = new BufferedReader
                        (new InputStreamReader(process.getInputStream()));
                final BufferedReader bre = new BufferedReader
                        (new InputStreamReader(process.getErrorStream()));
                while ((line = bri.readLine()) != null) {
                    System.out.println(line);
                    if (!line.contains(NEWLINE))
                        sb.append(line).append(NEWLINE);
                    else
                        sb.append(line);
                }
                while ((line = bre.readLine()) != null) {
                    System.out.println(line);
                    if (!line.contains(NEWLINE))
                        resultError.append(line).append(NEWLINE);
                    else
                        resultError.append(line);
                }
                bri.close();
                bre.close();
                result.setValue(sb.toString());
                exitValue = process.waitFor();
                if (sb.toString().equals(""))
                    result.setValue(resultError.toString());
                ret = result.toString();
                System.out.println("exitValue: " +process.exitValue());
                // exitValue = process.exitValue();
                System.out.println("Done.");
                process.destroy();
                process = null;
            }
            catch (IOException err) {
                err.printStackTrace();
                if (process != null)
                     exitValue = process.exitValue();
                strMessage = err.getMessage();
                setLabelMsg("IO Error when get module name: " +strMessage);
                process = null;
            } catch (InterruptedException ie){
                process = null;
                setLabelMsg("Interrupted when get module name: " +ie.getMessage());
            }
        }
        return ret;
    }

    public void buttonSearchDirClicked()
    {
        if (Main.bDebug)
            System.out.println("buttonSearchDirClicked");
        String strFieldSearchDir = this.textfieldSearchDir.getText();
        if (strFieldSearchDir != null && strFieldSearchDir.trim().length() != 0)
        {
            File tmpf = new File(strFieldSearchDir);
            if (tmpf.exists() && tmpf.isDirectory())
                this.directoryChooser.setInitialDirectory(tmpf);
            else
                this.directoryChooser.setInitialDirectory(null);
        }
        else
            this.directoryChooser.setInitialDirectory(null);
        selectedSearhDirectory = this.directoryChooser.showDialog(null);
        if (selectedSearhDirectory != null)
            this.textfieldSearchDir.setText(selectedSearhDirectory.getAbsolutePath());
    }

    private class ListResultData {
        private final String strValue;
        private ResultTextPosition searchData = null;

        public ListResultData(String p_strValue)
        {
            this.strValue = p_strValue;
        }

        public String toString() { return (this.strValue == null ? "" : this.strValue); }
        public ResultTextPosition getResultTextPosition() { return this.searchData; }
        public void setResultTextPosition(ResultTextPosition pos) { this.searchData = pos; }
    }

    private class ResultTextPosition
    {
        private int iRow = -1;
        private int iIndex = -1;
        private int iSearchLen = -1;
        private int [] arrIndex = null;
        private int iIndexEnd = -1;
        private int iIndexOfResultTextPosition = -1;
        private boolean bSearchCursor = false;

        public int getRow() { return iRow;}
        public int getRowIndex() { return iIndex;}
        public int getRowIndexEnd() { return iIndexEnd;}
        public int getSearchLen() { return iSearchLen;}
        public int getIndexOfResultTextPosition() { return iIndexOfResultTextPosition; }
        public boolean isSearchCursor() { return bSearchCursor; }
        public void setSearchCursorOn() { bSearchCursor = true; }
        public void setSearchCursorOff() { bSearchCursor = false; }

        public void addNewIndexInRow(int index)
        {
            if (index < 0 || iIndex == index)
                return;
            int [] newArrIndex = new int [arrIndex.length+1];
            int max = arrIndex.length;
            int i = 0;
            for (; i < max;)
            {
                newArrIndex[i] = arrIndex[i++];
            }
            newArrIndex[i] = index;
            this.arrIndex = newArrIndex;
        }

        public ResultTextPosition(int irow, int ipos, int iposEnd, int iSearchLen, int p_iIndexOfResultTextPosition)
        {
            this.iRow = irow;
            this.iIndex = ipos;
            this.iIndexEnd = iposEnd;
            this.iIndexOfResultTextPosition = p_iIndexOfResultTextPosition;
            this.arrIndex = new int[1];
            this.arrIndex[0] = iIndex;
            this.iSearchLen = iSearchLen;
        }
    }

    private ResultTextPosition [] getResultTextPositions(String strSearch)
    {
        if (strSearch == null || strSearch.trim().length() == 0)
            return null;
        if (this.arrResultTextRows == null || this.arrResultTextRows.length == 0)
            return null;

        List<ResultTextPosition> listSearchResults =
                new ArrayList<ResultTextPosition>();
        int indRow = -1, iRow = -1, iSearchLen = strSearch.length();
        int iRowLen = -1, iLastFoundedInd = -1, iIndeResultTextRow = 0;
        ResultTextPosition currFoundPos;

        for(ListResultData strRow : this.arrResultTextRows)
        {
            iRow++;
            if (strRow == null || strRow.toString().trim().length() == 0)
                continue;
            indRow = strRow.toString().indexOf(strSearch);
            if (indRow == -1)
                continue;
            iRowLen = strRow.toString().length();
            currFoundPos = new ResultTextPosition(iRow, indRow,
                    indRow+iSearchLen, iSearchLen, iIndeResultTextRow);
            iLastFoundedInd = indRow;
            while (indRow+iSearchLen < iRowLen )
            {
                indRow = strRow.toString().indexOf(strSearch, indRow+iSearchLen);
                if (indRow != -1) {
                    if (iLastFoundedInd < indRow)
                        currFoundPos.addNewIndexInRow(indRow);
                    else
                    if (Main.bDebug)
                        System.out.println("False: iLastFoundedInd < indRow");
                    iLastFoundedInd = indRow;
                }
                else
                    break;
            }
            listSearchResults.add(currFoundPos);
            iIndeResultTextRow++;
        }

        ResultTextPosition [] ret = new ResultTextPosition[listSearchResults.size()];
        ret = listSearchResults.toArray(ret);
        return ret;
    }

    public void buttonSearchResultClicked(){
        if (Main.bDebug)
            System.out.println("buttonSearchResultClicked");
        int items = this.listResultItems.size();
        if (items < 1)
            return;
        String strSearch = this.textfieldSearhFromResult.getText();
        if (strSearch == null || strSearch.trim().length() == 0)
            return;
        currentResultTextPositionCursor = null;
        ListResultData [] arrItems = new ListResultData[items];
        int iRow = -1;
        for(Object objRow : this.listResultItems)
        {
            iRow++;
            arrItems[iRow] = (ListResultData)objRow;
        }
        this.arrResultTextRows = arrItems;
        if (arrResultTextRows == null || arrResultTextRows.length == 0)
            return;
        arrSearchResults = getResultTextPositions(strSearch);
        unMarkListResultWithFoundedSearchs();
        this.lastResultTextPosition = getLastResultTextPosition();
        this.firstResultTextPosition = getFirstResultTextPosition();
        markListResultWithFoundedSearchs();
        buttonNextClicked();
    }

    private void unMarkListResultWithFoundedSearchs()
    {
        ListResultData data = null;
        int max = this.listResult.getItems().size();
        if (max == 0)
            return;
        for(int i = 0; i < max; i++)
        {
            data = (ListResultData)this.listResult.getItems().get(i);
            if (data == null)
                continue;
            data.setResultTextPosition(null);
            this.listResult.getItems().set(i, data);
        }
        this.listResult.refresh();
    }

    private void markListResultWithFoundedSearchs()
    {
        if (arrSearchResults != null && arrSearchResults.length > 0)
        {
            int iResultTextPosition = 0;
            ListResultData foundedResultData = null;
            for(ResultTextPosition foundpos : arrSearchResults)
            {
                if (foundpos == null)
                    continue;
                foundedResultData = (ListResultData) this.listResultItems.get(foundpos.getRow());
                if (foundedResultData == null)
                    continue;
                foundpos.setSearchCursorOff();
                /*
                if (iResultTextPosition == 0) {
                    foundpos.setSearchCursorOn();
                    currentResultTextPositionCursor = foundpos;
                }
                 */
                foundedResultData.setResultTextPosition(foundpos);
                this.listResult.getItems().set(foundpos.getRow(), foundedResultData);
                // markThisRowitemWithFoundedPositions(foundedResultItem, foundpos);
               // this.listResult.refresh();
            }
            this.listResult.refresh();
            this.buttonNext.setDisable(false);
            this.buttonPrevious.setDisable(false);
        }
        else
        {
            this.buttonNext.setDisable(true);
            this.buttonPrevious.setDisable(true);
        }
    }

    private void setResultTextPositionCursorOn(ResultTextPosition resultTextPosition)
    {
        if (resultTextPosition == null)
            return;
        if (this.listResult == null || this.listResult.getItems().size() == 0)
            return;
        ListResultData foundedResultData = (ListResultData) this.listResult.getItems().get(resultTextPosition.getRow());
        if (foundedResultData == null)
            return;
        resultTextPosition.setSearchCursorOn();
        foundedResultData.setResultTextPosition(resultTextPosition);
        this.listResult.getItems().set(resultTextPosition.getRow(), foundedResultData);
        if (this.currentResultTextPositionCursor != null)
        {
            currentResultTextPositionCursor.setSearchCursorOff();
            foundedResultData = (ListResultData) this.listResult.getItems().get(currentResultTextPositionCursor.getRow());
            if (foundedResultData != null)
            {
                foundedResultData.setResultTextPosition(currentResultTextPositionCursor);
                this.listResult.getItems().set(currentResultTextPositionCursor.getRow(), foundedResultData);
            }
        }
        currentResultTextPositionCursor = resultTextPosition;
        if (currentResultTextPositionCursor != null)
        {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                        listResult.scrollTo(currentResultTextPositionCursor.getRow());
                    // listResult.getSelectionModel().select(currentResultTextPositionCursor.getRow());
                }
            });
        }
    }

    private void setResultTextPositionCursorOff(ResultTextPosition resultTextPosition)
    {
        if (resultTextPosition == null)
            return;
        if (this.listResult == null || this.listResult.getItems().size() == 0)
            return;
        ListResultData foundedResultData = (ListResultData) this.listResult.getItems().get(resultTextPosition.getRow());
        if (foundedResultData == null)
            return;
        resultTextPosition.setSearchCursorOff();
        foundedResultData.setResultTextPosition(resultTextPosition);
        this.listResult.getItems().set(resultTextPosition.getRow(), foundedResultData);
    }

    private ResultTextPosition getFirstResultTextPosition()
    {
        if (this.arrSearchResults == null || this.arrSearchResults.length == 0)
            return null;
        return arrSearchResults[0];
    }

    private ResultTextPosition getLastResultTextPosition()
    {
        if (this.arrSearchResults == null || this.arrSearchResults.length == 0)
            return null;
        int max = this.arrSearchResults.length;
        int iLastIndex = max -1;
        return arrSearchResults[iLastIndex];
    }

    private ResultTextPosition getNextResultTextPosition(ResultTextPosition prev)
    {
        if (this.arrSearchResults == null || this.arrSearchResults.length  == 0)
            return null;
        int max = this.arrSearchResults.length;
        int iLastIndex = max -1;
        int iCurrentPosition = prev.getIndexOfResultTextPosition();
        int iNextInd = iCurrentPosition +1;
        if (iNextInd > iLastIndex)
            return  null;
        return arrSearchResults[iNextInd];
    }

    private ResultTextPosition getPreviousResultTextPosition(ResultTextPosition next)
    {
        if (this.arrSearchResults == null || this.arrSearchResults.length == 0)
            return null;
        int iCurrentPosition = next.getIndexOfResultTextPosition();
        int iPrevInd = iCurrentPosition -1;
        if (iPrevInd < 0)
            return  null;
        return arrSearchResults[iPrevInd];
    }

    /*
    private void markThisRowitemWithFoundedPositions(Object foundedResultItem, ResultTextPosition foundos)
    {
        foundedResultItem.setUserData()
    }
     */

    public void buttonExecutionClicked()
    {
        if (Main.bDebug)
            System.out.println("buttonExecutionClicked");
        if (this.bExecuted)
            return;
        this.buttonCancelExecution.setDisable(false);
        this.buttonExecution.setDisable(true);
        this.listResult.getItems().clear();
        this.listResult.refresh();
        searchClassFromJarFiles();
        if( !this.bExecuted)
        {
            this.buttonCancelExecution.setDisable(true);
            this.checkBoxMaven.setDisable(true);
            this.checkBoxGradle.setDisable(true);
            this.buttonExecution.setDisable(false);
        }
    }

    public void buttonCancelExecutionClicked()
    {
        if (Main.bDebug)
            System.out.println("buttonCancelExecutionClicked");
        cancelExection();
        this.setLabelMsg("Execution canceled.");
        this.bExecuted = false;
        this.buttonExecution.setDisable(false);
        this.checkBoxMaven.setDisable(true);
        this.checkBoxGradle.setDisable(true);
        this.buttonCancelExecution.setDisable(true);
        this.checkBoxMaven.setDisable(true);
        this.checkBoxGradle.setDisable(true);
    }

    private void cancelExection()
    {
        if (Main.bDebug)
            System.out.println("cancelExection");
        processes.cancelProcess();
        this.bExecuted = false;
    }

    private void makeBeep()
    {
        if (Main.bDebug)
            System.out.println("Make sound");
        byte[] buf = new byte[2];
        int frequency = 44100; //44100 sample points per 1 second
        AudioFormat af = new AudioFormat((float) frequency, 16, 1, true, false);
        try {
            SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
            sdl.open();
            sdl.start();
            int durationMs = 100;
            int numberOfTimesFullSinFuncPerSec = 441; //number of times in 1sec sin function repeats
            for (int i = 0; i < durationMs * (float) 44100 / 1000; i++) { //1000 ms in 1 second
                float numberOfSamplesToRepresentFullSin = (float) frequency / numberOfTimesFullSinFuncPerSec;
                double angle = i / (numberOfSamplesToRepresentFullSin / 2.0) * Math.PI;  // /divide with 2 since sin goes 0PI to 2PI
                short a = (short) (Math.sin(angle) * 32767);  //32767 - max value for sample to take (-32767 to 32767)
                buf[0] = (byte) (a & 0xFF); //write 8bits ________WWWWWWWW out of 16
                buf[1] = (byte) (a >> 8); //write 8bits WWWWWWWW________ out of 16
                sdl.write(buf, 0, 2);
            }
            sdl.drain();
            sdl.stop();
        }catch (javax.sound.sampled.LineUnavailableException e){
            e.getMessage();
        }
        /*
          if (Main.bDebug)
        System.out.print("\007");
        System.out.flush();
        try {
            Thread.sleep(1000); // introduce delay
        } catch (InterruptedException e) {
        }
        // Toolkit.getDefaultToolkit().beep();

         */
    }

    private void searchClassFromJarFiles()
    {
        if (Main.bDebug)
            System.out.println("searchClassFromJarFiles");
        StringBuffer sb = new StringBuffer();
        // sb.append("C:\\Java\\jdk1.8.0\\bin\\java -cp ./lib/jarscan21.jar;./lib/jarscan.jar; com.inetfeedback.jarscan.JarScan21 ");
        if (this.checkboxHelp.isSelected())
        {
            sb.append("" +this.checkboxHelp.getText());
            String strExecDir = new String(".");
            executeSearchFromGivenJarFiles(strExecDir, sb.toString());
            // this.bExecuted = false;
            return;
        }

        String strDir = this.textfieldSearchDir.getText();
        if (strDir == null || strDir.trim().length() == 0)
        {
            setLabelMsg("Search directory field is emty or contains space. Not executed.");
            this.bExecuted = false;
            makeBeep();
            return;
        }

        String strSearch = this.textfieldSearchClass.getText();
        if (strSearch == null || strSearch.trim().length() == 0)
        {
            setLabelMsg("Search class field is emty or contains space. Not executed.");
            this.bExecuted = false;
            makeBeep();
            return;
        }
        if (strSearch.contains("/"))
            strSearch = strSearch.replaceAll("/", "\\.");

        if (strDir.contains("%") || strDir.contains("$"))
            strDir = getPossibleEnvironmentVariableValue(strDir);

        if (strDir == null || strDir.trim().length() == 0)
        {
            setLabelMsg("Search directory %ENV_VARIABLE% is emty or contains space. Not executed.");
            this.bExecuted = false;
            makeBeep();
            return;
        }

        File fileTest = new File(strDir);
        if (!fileTest.exists())
        {
            setLabelMsg("This '" +fileTest.getAbsolutePath() +"' file does not exists. Executon canceled.");
            makeBeep();
            return;
        }
        if (!fileTest.isDirectory())
        {
            setLabelMsg("This '" +fileTest.getAbsolutePath() +"' file is not a directory. Executon canceled.");
            makeBeep();
            return;
        }

        sb.append("-dir " +strDir);
        sb.append(" " +searchType.getSelectionModel().getSelectedItem().toString());
        sb.append(" " +strSearch);

        if (this.checkboxZip.isSelected())
            sb.append(" " + this.checkboxZip.getText());
        if (!this.checkboxNoSearchList2.isSelected() && this.checkboxNoSearchList.isSelected())
            sb.append(" " + this.checkboxNoSearchList.getText());
        if (this.checkboxNosubdir.isSelected())
            sb.append(" " + this.checkboxNosubdir.getText());

        String strExecDir = new String(".");
        executeSearchFromGivenJarFiles(strExecDir, sb.toString());
        // this.bExecuted = false;
        return;
    }

    private String getPossibleEnvironmentVariableValue(String strDir)
    {
        String ret = strDir;
      if (strDir != null && strDir.contains("%"))
          return getWinEnvVariable(strDir);
      else
      if (strDir != null && strDir.contains("$"))
          return getLinuxEnvVariable(strDir);
      return ret;
    }

    private String getWinEnvVariable(String strDir)
    {
        if ( strDir == null || strDir.trim().length() == 0)
            return strDir;
        int ind = strDir.indexOf('%');
        if (ind == -1)
            return strDir;
        int indSecond = strDir.indexOf('%', ind+1);
        if (indSecond == -1)
            return strDir;
        String strEnvVar = strDir.substring(ind+1, indSecond);
        if (strEnvVar == null || strEnvVar.trim().length() == 0)
            return strDir;
        String strEnvValue = System.getenv(strEnvVar);
        if (strEnvValue == null || strEnvValue.trim().length() == 0)
            return strDir;
        return strEnvValue;
    }

    private String getLinuxEnvVariable(String strDir)
    {
        if ( strDir == null || strDir.trim().length() == 0)
            return strDir;
        strDir = strDir.trim();
        int ind = strDir.indexOf('$');
        if (ind == -1)
            return strDir;
        if (ind != 0)
            return strDir;
        String strEnvVar = strDir.substring(ind+1);
        if (strEnvVar == null || strEnvVar.trim().length() == 0)
            return strDir;
        String strEnvValue = System.getenv(strEnvVar);
        if (strEnvValue == null || strEnvValue.trim().length() == 0)
            return strDir;
        return strEnvValue;
    }

    private ListResultData [] getListResultDataWithResultTextPositions(String [] arrResultStrings,
                                                                       ResultTextPosition [] arrSearchResults)
    {
        ListResultData [] ret = null;
        if (arrResultStrings ==  null || arrResultStrings.length == 0)
            return null;
        ListResultData currentRdata = null;
        List<ListResultData> list = new ArrayList<ListResultData>();
        for (String value : arrResultStrings)
        {
            currentRdata = new ListResultData(value);
            list.add(currentRdata);
        }
        ret = new ListResultData[list.size()];
        ret = list.toArray(ret);
        return ret;
    }

    private ListResultData [] getListResultData(String [] arrResultStrings)
    {
        return getListResultDataWithResultTextPositions(arrResultStrings, null);
    }

    private String [] getSplittedArray(String strSplit, String strSplitWithThis)
    {
        if (strSplit == null)
            return null;
        int ind = strSplit.indexOf(strSplitWithThis);
        if (ind == -1)
        {
            String [] ret = new String[1];
            ret[0] = strSplit;
            return ret;
        }
        List<String> list = new ArrayList<String>();
        int iStart = 0, iLen = strSplit.length();
        String strFound = null;
        while(ind > -1 && ind < iLen)
        {
            strFound = strSplit.substring(iStart, ind);
            if (strFound == null)
                strFound = "";
            list.add(strFound);
            iStart = ind +1;
            ind = strSplit.indexOf(strSplitWithThis, iStart);
        }
        String [] ret = new String[list.size()];
        ret = list.toArray(ret);
        return ret;
    }
    private void executeSearchFromGivenJarFiles(String strWorkingDir, String strExecute) {
        if (Main.bDebug)
        {
            System.out.println("executeSearchFromGivenJarFiles");
            System.out.println("strExecute=" + strExecute);
        }
        final StringBuffer sbResult = new StringBuffer();
        try {
            this.listResult.getItems().clear();
            this.buttonSearchResult.setDisable(true);
             processes.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent t) {
                    System.out.println("done:" /* + t.getSource().getValue() */);
                    JarScanProcesses service = (JarScanProcesses)t.getSource();
                    int exitvalue = service.getExitValue();
                    Object arrValue = t.getSource().getValue();
                    if (exitvalue != 0) {
                        setLabelMsg("Error.");
                        isJarScanSearchOk.setBoolean(false);
                        if ((arrValue == null || arrValue.equals(""))
                                && service.getErrorString() != null) {
                            listResult.getItems().addAll(getListResultData(service.getErrorString().split("(\n|$)")));
                        }
                        else
                            listResult.getItems().addAll(getListResultData(((String)arrValue).split("(\n|$)")));
                    }
                    else
                    {
                        isJarScanSearchOk.setBoolean(true);
                        if (arrValue instanceof String) {
                            String strSplit = new String(((String)arrValue)+"\n");
                            strSplit = getNormalResult(strSplit);
                            String [] arrListRows = getSplittedArray(strSplit, "\n");
                            listResultItems.clear();
                            listResultItems.addAll(getListResultData(arrListRows));
                            listResult.getItems().addAll(listResultItems);
                        }
                        else
                            listResult.getItems().addAll(new ListResultData(arrValue.toString()));
                        setLabelMsg("Done.");
                    }
                    buttonExecution.setDisable(false);
                    if (listResult.getItems().size() > 0)
                    {
                        buttonSearchResult.setDisable(false);
                        checkBoxMaven.setDisable(false);
                        checkBoxGradle.setDisable(false);
                    }
                    buttonCancelExecution.setDisable(true);
                    bExecuted = false;
                    //listResult.getItems().addAll(strResult.split("(\n|$)"));
                }
            });
            processes.setOnFailed(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent t) {
                    System.out.println("failed:" /* + t.getSource().getValue() */);
                    if (bProssesRestarted)
                        return;
                    isJarScanSearchOk.setBoolean(false);
                    listResult.getItems().clear();
                    Object arrValue = t.getSource().getValue();
                    if (arrValue != null)
                        listResult.getItems().addAll(new ListResultData(arrValue.toString()));
                    setLabelMsg("Failed.");
                    //listResult.getItems().addAll(strResult.split("\n"));
                    buttonExecution.setDisable(false);
                    buttonCancelExecution.setDisable(true);
                    bExecuted = false;
                    if (listResult.getItems().size() > 0)
                    {
                        buttonSearchResult.setDisable(false);
                        checkBoxMaven.setDisable(false);
                        checkBoxGradle.setDisable(false);
                    }
                }
            });

            processes.setOnCancelled(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent t) {
                    System.out.println("canceled:" /* + t.getSource().getValue() */);
                    if (bProssesRestarted)
                        return;
                    isJarScanSearchOk.setBoolean(false);
                    listResult.getItems().clear();
                    Object arrValue = t.getSource().getValue();
                    if (arrValue != null) {
                        listResult.getItems().clear();
                        listResult.getItems().addAll(new ListResultData(arrValue.toString()));
                    }
                    setLabelMsg("Canceled.");
                    //listResult.getItems().addAll(strResult.split("\n"));
                    buttonExecution.setDisable(false);
                    buttonCancelExecution.setDisable(true);
                    checkBoxMaven.setDisable(true);
                    checkBoxGradle.setDisable(true);
                    bExecuted = false;
                    if (listResult.getItems().size() > 0)
                        buttonSearchResult.setDisable(false);
                }
            });

            buttonExecution.setDisable(true);
            buttonCancelExecution.setDisable(false);
            checkBoxMaven.setDisable(true);
            checkBoxGradle.setDisable(true);
            this.processes.setExecutionData(strWorkingDir, strExecute);
            this.setLabelMsg("Executing...");
            this.bExecuted = true;
           // test buttons: Thread.sleep(10000);
            if (this.processes.getState() != Worker.State.READY) {
                this.bProssesRestarted = true; // this boolean variable into true, because to prevent
                // of extra call below:
                this.processes.restart(); // restart causes an extra call into onsuccess etc
                bProssesRestarted = false; // after an extra handler catt, after this can do "normal" handler call
            }
            else
                this.processes.start();
           // strResult = processes.run(strWorkingDir, strExecute);
        }catch (Exception e){
            e.printStackTrace();
            sbResult.append(e.toString() +"\n\n errorlevel " +processes.getExitValue() +"\n\n" +processes.getErrorString());
            this.listResult.getItems().addAll(sbResult.toString().split("(\n|$)"));
            this.bExecuted = false;
        }
    }

    private String getNormalResult(String strResult)
    {
        if (strResult == null || strResult.trim().length() == 0)
            return strResult;
        if (checkboxNoSearchList2.isSelected())
            strResult = getResulRemovedFromSearchList(strResult);
        if (checkboxRemoveEmptyLines.isSelected())
            strResult = strResult.replaceAll("(\\s*\\n){2,}", "\n");
       return strResult;
    }

    private String getResulRemovedFromSearchList(String strResult)
    {
        if (strResult == null || strResult.trim().length() == 0)
            return strResult;
        // this kind* of regex in java does not work:
        // String strResult2 = strResult.replaceAll("\\d\\).*\\](\n|\r)", "");
        Pattern p = null;
        Matcher m = null;
        StringBuffer sb = new StringBuffer();
        try {
            p = Pattern.compile("^\\d+\\).*\\][\\n\\r]", Pattern.DOTALL | Pattern.MULTILINE); // either this: ^\d+\).*\][\n\r]
            m = p.matcher(strResult);
            boolean find = m.find();
            int iStart = -1, iEnd = -1, indCurr = 0, ilen = strResult.length();
            while (find && indCurr < ilen)
            {
                iStart = m.start();
                iEnd = m.end();
                sb.append(strResult.substring(indCurr, iStart));
                indCurr = iEnd;
                find = m.find();
            }
            if (indCurr < ilen)
                sb.append(strResult.substring(indCurr));
        } catch (PatternSyntaxException ex) {
            // Syntax error in the regular expression
            ex.printStackTrace();
            return null;
        }
        String ret = sb.toString().replaceAll("\n{3,}", "");
        return ret;
    }

    public void buttonNextClicked()
    {
        if (Main.bDebug)
            System.out.println("buttonNextClicked");
        if (this.arrResultTextRows == null || this.arrResultTextRows.length == 0)
            return;

        if (this.currentResultTextPositionCursor == null)
        {
            ResultTextPosition first = getFirstResultTextPosition();
            if (first == null) {
                this.buttonPrevious.setDisable(true);
                this.buttonNext.setDisable(true);
                return;
            }
            firstResultTextPosition = first;

            this.buttonPrevious.setDisable(true);
            this.setResultTextPositionCursorOn(first);
            this.currentResultTextPositionCursor = first;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    listResult.scrollTo(currentResultTextPositionCursor.getRow());
                    // listResult.getSelectionModel().select(currentResultTextPositionCursor.getRow());
                }
            });
            return;
        }

        ResultTextPosition next = getNextResultTextPosition(this.currentResultTextPositionCursor);
        if (next == null)
        {
           // this.currentResultTextPositionCursor = null;
            this.buttonNext.setDisable(true);
            this.buttonPrevious.setDisable(false);
            return;
        }

        // ResultTextPosition last = getLastResultTextPosition();
        if (lastResultTextPosition != null && lastResultTextPosition.equals(next))
        {
            this.buttonNext.setDisable(true);
            this.buttonPrevious.setDisable(false);
        }
        else
        if (this.buttonPrevious.isDisabled())
            this.buttonPrevious.setDisable(false);
        this.setResultTextPositionCursorOff(this.currentResultTextPositionCursor);
        this.setResultTextPositionCursorOn(next);
        this.currentResultTextPositionCursor = next;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                listResult.scrollTo(currentResultTextPositionCursor.getRow());
               // listResult.getSelectionModel().select(currentResultTextPositionCursor.getRow());
            }
        });
    }

    public void buttonPreviousClicked() {
        if (Main.bDebug)
            System.out.println("buttonPreviousClicked");
        if (this.arrResultTextRows == null || this.arrResultTextRows.length == 0)
            return;

        if (this.currentResultTextPositionCursor == null) {
            ResultTextPosition last = getLastResultTextPosition();
            if (last == null)
            {
                this.buttonPrevious.setDisable(true);
                this.buttonNext.setDisable(true);
                return;
            }
            lastResultTextPosition =  last;

            this.setResultTextPositionCursorOn(last);
            this.currentResultTextPositionCursor = last;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    listResult.scrollTo(currentResultTextPositionCursor.getRow());
                 //   listResult.getSelectionModel().select(currentResultTextPositionCursor.getRow());
                }
            });
            return;
        }

        ResultTextPosition prev = getPreviousResultTextPosition(this.currentResultTextPositionCursor);
        if (prev == null)
        {
            // this.currentResultTextPositionCursor = null;
            this.buttonPrevious.setDisable(true);
            this.buttonNext.setDisable(false);
            return;
        }

        if (firstResultTextPosition != null && firstResultTextPosition.equals(prev))
        {
            this.buttonPrevious.setDisable(true);
            this.buttonNext.setDisable(false);
        }
        else
        if (this.buttonNext.isDisabled())
            this.buttonNext.setDisable(false);
        this.setResultTextPositionCursorOff(this.currentResultTextPositionCursor);
        this.setResultTextPositionCursorOn(prev);
        this.currentResultTextPositionCursor = prev;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                listResult.scrollTo(currentResultTextPositionCursor.getRow());
               // listResult.getSelectionModel().select(currentResultTextPositionCursor.getRow());
            }
        });
    }

    private String getClassValue(String duppelClickedItem)
    {
        String ret = null;
        if (duppelClickedItem == null)
            return null;
        if (duppelClickedItem.trim().length() == 0)
            return null;
        final String strClass = "Class:";
        if (!duppelClickedItem.trim().startsWith(strClass))
            return null;
        ret = duppelClickedItem.replace(strClass, "").trim();
        return ret;
    }

    private String getJarPathValue(String duppelClickedItem)
    {
        String ret = null;
        if (duppelClickedItem == null)
            return null;
        if (duppelClickedItem.trim().length() == 0)
            return null;
        if (!duppelClickedItem.toLowerCase().contains(".jar"))
            return null;
        final String strLibPath = "Library Path:";
        if (!duppelClickedItem.contains(strLibPath))
            return null;
        String pathSeparator = "" +File.separatorChar;
        if (!duppelClickedItem.toLowerCase().contains(pathSeparator))
            return null;
        ret = duppelClickedItem.replaceAll(strLibPath, "").trim();
        return ret;
    }

    private double getCurrentDividerPosition(double [] arrDiverPosition)
    {
        if (arrDiverPosition == null && arrDiverPosition.length != 1)
            return 0.5;
        double dDiverPosition = arrDiverPosition[0];
        return dDiverPosition;
    }

    private double roundHalfDividerPosition(double dvalue)
    {
        return round(dvalue, 1, RoundingMode.HALF_UP);
    }

    private double roundZeroDividerPosition(double dvalue)
    {
        return round(dvalue, 1, RoundingMode.HALF_DOWN);
    }

    private static double round(double value, int places, RoundingMode roundinmode) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, roundinmode);
        return bd.doubleValue();
    }

    public void togleSplitPanel()
    {
        if (Main.bDebug) {
            System.out.println("buttonSearchDir is: " + (buttonSearchDir == null ? "on null" : "ei ole null"));
            System.out.println("anchorPaneUppder is: " + (anchorPaneUppder == null ? "on null" : "ei ole null"));
            System.out.println("anchorPaneLower is: " + (anchorPaneLower == null ? "on null" : "ei ole null"));
            System.out.println("titledPaneSearhFromResult is: " + (titledPaneSearhFromResult == null ? "on null" : "ei ole null"));
        }
        double dDiverPosition = getCurrentDividerPosition(
                        this.splitPaneResult.getDividerPositions()
                );

        if (dZeroDiverPosition == null /* ||
                roundZeroDividerPosition(dDiverPosition) !=
                        dZeroDiverPosition.doubleValue() */ )
        {
            // next statement is removing titledPaneSearhFromResult node from anchorPaneUppder node:
            splitPaneResult.getItems().set(0, this.titledPaneSearhFromResult);
            // splitPaneResult.getItems().clear();
            splitPaneResult.setDividerPosition(0, 0.0);
            // splitPaneResult.getItems().addAll(this.titledPaneSearhFromResult, anchorPaneLower);
            dZeroDiverPosition = new Double(roundZeroDividerPosition(
                    getCurrentDividerPosition(
                            this.splitPaneResult.getDividerPositions()))
            );
            if (currentResultTextPositionCursor != null)
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    listResult.scrollTo(currentResultTextPositionCursor.getRow());
                    // listResult.getSelectionModel().select(currentResultTextPositionCursor.getRow());
                }
            });
        }
        else
        {
            // above statement was removing titledPaneSearhFromResult node from anchorPaneUppder node,
            // and therefor titledPaneSearhFromResult will be added AGAIN into anchorPaneUppder node:
            anchorPaneUppder.getChildren().add(titledPaneSearhFromResult);
            splitPaneResult.getItems().set(0, this.anchorPaneUppder);
            dZeroDiverPosition = null;
            // splitPaneResult.getItems().clear();
            // splitPaneResult.getItems().addAll(this.anchorPaneUppder, anchorPaneLower);
            splitPaneResult.setDividerPosition(0, 0.5);
            dHalfDiverPosition = new Double(roundHalfDividerPosition(
                    getCurrentDividerPosition(
                            this.splitPaneResult.getDividerPositions()))
            );
            if (currentResultTextPositionCursor != null)
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    // sometime without -1 in next scrollto method scrolled row doest not show, but
                    // when this 'bug' comes -1 is correcting situation rigth:
                    listResult.scrollTo(currentResultTextPositionCursor.getRow()-1);
                    // listResult.getSelectionModel().select(currentResultTextPositionCursor.getRow());
                }
            });
        };
    }

    private void setLabelMsg(String str)
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                labelMsg.setText(str);
            }
        });
    }
}
