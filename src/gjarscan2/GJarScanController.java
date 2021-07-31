package gjarscan2;

import javafx.concurrent.Task;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;
import javafx.scene.text.Font;

import java.util.HashMap;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.nio.charset.StandardCharsets;
import java.io.FileFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.AudioSystem;
class SourceComment {

    public SourceComment(int p_start, int p_end)
    {
        start = p_start;
        end = p_end;
    }

    public final int start;
    public final int end;
}

public class GJarScanController {

    private static final String cnstSearchImports = "-search_imports";

    private ObservableList<String> searchTypeList = FXCollections.observableArrayList(
            "-class", "-package", "-files", cnstSearchImports
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
    Button buttonSourceRootDir;

    private boolean bProssesRestarted = false;
    private volatile boolean bExecuted = false;
    private final JarScanProcesses processes = new JarScanProcesses();
    private Task<Integer> importTask = null;
    final ObservableList listResultItems = FXCollections.observableArrayList("1", "2", "3");
    private String currentDualClickedItem = null;
    private Double dZeroDiverPosition = null;
    private Double dHalfDiverPosition = null;
    private DirectoryChooser directoryChooser = new DirectoryChooser();
    private DirectoryChooser directoryChooserOfSource = new DirectoryChooser();
    private File selectedSearhDirectory = null;
    private ListResultData [] arrResultTextRows = null;
    private ResultTextPosition [] arrSearchResults = null;
    private ResultTextPosition currentResultTextPositionCursor = null;
    private ResultTextPosition firstResultTextPosition = null;
    private ResultTextPosition lastResultTextPosition =  null;
    private int iIndextitledPaneSearhFromResult = -1;
    private ExtensionsFilter sourcefilefilter = new ExtensionsFilter(new String[] {".java", ".groovy",".kt", ".scala"});
    private HashMap<String,File> hmImportedJars = new HashMap<String,File>();
    private List<ListResultData> importJars = new ArrayList<ListResultData>();

    @FXML
    public void initialize() {
        if (Main.bDebug)
            System.out.println("initialize");
        System.out.println("java.version=" +System.getProperty("java.version"));
        System.out.println("javafx.version=" +System.getProperty("javafx.version"));

        buttonSourceRootDir.setDisable(true);

        searchType.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            System.out.println(newValue);
            String strValue = (String) newValue;
            if (strValue != null && strValue.equals(cnstSearchImports))
            {
                buttonSourceRootDir.setDisable(false);
                textfieldSearchClass.setText("");
                if (textfieldSearchDir.getText().trim().length() > 0)
                    textfieldSearchClass.setText(textfieldSearchDir.getText());
                else
                    textfieldSearchClass.setText("");
            }
            else
                buttonSourceRootDir.setDisable(true);
        });

        directoryChooserOfSource.setTitle("Select source root directory");

        //  this.listResult.setFont(defaultFont);

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
        this.textfieldSearhFromResult.setText(" start =");
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

        this.directoryChooser.setTitle("Select jar search direcotory");

