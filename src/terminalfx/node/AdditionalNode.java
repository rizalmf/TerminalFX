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
package terminalfx.node;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import terminalfx.services.Service;
import terminalfx.services.model.Bookmark;

/**
 *
 * @author RIZAL
 */
public class AdditionalNode {
    private final Service service;
    
    public AdditionalNode(Service service) {
        this.service = service;
    }
    
    //unused. soon :D
    public TreeItem<String> getNodeDirectory(File dir){
        TreeItem<String> root = new TreeItem<>(dir.getName());
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                root.getChildren().add(getNodeDirectory(f));
            }else{
                root.getChildren().add(new TreeItem<>(f.getName()));
            }
        }
        return root;
    }
    
    public Object[] getGridBookmarks(List<Bookmark> list){
        Object[] obj = new Object[2];
        List<HBox> hbs = new ArrayList<>();
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(0));
        grid.setVgap(0);
        grid.setHgap(0);
        grid.setStyle("-fx-alignment: center;");
        int row = 0;
        int col = 0;
        for (Bookmark b : list) {
            FontAwesomeIconView ico = new FontAwesomeIconView(FontAwesomeIcon.MINUS_CIRCLE);
            ico.setFill(Paint.valueOf("#000000"));
            ico.setSize("10");
            JFXButton delBookmark = new JFXButton(" ", ico);
            delBookmark.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            delBookmark.setPrefWidth(20);
            delBookmark.setTooltip(new Tooltip(b.getId_bookmark()+""));
            JFXButton path = new JFXButton(b.getPath());
            path.setId("left");
            path.setPrefWidth(205);
            path.setTooltip(new Tooltip(b.getPath()));
            HBox hbox = new HBox(delBookmark, path);
            hbox.setId("hid");
            hbs.add(hbox);
            grid.add(hbox, col, row++);
        }
        obj[0] = grid;
        obj[1] = hbs;
        return obj;
    }
}
