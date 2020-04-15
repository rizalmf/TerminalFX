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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import terminalfx.services.Service;
import terminalfx.services.model.Bookmark;
import terminalfx.services.model.Command;
import terminalfx.services.model.Response;

/**
 *
 * @author RIZAL
 * 
 * Using SQL lite
 */
public class JdbcService implements Service{
    private Connection connection;
    private PreparedStatement statement;
    private Connection getConnection(){
        try {
            if (connection == null) {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection("jdbc:sqlite:tfx.db");
            }else{
                if (connection.isClosed()) {
                    Class.forName("org.sqlite.JDBC");
                    connection = DriverManager.getConnection("jdbc:sqlite:tfx.db");
                }
            }
        } catch (Exception e) {
            Logger.getLogger(JdbcService.class.getName()).log(Level.SEVERE, null, e);
        }
        return connection;
    }
    private void close(){
        if (connection != null) {
            try {
                connection.close();
                System.out.println("closing connection session");
            } catch (Exception ex) {
                Logger.getLogger(JdbcService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @Override
    public Response create(){
        Response response;
        try {
            String createCommand = "CREATE TABLE IF NOT EXISTS `command` (" +
                "  id integer primary key autoincrement," +
                "  text varchar" +
                ")";
            statement = getConnection().prepareStatement(createCommand);
            statement.executeUpdate();
            
            String createBookmark= "CREATE TABLE IF NOT EXISTS `bookmark` (" +
                "  id integer primary key autoincrement," +
                "  path varchar" +
                ")";
            statement = getConnection().prepareStatement(createBookmark);
            statement.executeUpdate();
            
            response = new Response(true, null, "");
        } catch (Exception e) {
            response = new Response(false, null, e.getMessage());
            //Logger.getLogger(JdbcService.class.getName()).log(Level.SEVERE, null, e);
        }
        return response;
    }
    
    @Override
    public Response saveCommand(Command command) {
        Response response;
        try {
            String sql = "insert into command values(null, ?)";
            statement = getConnection().prepareStatement(sql);
            statement.setString(1, command.getText());
            statement.executeUpdate();
            response = new Response(true, null, "Command saved");
        } catch (Exception e) {
            response = new Response(false, null, e.getMessage());
        }
        return response;
    }

    @Override
    public Response deleteCommand(int id) {
        Response response;
        try {
            String sql = "delete from command where id=?";
            statement = getConnection().prepareStatement(sql);
            statement.setInt(1, id);
            statement.executeUpdate();
            response = new Response(true, null, "Command deleted");
        } catch (Exception e) {
            response = new Response(false, null, e.getMessage());
        }
        return response;
    }

    @Override
    public Response deleteAllCommand() {
        Response response;
        try {
            String deleteAll = "delete from command";
            statement = getConnection().prepareStatement(deleteAll);
            statement.executeUpdate();
            getConnection().close();
            response = new Response(true, null, "All command deleted");
        } catch (Exception e) {
            response = new Response(false, null, e.getMessage());
            //Logger.getLogger(JdbcService.class.getName()).log(Level.SEVERE, null, e);
        }
        return response;
    }

    @Override
    public Response getCommands() {
        Response response;
        try {
            statement = getConnection().prepareStatement("select * from command order by id asc"); 
            List<Command> list = new ArrayList<>();
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {                
                Command c = new Command();
                c.setId_command(rs.getInt("id"));
                c.setText(rs.getString("text"));
                list.add(c);
            }
            response = new Response(true, list, "List command found");
        } catch (Exception e) {
            response = new Response(false, new ArrayList<>(), e.getMessage());
        }
        return response;
    }

    @Override
    public Response saveBookmark(Bookmark bookmark) {
        Response response;
        try {
            String sql = "insert into bookmark values(null, ?)";
            statement = getConnection().prepareStatement(sql);
            statement.setString(1, bookmark.getPath());
            statement.executeUpdate();
            response = new Response(true, null, "Path bookmarked");
        } catch (Exception e) {
            response = new Response(false, null, e.getMessage());
        }
        return response;
    }

    @Override
    public Response deleteBookmark(int id) {
        Response response;
        try {
            String sql = "delete from bookmark where id=?";
            statement = getConnection().prepareStatement(sql);
            statement.setInt(1, id);
            statement.executeUpdate();
            response = new Response(true, null, "Bookmark deleted");
        } catch (Exception e) {
            response = new Response(false, null, e.getMessage());
        }
        return response;
    }

    @Override
    public Response getBookmarks() {
        Response response;
        try {
            statement = getConnection().prepareStatement("select * from bookmark order by id asc"); 
            List<Bookmark> list = new ArrayList<>();
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {                
                Bookmark b = new Bookmark();
                b.setId_bookmark(rs.getInt("id"));
                b.setPath(rs.getString("path"));
                list.add(b);
            }
            response = new Response(true, list, "List bookmark found");
        } catch (Exception e) {
            response = new Response(false, new ArrayList<>(), e.getMessage());
        }
        return response;
    }
    
}
