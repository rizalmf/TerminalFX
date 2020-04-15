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
package terminalfx.services.model;

/**
 *
 * @author RIZAL
 */
public class Bookmark {
    private int id_bookmark;
    private String path;

    public Bookmark() {
    }

    public Bookmark(String path) {
        this.path = path;
    }

    public int getId_bookmark() {
        return id_bookmark;
    }

    public void setId_bookmark(int id_bookmark) {
        this.id_bookmark = id_bookmark;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    
}
