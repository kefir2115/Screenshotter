package me.kefir.screenshotter;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import java.awt.*;
import java.awt.event.*;

public class Main {

    static Frame win;
    public static void main(String[] args) {
        win = new Frame();
        win.setVisible(true);

        try {
            GlobalScreen.registerNativeHook();
        } catch(NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }
        GlobalScreen.addNativeKeyListener(new KeyListener());
        win.addMouseListener(new MouseListener() {
            @Override public void mouseClicked(MouseEvent e) {}
            @Override public void mouseEntered(MouseEvent e) {}
            @Override public void mouseExited(MouseEvent e) {}
            @Override public void mousePressed(MouseEvent e) {
                win.handleMouse(e.getPoint(), 0);
            }
            @Override public void mouseReleased(MouseEvent e) {
                win.handleMouse(e.getPoint(), 1);
            }
        });
        win.addMouseMotionListener(new MouseMotionListener() {
            @Override public void mouseDragged(MouseEvent e) {
                win.handleMouse(e.getPoint(), 2);
            }
            @Override public void mouseMoved(MouseEvent e) {}
        });
    }

    public static class KeyListener implements NativeKeyListener {
        public void nativeKeyPressed(NativeKeyEvent e) {
//            System.out.println("Key Pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
        }

        public void nativeKeyReleased(NativeKeyEvent e) {
//            System.out.println("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
            int c = e.getKeyCode();
//            System.out.println(c);
            if(c==13) {
                win.setPaneVisible(!win.isPaneVisible());
            }
            if(c==1) {
                win.setPaneVisible(false);
            }
        }
    }
}
