/*
 *  Copyright (c) 2025/2026, fractalino
 * 
 *  This file is part of Atarizator.
 *  It is subject to the terms of the GNU General Public License v3.
 *  For details, see the LICENSE file at the root of the project.
 */
package com.fractalino.atarizator.gui;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

import org.lwjgl.glfw.GLFWErrorCallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fractalino
 */
public class Window {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Window.class);
    
    private final long handle;
    
    public Window() {
        if(!GLFWSystem.isInitialized()) {
            LOGGER.error("Can't create Window: uninitialized GLFW.");
            handle = NULL;
            return;
        }
        
        handle = create();
    }
    
    private static long create() {
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        
        final var h = glfwCreateWindow(
                400, 300, 
                "Atarizator", 
                NULL, NULL);
        if(h == NULL){
            GLFWSystem.terminate();
            throw new AtarizatorInitializationException("Couldn't create Window");
        }
        
        glfwMakeContextCurrent(h);
        glfwSwapInterval(1);
        glfwShowWindow(h);
        
        return h;
    }
    
    public void loop(Runnable r) {
        do {
            glfwPollEvents();
            r.run();
        } while(!shouldClose());
    }
    
    public boolean shouldClose() {
        return glfwWindowShouldClose(handle);
    }
    
    public long getHandle() {
        return handle;
    }
    
    public void destroy() {
        if(handle != NULL)
            glfwDestroyWindow(handle);
    }
}