        this.listResult.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY
                        && event.getClickCount() == 2 )
                {
                    currentDualClickedItem = null;
                    Object selected = listResult.getSelectionModel().getSelectedItem();
                    if (selected != null) {
                        boolean bJarPathSeeked = true;
                        currentDualClickedItem = getJarPathValue(selected.toString());
                        if (currentDualClickedItem == null)
                        {
                            bJarPathSeeked = false;
                            currentDualClickedItem = getClassValue(selected.toString());
                        }
                        if (currentDualClickedItem != null) {
                            Toolkit.getDefaultToolkit()
                                    .getSystemClipboard()
                                    .setContents(
                                            new StringSelection(currentDualClickedItem),
                                            null
                                    );
                            if (bJarPathSeeked)
                                labelMsg.setText("A jar file path copyed into clipboard");
                            else
                                labelMsg.setText("A class copyed into clipboard");
                        }
                        else
                            labelMsg.setText("");
                    }

                }
                //your code here
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

    @FXML
    public void buttonSourceRootDirPressed()
    {
        if (Main.bDebug)
            System.out.println("buttonSourceRootDirPressed");
        String strFieldSearchDir = textfieldSearchClass.getText();
        if (strFieldSearchDir != null && strFieldSearchDir.trim().length() != 0)
        {
            File tmpf = new File(strFieldSearchDir);
            if (tmpf.exists() && tmpf.isDirectory())
                directoryChooserOfSource.setInitialDirectory(tmpf);
            else
                directoryChooserOfSource.setInitialDirectory(null);
        }
        else
            this.directoryChooserOfSource.setInitialDirectory(null);
        File selectedSearhDirectory = this.directoryChooserOfSource.showDialog(null);
        if (selectedSearhDirectory != null)
            this.textfieldSearchClass.setText(selectedSearhDirectory.getAbsolutePath());
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
        int indSel = searchType.getSelectionModel().getSelectedIndex();
        if (indSel == -1)
            return;
        String selected = (String)searchType.getSelectionModel().getSelectedItem();
        if (selected == null || selected.trim().length() != 0 && selected.equals(cnstSearchImports)) {
            checkboxNoSearchList2.setSelected(true);
            checkboxNoSearchList.setSelected(true);
            searchSourceImportsJarFiles();
        }
        else
            searchClassFromJarFiles();
        if( !this.bExecuted)
        {
            this.buttonCancelExecution.setDisable(true);
            this.buttonExecution.setDisable(false);
        }
    }

    private void searchSourceImportsJarFiles()
    {
        File fdir = new File(textfieldSearchDir.getText());
        if (!fdir.exists())
        {
            labelMsg.setText("Dir " +fdir.getAbsolutePath() +"do not exists! No execution!");
            return;
        }

        this.bExecuted = true;
        this.listResult.getItems().clear();
        this.buttonSearchResult.setDisable(true);
        hmImportedJars.clear();
        importJars.clear();

        importTask = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                searchSourceImportsJarFiles(fdir);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("list result size: " +listResult.getItems().size());

                        listResult.refresh();
                        labelMsg.setText("All done!");
                        buttonCancelExecution.setDisable(true);
                    }});
                super.succeeded();
                return 0;
            }

            @Override
            protected void succeeded() {
                labelMsg.setText("All done1: ");
               // this.buttonSearchResult.setDisable(false);
                buttonExecution.setDisable(false);
                bExecuted = false;
            }

            @Override
            protected void cancelled() {
                super.cancelled();
                buttonExecution.setDisable(false);
                bExecuted = false;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        labelMsg.setText("User canceled the execution!");
                    }});
            }

            @Override
            protected void failed() {
                super.failed();
                // this.buttonSearchResult.setDisable(true);
                bExecuted = false;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        labelMsg.setText("The execution failed!");
                    }});
            }
        };

        Thread th = new Thread(importTask);
        th.setDaemon(true);
        th.start();
    }

    private void searchSourceImportsJarFiles(File dir)
            throws InterruptedException, IOException
    {
        File [] sources, subdirs;
        String name;

        File [] arrSources = dir.listFiles(sourcefilefilter);
        for(File f : arrSources) {
            searchFromSouceImportsInJarFiles(f);
        }

        File [] arrSubDirs = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        });

        for(File dir2 : arrSubDirs) {
                searchSourceImportsJarFiles(dir2);
        }
    }

    private static String getSourceText(File f)
    {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines( Paths.get(f.getAbsolutePath()), StandardCharsets.UTF_8))
        {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }

        return contentBuilder.toString();
    }

    private SourceComment [] getSourceComments(String strSource)
    {
        if (strSource == null || strSource.trim().length() == 0)
            return null;
        final String cntStartComment = "/*";
        final String cntEndComment = "*/";
        SourceComment [] ret = null;
        int indStart = strSource.indexOf(cntStartComment);
        if (indStart == -1)
            return null;
        int indEnd = strSource.indexOf(cntEndComment, indStart);
        if (indEnd == -1)
            return null; // slmost impossible !

        List<SourceComment> list = new ArrayList<SourceComment>();
        SourceComment comment = null;
        while (indStart > -1 && indEnd > -1)
        {
            comment = new SourceComment(indStart, indEnd);
            list.add(comment);
            indStart = strSource.indexOf(cntStartComment, indEnd);
            if (indStart == -1)
                indEnd = -1;
            else
                indEnd = strSource.indexOf(cntEndComment, indStart);
        }
        ret = new SourceComment[list.size()];
        ret = list.toArray(ret);
        return ret;
    }

    private void searchFromSouceImportsInJarFiles(File f)
            throws InterruptedException, IOException
    {
        if (Main.bDebug)
            System.out.println("searchFromSouceImportsInJarFiles");
        if (f == null || !f.exists())
            return;

        String strSource = getSourceText(f);
        // dddd
        SourceComment [] sourceComments = getSourceComments(strSource);
        if (strSource == null || strSource.trim().length() == 0)
            return;
        Pattern p = null;
        Matcher m = null;
        int indComment = -1;
        String strLine = null;
        StringBuffer sb = new StringBuffer();
        try {
            p = Pattern.compile("($.*?)import\\s+(.*?)(;*\\s*\n)", Pattern.DOTALL | Pattern.MULTILINE); // either this: ^\d+\).*\][\n\r]
            m = p.matcher(strSource);
            boolean find = m.find();
            String strClass = null;
            int iGroup = -1, max = m.groupCount(), iStart, iEnd;
            while (find) {
                strClass = m.group(2);
                strLine = m.group(1);
                iStart = m.start();
                iEnd = m.end();
                if (strClass == null || strClass.trim().length() == 0)
                {
                    find = m.find();
                    continue;
                }

                if (strClass.startsWith("java.") || strClass.startsWith("javafx.") || strClass.startsWith("javax."))
                {
                    find = m.find();
                    continue;
                }
                if (strLine != null)
                {
                    indComment = strLine.indexOf("//");
                    if (indComment > -1)
                    {
                        find = m.find();
                        continue;
                    }
                }

                // search if import is inside of a comment:
                if (sourceComments != null)
                for (SourceComment sc : sourceComments)
                {
                    if (sc == null)
                        continue;
                    if (sc.start < iStart && sc.end > iEnd)
                    {
                        find = m.find();
                        continue;
                    }
                }

                System.out.println("class=" +strClass);
                startCLassSearchFrom(strClass);
                find = m.find();
            }
            /*
        }catch (Exception e) {
            e.printStackTrace();
             */
        }finally {
        }
    }

    private void startCLassSearchFrom(String strClass)
            throws InterruptedException, IOException
    {
        String search = strClass;
        String strSearchType = "-class";
        if (strClass.endsWith(".*")) {
            int ind = strClass.lastIndexOf(".*");
            if (ind > -1) {
                search = strClass.substring(0, ind);
                strSearchType = "-package";
            }
        }

        String strSearch = strClass;
        if (strSearch == null || strSearch.trim().length() == 0) {
            labelMsg.setText("Search class field is emty or contains space. Not executed.");
            makeBeep();
            return;
        }

        StringBuffer sb = new StringBuffer();
        String strDir = textfieldSearchDir.getText();
        File fdir = new File(strDir);
        if (!fdir.exists())
        {
            labelMsg.setText("Search dir does not exists!");
            makeBeep();
            return;
        }

        sb.append("-dir " +strDir);
        sb.append(" " +strSearchType);
        sb.append(" " +search);

        if (this.checkboxZip.isSelected())
            sb.append(" " + this.checkboxZip.getText());
        if (!this.checkboxNoSearchList2.isSelected() && this.checkboxNoSearchList.isSelected())
            sb.append(" " + this.checkboxNoSearchList.getText());
        if (this.checkboxNosubdir.isSelected())
            sb.append(" " + this.checkboxNosubdir.getText());

        String strExecDir = new String(".");
        executeSearchFromGivenJarFilesWithOutTask(strExecDir, sb.toString());
    }

    public void buttonCancelExecutionClicked()
    {
        if (Main.bDebug)
            System.out.println("buttonCancelExecutionClicked");
        cancelExection();
        this.labelMsg.setText("Execution canceled.");
        this.bExecuted = false;
        this.buttonExecution.setDisable(false);
        this.buttonCancelExecution.setDisable(true);
    }

    private void cancelExection()
    {
        if (Main.bDebug)
            System.out.println("cancelExection");
        String selected = (String) searchType.getSelectionModel().getSelectedItem();
        if (selected == null || selected.trim().length() == 0)
            return;
        if (selected.equals(cnstSearchImports))
            importTask.cancel(true);
        else
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
            labelMsg.setText("Search directory field is emty or contains space. Not executed.");
            this.bExecuted = false;
            makeBeep();
            return;
        }

        File fileTest = new File(strDir);
        if (!fileTest.exists())
        {
            labelMsg.setText("This '" +fileTest.getAbsolutePath() +"' file does not exists. Executon canceled.");
            makeBeep();
            return;
        }

        if (!fileTest.isDirectory())
        {
            labelMsg.setText("This '" +fileTest.getAbsolutePath() +"' file is not a directory. Executon canceled.");
            makeBeep();
            return;
        }

        sb.append("-dir " +strDir);
        sb.append(" " +searchType.getSelectionModel().getSelectedItem().toString());
        sb.append(" " +textfieldSearchClass.getText());

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

    private String getPossilbleEnvironmentVariableValue( String strdir)
    {
        if ( strdir == null || strdir.trim().length() == 0)
            return strdir;
        int ind = strdir.indexOf('%');
        if (ind == -1)
            return strdir;
        int indSecond = strdir.indexOf('%', ind+1);
        if (indSecond == -1)
            return strdir;
        String strEnvVar = strdir.substring(ind+1, indSecond);
        if (strEnvVar == null || strEnvVar.trim().length() == 0)
            return strdir;
        String strEnvValue = System.getenv(strEnvVar);
        if (strEnvValue == null || strEnvValue.trim().length() == 0)
            return strdir;
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
                        labelMsg.setText("Error.");
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
                        labelMsg.setText("Done.");
                    }
                    buttonExecution.setDisable(false);
                    if (listResult.getItems().size() > 0)
                        buttonSearchResult.setDisable(false);
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
                    labelMsg.setText("Failed.");
                    //listResult.getItems().addAll(strResult.split("\n"));
                    buttonExecution.setDisable(false);
                    buttonCancelExecution.setDisable(true);
                    bExecuted = false;
                    if (listResult.getItems().size() > 0)
                        buttonSearchResult.setDisable(false);
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
                    labelMsg.setText("Canceled.");
                    //listResult.getItems().addAll(strResult.split("\n"));
                    buttonExecution.setDisable(false);
                    buttonCancelExecution.setDisable(true);
                    bExecuted = false;
                    if (listResult.getItems().size() > 0)
                        buttonSearchResult.setDisable(false);
                }
            });

            buttonExecution.setDisable(true);
            buttonCancelExecution.setDisable(false);
            this.processes.setExecutionData(strWorkingDir, strExecute);
            this.labelMsg.setText("Executing...");
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

    private String [] getOnlyNewJarsInImportJars(String [] arrInput)
    {
        String [] ret = null;
        if (arrInput == null || arrInput.length == 0)
            return null;
        StringBuffer sb = new StringBuffer();
        int ind = -1;
        final String cnstClass = "Class:";
        final String cnstLibraryName = "Library Path:";
        final String cnstPackage = "Package:";
        String jarPath = null, jarBaseName = null;
        File jarFile = null;
        List<String> addJarPaths = new ArrayList<String>();
        String strClass = null;
        String strPackage = null;
        int indClass = -1, indPackage = -1;

        for (String s : arrInput)
        {
            if (s == null || s.trim().length() == 0)
                continue;
            indClass = s.indexOf(cnstClass);
            if (indClass > -1)
                strClass = s.substring(indClass +cnstClass.length() +1 );
            indPackage = s.indexOf(cnstPackage);
            if (indPackage > -1)
                strPackage = s.substring(indPackage +cnstPackage.length() +1 );
            ind = s.indexOf(cnstLibraryName);
            jarPath = null;
            if (ind > -1) {
                jarPath = s.substring(ind +cnstLibraryName.length() + 1);
                jarPath = jarPath.replaceAll("\n","").replaceAll("\r","");
                jarFile = new File(jarPath);
                jarBaseName = jarFile.getName();
                if (hmImportedJars.containsKey(jarBaseName))
                    continue;
                hmImportedJars.put(jarBaseName, jarFile);
                if (strClass != null)
                    addJarPaths.add(cnstClass +" " +strClass);
                else
                    addJarPaths.add(cnstPackage +" " +strPackage);
                addJarPaths.add(cnstLibraryName +" " +jarPath);
            }
        }
        ret = new String[addJarPaths.size()];
        ret = addJarPaths.toArray(ret);
        if (ret.length == 0)
            return null;
        return ret;
    }
    private void executeSearchFromGivenJarFilesWithOutTask(String strWorkingDir, String strExecute)
            throws InterruptedException, IOException
    {
        if (Main.bDebug)
        {
            System.out.println("executeSearchFromGivenJarFilesWithOutTask");
            System.out.println("strExecute=" + strExecute);
        }

        final StringBuffer sbResult = new StringBuffer();

        System.out.println("done:" /* + t.getSource().getValue() */);
        JarScanProcesses service = new JarScanProcesses();
        // service.setExecutionData(strWorkingDir, strExecute);
        // try {
            String arrValue = service.runJava(strWorkingDir, strExecute);
            int exitvalue = service.getExitValue();
            if (exitvalue != 0) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        labelMsg.setText("Error.");
                        isJarScanSearchOk.setBoolean(false);
                        if ((arrValue == null || arrValue.equals(""))
                                && service.getErrorString() != null) {
                            listResult.getItems().addAll(getListResultData(service.getErrorString().split("(\n|$)")));
                        } else
                            listResult.getItems().addAll(getListResultData(((String) arrValue).split("(\n|$)")));
                        }
                    });
            } else {
                isJarScanSearchOk.setBoolean(true);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (arrValue instanceof String) {
                            String strSplit = new String(((String) arrValue) + "\n");
                            strSplit = getNormalResult(strSplit);
                            String[] arrListRows = getSplittedArray(strSplit, "\n");
                            String[] arrListRows2 = getOnlyNewJarsInImportJars(arrListRows);
                            if (arrListRows2 == null || arrListRows2.length == 0)
                                return;
                            listResult.getItems().addAll(getListResultData(arrListRows2));
                        } else
                            listResult.getItems().add(new ListResultData(arrValue.toString()));
                    }
                });
              //  labelMsg.setText("Done.");
            }
            /*
        }catch (InterruptedException ie){
            labelMsg.setText("User interrupted the execution!");
            bExecuted = false;
        }catch (IOException ioe){
            ioe.printStackTrace();
            labelMsg.setText(ioe.getMessage());
            bExecuted = false;
        }
             */
        //listResult.getItems().addAll(strResult.split("(\n|$)"));

    /*
                    System.out.println("failed:");
                    if (bProssesRestarted)
                        return;
                    isJarScanSearchOk.setBoolean(false);
                    listResult.getItems().clear();
                    Object arrValue = t.getSource().getValue();
                    if (arrValue != null)
                        listResult.getItems().addAll(new ListResultData(arrValue.toString()));
                    labelMsg.setText("Failed.");
                    //listResult.getItems().addAll(strResult.split("\n"));
                    buttonExecution.setDisable(false);
                    buttonCancelExecution.setDisable(true);
                    bExecuted = false;
                    if (listResult.getItems().size() > 0)
                        buttonSearchResult.setDisable(false);
                }

                    System.out.println("canceled:");
                    if (bProssesRestarted)
                        return;
                    isJarScanSearchOk.setBoolean(false);
                    listResult.getItems().clear();
                    Object arrValue = t.getSource().getValue();
                    if (arrValue != null) {
                        listResult.getItems().clear();
                        listResult.getItems().addAll(new ListResultData(arrValue.toString()));
                    }
                    labelMsg.setText("Canceled.");
                    //listResult.getItems().addAll(strResult.split("\n"));
                    buttonExecution.setDisable(false);
                    buttonCancelExecution.setDisable(true);
                    bExecuted = false;
                    if (listResult.getItems().size() > 0)
                        buttonSearchResult.setDisable(false);
                }

            this.processes.setExecutionData(strWorkingDir, strExecute);
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
        }
     */
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

    private String getClassValue(String dualClickedItem)
    {
        String ret = null;
        if (dualClickedItem == null)
            return null;
        if (dualClickedItem.trim().length() == 0)
            return null;
        final String strClass = "Class:";
        if (!dualClickedItem.trim().startsWith(strClass))
            return null;
        ret = dualClickedItem.replace(strClass, "").trim();
        return ret;
    }

    private String getJarPathValue(String dualClickedItem)
    {
        String ret = null;
        if (dualClickedItem == null)
            return null;
        if (dualClickedItem.trim().length() == 0)
            return null;
        if (!dualClickedItem.toLowerCase().contains(".jar"))
            return null;
        final String strLibPath = "Library Path:";
        if (!dualClickedItem.contains(strLibPath))
            return null;
        String pathSeparator = "" +File.separatorChar;
        if (!dualClickedItem.toLowerCase().contains(pathSeparator))
            return null;
        ret = dualClickedItem.replaceAll(strLibPath, "").trim();
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
}
