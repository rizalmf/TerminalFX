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
package terminalfx.services.impl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import terminalfx.services.Service;
import terminalfx.services.model.Bookmark;
import terminalfx.services.model.Command;
import terminalfx.services.model.Response;

/**
 *
 * @author RIZAL
 * 
 * Data temporary
 */
public class TempService implements Service{
    private static ObservableList<Bookmark> bookmarkList;
    private static ObservableList<Command> commandList;
    
    public TempService() {
    }

    @Override
    public Response saveCommand(Command command) {
        for (Command c : commandList) {
            if (c.getText().equals(command.getText()))
                return new Response(false, null, "Command already saved");
        }
        command.setId_command(commandList.size());
        commandList.add(command);
        return new Response(true, null, "Command saved");
    }

    @Override
    public Response deleteCommand(int id) {
        commandList.remove(id);
        return new Response(true, null, "Command deleted");
    }

    @Override
    public Response deleteAllCommand() {
        commandList.clear();
        return new Response(true, null, "All command deleted");
    }

    @Override
    public Response getCommands() {
        return new Response(true, commandList, null);
    }

    @Override
    public Response saveBookmark(Bookmark bookmark) {
        for (Bookmark b : bookmarkList) {
            if (b.getPath().equals(bookmark.getPath()))
                return new Response(false, null, "Path already saved");
        }
        bookmark.setId_bookmark(bookmarkList.size());
        bookmarkList.add(bookmark);
        return new Response(true, null, null);
    }

    @Override
    public Response deleteBookmark(int id) {
        bookmarkList.remove(id);
        return new Response(true, null, null);
    }

    @Override
    public Response getBookmarks() {
        return new Response(true, bookmarkList, null);
    }

    @Override
    public Response create() {
        bookmarkList = FXCollections.observableArrayList();
        commandList = FXCollections.observableArrayList();
        return new Response(true, bookmarkList, null);
    }
    
}
