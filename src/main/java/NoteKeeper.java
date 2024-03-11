import javax.swing.SwingUtilities;

public class NoteKeeper {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        NoteKeeperGUI gui = new NoteKeeperGUI();
        gui.showGUI();
      }
    });
  }
}