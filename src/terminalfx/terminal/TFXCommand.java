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

import java.util.List;
import terminalfx.services.Service;
import terminalfx.services.model.Command;
import terminalfx.services.model.Response;

/**
 *
 * @author RIZAL
 */
public class TFXCommand {
    //key to access tfxCommand
    private final String key = "-tfx";
    private final String version = "0.0.1 alpha";
    private final Service service;

    public TFXCommand(Service service) {
        this.service = service;
    }
    
    public String getKey(){
        return key;
    }
    public String getResult(String com, List<String> list){
        com = com.replace(key, "");
        String result = "";
        if (com.contains("help")) { result = getHelper(); }
        else if(com.contains("temp-delete")){ result = getTempDelete(list); }
        else if(com.contains("temp")){ result = getTemp(list); }
        else if(com.contains("about")){ result = getAboutMe(); }
        //else if(com.contains("initiate")){ result = getRefresh(); }
        else if(com.contains("save ")){ result = saveCommand(com.replaceAll("save ", "")); }
        else if(com.contains("show")){ result = getCommands(); }
        else if(com.contains("delete-all")){ result = deleteAllCommand(); }
        else if(com.contains("delete ")){ result = deleteCommand(com.replaceAll("delete ", "")); }
        else if(com.contains("stop")){ result = stopProcess(); }
        else { result = getHelper(); }
        return result+"\n";
    }
    private String getHelper(){
        String result = "terminalFX helper list command: \n"
                + "  run as root/administrator to grant all UAC support\n"
                + "  about                    (about this app)\n"
                + "  delete [command_id]      (delete your saved command)\n"
                + "  delete-all               (delete all your saved command)\n"
                + "  help                     (provides informations terminalFX command)\n"
//                + "  initiate                 (initiate new cmd process. kill all preview process)\n"
                + "  show                     (displays your saved command)\n"
                + "  save [command]           (save your commands)\n"
                + "  stop                     (stop current process and kill) \n"
//                + "consider call initiate or add new tab after use this process)\n"
                + "  temp                     (displays your last execute command) \n"//("+key+") not included)
                + "  temp-delete              (delete all temp command)\n";
        return result;
    }
    private String getTemp(List<String> list){
        String result = "List temp executed command \n";
        for (int i = 0; i < list.size(); i++) {
            result += "  "+(i+1)+". "+list.get(i)+"\n";
        }
        return result;
    }
    private String getTempDelete(List<String> list){
        String result = "temp command cleared \n";
        list.clear();
        return result;
    }
    private String getAboutMe(){
        String result = "TerminalFX version "+version+" \n"
                +" This app is open source and dedicated for programmers."
                +" I am very welcome for any feedback to make this app better."
                +" Anyway, dont forget to be happy :D \n\n Rizal Maulana Fahmi\n";
        return result;
    }
    private String getRefresh(){
        return null;
    }
    private String saveCommand(String com){
        Response response = service.saveCommand(new Command(com));
        String result = response.getMsg();
        return result;
    }
    private String getCommands(){    
        Response response = service.getCommands();
        String result;
        if (response.isStatus()) {
            result = "List saved commands \n\n"
                    + "_id      _command\n";
            List<Command> list = (List<Command>) response.getData();
            for (Command c : list) {
                result += " "+c.getId_command()+".      "+c.getText()+"\n";
            }
        }else result = response.getMsg();
        return result;
    }
    private String deleteAllCommand(){
        Response response = service.deleteAllCommand();
        String result = response.getMsg();
        return result;
    }
    private String deleteCommand(String id){
        id = id.replaceAll(" ", "").replaceAll("\\.", "");
        Response response = service.deleteCommand(Integer.parseInt(id));
        String result = response.getMsg();
        return result;
    }
    private String stopProcess(){
        return key;
    }
}
