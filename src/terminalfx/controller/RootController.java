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

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import terminalfx.TerminalFX;

/**
 * FXML Controller class
 *
 * @author RIZAL
 */
public class RootController implements Initializable {

    @FXML
    private TabPane tabPane;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        add();
    }    
    private void add(){
        try {
            Parent main = FXMLLoader.load(TerminalFX.class.getResource("views/main.fxml"));
            int size = tabPane.getTabs().size()+1;
            Tab tab = new Tab("Terminal "+size);
            tab.setContent(main);
            tabPane.getTabs().add(tab);
        } catch (Exception ex) {
            Logger.getLogger(RootController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @FXML
    private void addTerminal(ActionEvent event) {
        add();
    }
    
}
