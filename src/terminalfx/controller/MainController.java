/*
 * Copyright (C) 2020 RIZAL
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package terminalfx.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import terminalfx.node.AdditionalNode;
import terminalfx.services.Service;
import terminalfx.services.impl.JdbcService;
import terminalfx.services.impl.TempService;
import terminalfx.services.model.Bookmark;
import terminalfx.services.model.Response;
import terminalfx.terminal.Terminal;
import terminalfx.terminal.CleanText;
import terminalfx.terminal.TFXCommand;

/**
 * FXML Controller class
 *
 * @author Rizal
 */
public class MainController implements Initializable {
    /**
     * Initializes the controller class.
     */
    //unused
    private final BooleanProperty ctrlPressed = new SimpleBooleanProperty(false);
    private final BooleanProperty cPressed = new SimpleBooleanProperty(false);
    private final BooleanBinding ctrlCPressed = ctrlPressed.and(cPressed);
    
    public  OutputStream os;
    public InputStream is;
    private boolean isTFX;
    private TFXCommand tfxCommand;
    private File folder;
    public List<String> tempCommandList;
    public String tempCommand;
    public int tempSize;
    private CleanText cleanText;
    private Service service;
    private AdditionalNode additionalNode;
    private List<Bookmark> bookmarks;
    @FXML
    private ScrollPane spBookmark;
    @FXML
    private TextField tfCommand;
    private Terminal terminal;
    @FXML
    private JFXTextArea taResult;
    @FXML
    private FontAwesomeIconView icoStar;
    @FXML
    private JFXButton bFolder;
    @FXML
    private VBox vbBookmark;
    @FXML
    private AnchorPane ap;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        isTFX = false;
        tempCommandList = new ArrayList<>();
        cleanText = new CleanText();
        //service = new TempService();
        service = new JdbcService();
        tfxCommand =new TFXCommand(service);
        additionalNode = new AdditionalNode(service);
        vbBookmark.setVisible(false);
        spBookmark.setVisible(false);
        initiate();
    }    
    private void initiate(){
        taResult.appendText("Type \""+tfxCommand.getKey()
                +" help\" for more information\n\n");
        //add delay to initiate terminal
        Timeline tl = new Timeline(new KeyFrame(Duration.ONE, (e) -> {
            Stage stage = (Stage) tfCommand.getScene().getWindow();
            stage.setTitle(new File(".").getAbsolutePath());
            terminal = new Terminal();
            Object[] obj = terminal.initiate();
            os = (OutputStream) obj[0];
            is = (InputStream) obj[1];
            Response response = service.create();
            if (!response.isStatus()) {
                taResult.appendText("Failed create db error: "+response.getMsg()+"\n"
                        + "Please delete tfx.db and restart app");
            }
            setStream();
            tabListener();
            checkStared();
            tfCommand.requestFocus();
        }));
        tl.setDelay(Duration.millis(600));
        tl.play();
        tfCommand.focusedProperty().addListener((o, old, nv) -> {
            if (nv){ vbBookmark.setVisible(!nv); spBookmark.setVisible(!nv);}
        });
        vbBookmark.visibleProperty().addListener((o, old, nv) -> {
            if (!nv) {spBookmark.setVisible(false);}
        });
    }
    
    //tab close listener
    private void tabListener(){
        Scene scene = ap.getScene();
        Node node = scene.lookup("#tabPane");
        TabPane tp = (TabPane) node;
        Tab tab = null;
        for (Tab t : tp.getTabs()) {
            if (t.getContent().equals(ap)) {
                tab = t;break;
            }
        }
        if (tab != null) {
            tab.setOnCloseRequest((ev) -> {terminal.forceKillProcess();});
        }
    }
    /**
     * Start stream input from pty4j, append to textArea
     * 
     */
    public void setStream(){
//        Thread t;
        BufferedReader input = new BufferedReader(new InputStreamReader(is));
        ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
        Runnable runnable = () -> {
            try {
                while (true){
                    String line;
                    line = input.readLine();
                    if (line != null) {
                        javafx.application.Platform.runLater(() -> {
                            taResult.appendText(cleanText.cleanWinText(line)+"\n");
                        });
                    }else{
                        taResult.appendText("Stream disconnected. "
                                + "open new tab to create new instance"+"\n");
                        break;
                    }
                }
                input.close();
                if (terminal != null) terminal.killProcess();
            } catch (IOException ex) {
                Logger.getLogger(Terminal.class.getName()).log(Level.SEVERE, null, ex);
            }
        };
        timer.schedule(runnable, 500, TimeUnit.MILLISECONDS);
//        t = new Thread(runnable);
//        t.start();
    }
    
    @FXML
    private void setFolder(ActionEvent event) {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory((folder == null)? new File(".") : folder);
        dc.setTitle("Choose directory");
        Stage thisStage = (Stage) tfCommand.getScene().getWindow();
        folder = dc.showDialog(thisStage);
        if (folder != null) {
            String s = folder.getAbsolutePath();
            bFolder.setText(s);
            terminal.write(terminal.getRoot(s), os);
            terminal.write("cd \""+s+"\"", os);
            Stage stage = (Stage) tfCommand.getScene().getWindow();
            stage.setTitle(s);
            checkStared();
        }
        tfCommand.requestFocus();
    }
    private void checkStared(){
        if (folder == null) {folder = new File(".");}
        if (bookmarks == null) {
            Response response = service.getBookmarks();
            bookmarks = (List<Bookmark>) response.getData();
        }
        boolean saved = false;
        for (Bookmark b : bookmarks) {
            if (b.getPath().equals(folder.getAbsolutePath())) {
                saved= true;
                break;
            }
        }
        if (saved) {
            FontAwesomeIconView ico = new FontAwesomeIconView(FontAwesomeIcon.STAR);
            ico.setFill(Paint.valueOf("#eed111"));
            bFolder.setGraphic(ico);
        }else{
            FontAwesomeIconView ico = new FontAwesomeIconView(FontAwesomeIcon.FOLDER);
            ico.setFill(Paint.valueOf("#000000"));
            bFolder.setGraphic(ico);
        }
    }
    @FXML
    private void tfCommandOnRelease(KeyEvent e) {
        if (null != e.getCode()) switch (e.getCode()) {
            case ENTER:
                String comm = tfCommand.getText().replaceAll(" ", "").replaceAll("\\.", "");
                if (comm.equalsIgnoreCase("cls") || comm.equalsIgnoreCase("clear")) {
                    taResult.clear();
                }else if (comm.equalsIgnoreCase("ls")) {
                    terminal.write("dir", os);
                }else if (comm.equalsIgnoreCase("cd")) {
                   taResult.appendText("cd command not support for now\n");
                }else if (comm.contains(tfxCommand.getKey())) {
                    String result = tfxCommand.getResult(tfCommand.getText(), 
                            tempCommandList);
                    if (result.startsWith(tfxCommand.getKey())) {
                        terminal.forceKillProcess();
                    }else{taResult.appendText(result);}
                    
                    //enable this if you wont save any (key) on temp
                    //isTFX = true;
                }else terminal.write(tfCommand.getText(), os);
                
                if (isTFX) {isTFX = false;}
                else{
                    if (!tfCommand.getText().isEmpty())tempCommandList.add(tfCommand.getText());
                }
                tfCommand.clear();
                break;
            case CONTROL: ctrlPressed.set(true);//unused
                break;
            case C: cPressed.set(true);//unused
                break;
            case UP: getTemp(true);
                break;
            case DOWN: getTemp(false);
                break;
                
        }
    }
    
    //get temporary command when press up/down
    private void getTemp(boolean isUp){
        if (isUp) {
            if (tempCommand == null) tempSize = tempCommandList.size() - 1;
            if (tempCommandList.size() <= tempSize) tempSize = tempCommandList.size() - 1;
            if (tempCommandList.size() > 0) {
                if (tempSize >= 0) {
                   tempCommand = tempCommandList.get(tempSize--);
                   tfCommand.setText(tempCommand);
                }
            }
        }else{
            if (tempCommand != null) {
                if (tempCommandList.size() > tempSize) {
                    if (tempSize < 0) tempSize = 1;
                    tempCommand = tempCommandList.get(tempSize++);
                    tfCommand.setText(tempCommand);
                }
            }
        }
        tfCommand.selectEnd();
    }
    
    @FXML
    private void openBookMark(ActionEvent event) {
        vbBookmark.setVisible(!vbBookmark.isVisible());
    }

    @FXML
    private void addBookmark(ActionEvent event) {
        if (folder == null) folder =  new File(".");
        Response response = service.saveBookmark(new Bookmark(folder.getAbsolutePath()));
        if (response.isStatus()) {
            FontAwesomeIconView ico = new FontAwesomeIconView(FontAwesomeIcon.STAR);
            ico.setFill(Paint.valueOf("#eed111"));
            bFolder.setGraphic(ico);
            taResult.appendText("Bookmark saved\n");
        }else{
            taResult.appendText("Bookmark failed: "+response.getMsg()+"\n");
        }
        tfCommand.requestFocus();
    }

    @FXML
    private void getBookmark(ActionEvent event) {
        spBookmark.setVisible(!spBookmark.isVisible());
        if (spBookmark.isVisible()) {
            spBookmark.setContent(null);
            spBookmark.setVvalue(0);spBookmark.setHvalue(0);
            Response response = service.getBookmarks();
            if (response.isStatus()) {
                bookmarks = (List<Bookmark>) response.getData();
                Object[] obj= additionalNode.getGridBookmarks(bookmarks);
                GridPane grid = (GridPane) obj[0];
                spBookmark.setContent(grid);
                setActionBookmark((List<HBox>) obj[1], grid);
            }else{
                taResult.appendText("get bookmark failed: "+response.getMsg()+"\n");
            }
        }
    }
    
    //set action all hbox saved path
    private void setActionBookmark(List<HBox> list, GridPane grid){
        list.forEach((hb) -> {
            JFXButton del =(JFXButton) hb.getChildren().get(0);
            JFXButton choose =(JFXButton) hb.getChildren().get(1);
            del.setOnAction((e) -> {
                int id = Integer.parseInt(del.getTooltip().getText());
                Response response = service.deleteBookmark(id);
                if (response.isStatus()){ 
                    grid.getChildren().remove(hb);
                    for (Bookmark b : bookmarks) {
                        if (choose.getText().equals(b.getPath())) {
                            bookmarks.remove(b);
                            checkStared();
                            break;
                        }
                    }
                }
                else{
                    taResult.appendText("delete failed: "+response.getMsg()+"\n");
                }
            });
            choose.setOnAction((e) -> {
                String s = choose.getText();
                bFolder.setText(s);
                terminal.write(terminal.getRoot(s), os);
                terminal.write("cd \""+s+"\"", os);
                folder = new File(s);
                Stage stage = (Stage) tfCommand.getScene().getWindow();
                stage.setTitle(s);
                FontAwesomeIconView ico = new FontAwesomeIconView(FontAwesomeIcon.STAR);
                ico.setFill(Paint.valueOf("#eed111"));
                bFolder.setGraphic(ico);
                tfCommand.requestFocus();
            });
        });
    }
}
