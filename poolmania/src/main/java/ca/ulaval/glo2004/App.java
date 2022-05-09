package ca.ulaval.glo2004;


import ca.ulaval.glo2004.gui.MainWindow;

import javax.swing.*;

public class App {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MainWindow fenetrePrincipale = new MainWindow();
                fenetrePrincipale.setVisible(true);
            }
        });
    }
}

