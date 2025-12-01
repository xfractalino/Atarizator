/*
 *  Copyright (c) 2025/2026, fractalino
 * 
 *  This file is part of Atarizator.
 *  It is subject to the terms of the GNU General Public License v3.
 *  For details, see the LICENSE file at the root of the project.
 */

package com.fractalino.atarizator;

import com.fractalino.atarizator.gui.debugging.DebugWindow;
import java.awt.EventQueue;

/**
 *
 * @author fractalino
 */
public class Atarizator {

    public static void main(String[] args) {
        EventQueue.invokeLater(() ->
            DebugWindow.init()
        );
    }
}
