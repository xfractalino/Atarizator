/*
 *  Copyright (c) 2025/2026, fractalino
 * 
 *  This file is part of Atarizator.
 *  It is subject to the terms of the GNU General Public License v3.
 *  For details, see the LICENSE file at the root of the project.
 */
package com.fractalino.atarizator.gui;

/**
 *
 * @author fractalino
 */
public class AtarizatorInitializationException extends RuntimeException {
    public AtarizatorInitializationException() {
        super();
    }

    public AtarizatorInitializationException(String message) {
        super(message);
    }

    public AtarizatorInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AtarizatorInitializationException(Throwable cause) {
        super(cause);
    }

    protected AtarizatorInitializationException(String message, Throwable cause,
                               boolean enableSuppression,
                               boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
