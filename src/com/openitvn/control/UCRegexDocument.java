/*
 * Copyright (C) 2019 Thinh
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
package com.openitvn.control;

import java.util.regex.Pattern;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 *
 * @author Thinh
 */
class UCRegexDocument extends PlainDocument {
    
    static String REGEX_INTEGER = "^-?(\\d+)?$";
    static String REGEX_FLOAT = "^-?(\\d+)?\\.?(\\d+)?$";
    
    private final Pattern regex;
    
    UCRegexDocument(String regex) {
        this.regex = Pattern.compile(regex);
    }
    
    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        StringBuilder sb = new StringBuilder(getText(0, getLength()));
        String txt = sb.insert(offs, str).toString();
        if (regex.matcher(txt).matches())
            super.insertString(offs, str, a);
    }
}
