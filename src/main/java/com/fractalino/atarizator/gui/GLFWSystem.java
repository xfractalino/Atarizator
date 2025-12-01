/*
 *  Copyright (c) 2025/2026, fractalino
 * 
 *  This file is part of Atarizator.
 *  It is subject to the terms of the GNU General Public License v3.
 *  For details, see the LICENSE file at the root of the project.
 */
package com.fractalino.atarizator.gui;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.GLFWErrorCallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fractalino
 */
public final class GLFWSystem {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(GLFWSystem.class);
    
    private static Status status = Status.UNINIT;
    
    private GLFWSystem() {}
    
    public static void init() {
        if(status != Status.UNINIT) {
            LOGGER.warn("init() called on initialized or terminated GLFW.");
            return;
        }
        
        LOGGER.info("Initializing GLFW");
        
        GLFWErrorCallback.create((code, msg) -> {
            LOGGER.error("GLFW Error {}: {}", code, msg);
        });
        
        if(!glfwInit())
            throw new AtarizatorInitializationException("Can't initialize GLFW");
        
        status = Status.INITIALIZED;
    }
    
    public static void terminate() {
        if(status == Status.UNINIT) {
            LOGGER.warn("terminate() called on uninitialized GLFW");
            return;
        } else if(status == Status.TERMINATED) {
            LOGGER.warn("terminate() called on terminated GLFW");
            return;
        }   
        
        glfwTerminate();
        
        status = Status.TERMINATED;
    }
    
    public static Status getStatus() {
        return status;
    }
    
    public static boolean isInitialized() {
        return getStatus() == Status.INITIALIZED;
    }
    
    public static enum Status {
        UNINIT, INITIALIZED, TERMINATED
    }
    
}
