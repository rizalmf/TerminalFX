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
package terminalfx.terminal;

import com.pty4j.PtyProcess;
import com.pty4j.WinSize;
import com.sun.jna.Platform;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import terminalfx.controller.MainController;

/**
 *
 * @author Rizal
 */
public class Terminal {
    private PtyProcess pty;
    
    public Object[] initiate(){
        // The command to run in a PTY...
        String[] cmdWin = { "cmd.exe", "-verb runAs" };
        String[] cmdLinux = { "/bin/sh", "-l" };
        String[] cmd = Platform.isWindows() ? cmdWin : cmdLinux;
        
        // The initial environment to pass to the PTY child process...
        String[] env = { "TERM=xterm" }; //unused
        try {
            //, env
            pty = PtyProcess.exec(cmd);
            //much column,row. standard (80,1)
            pty.setWinSize(new WinSize(220, 1)); 
        } catch (IOException ex) {
            Logger.getLogger(Terminal.class.getName()).log(Level.SEVERE, null, ex);
        }
        Object[] obj = new Object[2];
        obj[0] = pty.getOutputStream();
        obj[1] = pty.getInputStream();
        return obj;
    }
    public void killProcess(){
        // wait until the PTY child process terminates...
        //int result = pty.waitFor();
        System.out.println("kill process");
        pty.destroy(); //close(); which version??
    }
    public void forceKillProcess(){  
        System.out.println("force kill process");
        pty.destroyForcibly();
    }
    public String getRoot(String text){
        String root = "";
        if (Platform.isWindows()) {
            for (int i = 0; i < text.length(); i++) {
                String c = text.charAt(i)+"";
                root += c;
                if(c.equals(":")){
                    break;
                }
            }
        }
        return root;
    }
    public void write(String text, OutputStream os){
        try {
            os.write((text+ "\r").getBytes(StandardCharsets.UTF_8));
            os.flush();
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
