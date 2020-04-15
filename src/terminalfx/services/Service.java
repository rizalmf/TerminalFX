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
package terminalfx.services;

import java.util.List;
import terminalfx.services.model.Bookmark;
import terminalfx.services.model.Command;
import terminalfx.services.model.Response;

/**
 *
 * @author RIZAL
 */
public interface Service {
    Response create();
    
    Response saveCommand(Command command);
    Response deleteCommand(int id);
    Response deleteAllCommand();
    Response getCommands();
    
    Response saveBookmark(Bookmark bookmark);
    Response deleteBookmark(int id);
    Response getBookmarks();
}
