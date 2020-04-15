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

import com.google.common.base.Ascii;
import com.sun.jna.Platform;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author RIZAL
 */
public class CleanText {
    public String cleanWinText(@NotNull String text) {
        if (Platform.isWindows()) {
            //ANSI color
            text = text.replace("\u001B[0m", "").replace("\u001B[0K", "")
              .replace("\u001B[?25l", "").replace("\u001b[?25h", "").replace("\u001b[1G", "")
              .replace("[0;34;94m", "").replace("[0;1m", "").replace("[23G", "").replace("[0;31;91m", "")
              .replace("[34G", "").replace("[12G", "").replace("[25G", "").replace("[14G", "")
              .replace("[30G", "").replace("[73G", "").replace("[11G", "").replace("[0;32m", "")
              .replace("[3G", "").replace("[10G", "").replace("[8G", "");
            int oscInd = 0;
            do {
                oscInd = text.indexOf("\u001b]0;", oscInd);
                int bellInd = oscInd >= 0 ? text.indexOf(Ascii.BEL, oscInd) : -1;
                if (bellInd >= 0) {
                    text = text.substring(0, oscInd) + text.substring(bellInd + 1);
                }
            } while (oscInd >= 0);
        }
        return text;
    }
}
