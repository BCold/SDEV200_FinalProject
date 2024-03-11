/*****************************************************************************
Program name: NoteKeeper
Author: Brandon Coldren
Date last updated: March 10th, 2024
Purpose: Provide a GUI that allows the user to create, edit, and delete notes.
******************************************************************************/

import javax.swing.SwingUtilities;

public class Main {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        NoteKeeperGUI gui = new NoteKeeperGUI();
        gui.showGUI();
      }
    });
  }
}