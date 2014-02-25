package com.chaseoes.dbo;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.swing.*;

import com.chaseoes.dbo.utilities.GeneralUtilities;

public class Main {

    public static final String hey = "HEY YOU GO AWAY I SEE YOU READING MY CODE";
    public static final String version = "1.0";
    public static final String key = "cc66f137b";
    public static final int refreshTime = 60;

    public static int projectCount = 0;
    public static int fileCount = 0;
    public static int teamCount = 0;

    public static boolean nagProjects = true;
    public static boolean nagFiles = false;
    public static boolean nagTeams = false;

    public static Nagger nagger;
    public static boolean naggerPaused = false;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        UIManager.put("swing.boldMetal", Boolean.FALSE);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        if (!SystemTray.isSupported()) {
            return;
        }

        final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon = new TrayIcon(GeneralUtilities.createImage("/tray.png", "tray icon"));
        final SystemTray tray = SystemTray.getSystemTray();

        MenuItem aboutItem = new MenuItem("About");
        CheckboxMenuItem paused = new CheckboxMenuItem("Muted");
        CheckboxMenuItem nagProjects = new CheckboxMenuItem("Nag Projects");
        nagProjects.setState(true);
        CheckboxMenuItem nagFiles = new CheckboxMenuItem("Nag Files");
        CheckboxMenuItem nagTeams = new CheckboxMenuItem("Nag Teams");
        MenuItem exitItem = new MenuItem("Exit");

        popup.add(aboutItem);
        popup.addSeparator();
        popup.add(paused);
        popup.addSeparator();
        popup.add(nagProjects);
        popup.add(nagFiles);
        popup.add(nagTeams);
        popup.addSeparator();
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);
        trayIcon.setToolTip("Loading...");

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
            return;
        }

        trayIcon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("http://dev.bukkit.org/admin/approval-queue/"));
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }
        });

        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "DBO Tray Notifier by chaseoes. Version " + version + ".");
            }
        });

        nagProjects.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                int cb1Id = e.getStateChange();
                if (cb1Id == ItemEvent.SELECTED){
                    Main.nagProjects = true;
                } else {
                    Main.nagProjects = false;
                }
            }
        });

        nagFiles.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                int cb1Id = e.getStateChange();
                if (cb1Id == ItemEvent.SELECTED){
                    Main.nagFiles = true;
                } else {
                    Main.nagFiles = false;
                }
            }
        });

        nagTeams.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                int cb1Id = e.getStateChange();
                if (cb1Id == ItemEvent.SELECTED){
                    Main.nagTeams = true;
                } else {
                    Main.nagTeams = false;
                }
            }
        });

        paused.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                int cb1Id = e.getStateChange();
                if (cb1Id == ItemEvent.SELECTED){
                    naggerPaused = true;
                } else {
                    naggerPaused = false;
                }
            }
        });

        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tray.remove(trayIcon);
                System.exit(0);
            }
        });

        updateCounts(trayIcon);
        nag(trayIcon);
        nagger = new Nagger(trayIcon, refreshTime);
    }

    public static void nag(TrayIcon trayIcon) {
        String message = null;
        if (Main.nagProjects) {
            if (projectCount > 0) {
                message = "There are " + projectCount + " unapproved projects.";
            }
        }

        if (Main.nagFiles) {
            if (fileCount > 0) {
                if (message == null) {
                    message = "There are " + fileCount + " unapproved files.";
                } else {
                    message = message.substring(0, message.length() -1) + ", " + fileCount + " files.";
                }
            }
        }

        if (Main.nagTeams) {
            if (teamCount > 0) {
                if (message == null) {
                    message = "There are " + teamCount + " unapproved teams.";
                } else {
                    message = message.substring(0, message.length() -1) + ", " + teamCount + " teams.";
                }
            }
        }

        if (message != null) {
            GeneralUtilities.play("/ding.wav");
            trayIcon.displayMessage("BukkitDev Approval Queue", message, TrayIcon.MessageType.INFO);
        }
    }

    public static void updateCounts(TrayIcon trayIcon) {
        try {
            URL url;
            url = new URL("http://dbo.chaseoes.com/api/v1/?key=" + key);
            InputStream is = url.openStream();
            JsonReader rdr = Json.createReader(is);
            JsonObject obj = rdr.readObject();
            projectCount = Integer.parseInt(obj.get("project_count").toString());
            fileCount = Integer.parseInt(obj.get("file_count").toString());
            // teamCount = Integer.parseInt(obj.get("team_count").toString());

            trayIcon.setToolTip("Projects: " + projectCount + " Files: " + fileCount + " Teams: " + teamCount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
