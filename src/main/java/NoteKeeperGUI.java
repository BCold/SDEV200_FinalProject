import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException; 
import java.io.FileWriter;
import java.io.FileNotFoundException; 
import java.util.Scanner;

class NoteKeeperGUI extends JFrame{
  // Initialization
  private JLabel noteTitleLbl, noteBodyLbl, noteListLbl;
  private JTextField noteTitleTxt;
  private JTextArea noteBodyTxt;
  private JTable noteListTbl;
  private JButton saveBtn, deleteBtn, clearBtn;
  private JPanel parentPanel, noteEditorPanel, noteListPanel, buttonPanel;

  ArrayList<Note> notes = new ArrayList<Note>(); // ArrayList of Notes
  String notesDirectory = System.getProperty("user.dir") + File.separator + "Notes" + File.separator;
  File createNewDirectory = new File(notesDirectory);
  boolean test = createNewDirectory.mkdirs();
  //String notesDirectory = "src/main/notes/"; // Path to notes directory

  // Create NoteKeeper GUI
  public void showGUI(){

    // Define JFrame properties
    setTitle("Note Keeper");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(500, 400);
    setLayout(new GridLayout(1,1));
    setLocationRelativeTo(null);

    parentPanel = new JPanel();
    parentPanel.setSize(new Dimension(getSize()));
    parentPanel.setLayout(new BorderLayout(5,5));

    // Define note editor panel
    noteEditorPanel = new JPanel();
    noteEditorPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 640, 5));

    // Define the components to be added to the notes editor panel
    noteTitleLbl = new JLabel("Title");
    noteTitleLbl.setFont(new Font("Arial", Font.BOLD, 12));
    
    noteTitleTxt = new JTextField(20);
    

    noteBodyLbl = new JLabel("Body");
    noteBodyLbl.setFont(new Font("Arial", Font.BOLD, 12));

    noteBodyTxt = new JTextArea(17, 32);
    noteBodyTxt.setLineWrap(true);
    noteBodyTxt.setWrapStyleWord(true);
    JScrollPane noteBodyScrollPane = new JScrollPane(noteBodyTxt);

    // Add components to the note editor panel

    noteEditorPanel.add(noteTitleLbl);
    noteEditorPanel.add(noteTitleTxt);
    noteEditorPanel.add(noteBodyLbl);
    noteEditorPanel.add(noteBodyScrollPane);

    // Define the button panel
    buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

    // Define the components to be added to the button panel

    clearBtn = new JButton("Clear");
    saveBtn = new JButton("Save");
    deleteBtn = new JButton("Delete");

    // Add components to the button panel
    buttonPanel.add(clearBtn);
    buttonPanel.add(saveBtn);
    buttonPanel.add(deleteBtn);

    // Add button panel to the note editor panel
    noteEditorPanel.add(buttonPanel);


    // Define the note list panel
    noteListPanel = new JPanel();
    noteListPanel.setLayout(new BorderLayout());

    // Define the components to be added to the notes list panel
    noteListLbl = new JLabel("Notes", SwingConstants.CENTER);
    noteListLbl.setFont(new Font("Arial", Font.BOLD, 12));
    
    noteListTbl = new JTable();
    
    JScrollPane noteListScrollPane = new JScrollPane(noteListTbl);
    noteListScrollPane.setVisible(true);
    noteListScrollPane.setPreferredSize(new Dimension(120, 100));

    // Add components to the notes list panel
    noteListPanel.add(noteListLbl, BorderLayout.NORTH);
    noteListPanel.add(noteListScrollPane, BorderLayout.CENTER);

    // Add panels to the JFrame
    parentPanel.add(noteEditorPanel, BorderLayout.CENTER);
    parentPanel.add(noteListPanel, BorderLayout.LINE_START);

    add(parentPanel);

    PopulateNotes();

    // Display the JFrame
    setVisible(true);

    // Add event listeners 
    
      parentPanel.addComponentListener(new ComponentAdapter() 
      {  
              public void componentResized(ComponentEvent evt) {
                 parentPanel.setSize(new Dimension(getSize()));
                 noteEditorPanel.setLayout(new FlowLayout(FlowLayout.CENTER, parentPanel.getWidth(), 5));

              }
      });

    
    clearBtn.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        // If editor fields are not empty, clear them. Otherwise provide feedback to the user.
        if(noteTitleTxt.getText().isEmpty() && noteBodyTxt.getText().isEmpty()){
          DisplayErrorDialog("Note Editor Fields are already empty.", "Error");
        }
        else if(DisplayConfirmDialog("Are You Sure You Want to Clear The Note Editor Fields?", "Clear Editor?") == 0){
          ClearNoteEditor();
        }
      }
    });
    
    saveBtn.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        CreateNote();
      }
    });

    deleteBtn.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        // If note exists, confirm delete action with user before deletion. Otherwise inform the user that the note does not exist.
        boolean noteExists = CheckNoteExists();
        
        if(noteExists){
          if(DisplayConfirmDialog("Are you sure you want to delete this note?", "Delete Note?") == 0){
            DeleteNote();
          }
        }
        else{
            DisplayErrorDialog("The Note You Are Trying to Delete Doesn't Exist.", "Error");  
        }
      }
    });

    noteListTbl.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mousePressed(java.awt.event.MouseEvent event) {
            // If the user clicks on a row in the table, display the chosen note in the note editor panel.
            int row = noteListTbl.rowAtPoint(event.getPoint());
            if (row >= 0) {
                int modelRow = noteListTbl.convertRowIndexToModel(row);
                Note note = notes.get(modelRow);
                noteTitleTxt.setText(note.getTitle());
                noteBodyTxt.setText(note.getBody());
            }
        }
    });
  }


  // Load content from a note file and use to create and add a note object to the notes ArrayList
  private void LoadNote(File file){
    // Initialization
    StringBuilder noteFileContent = new StringBuilder();
    String noteFileName = file.getName();
    int extensionPos = noteFileName.lastIndexOf(".");

    try {
        File noteFile = new File(notesDirectory + file.getName());
        Scanner noteFileReader = new Scanner(noteFile);

      // Add the content of the note file to the noteFileContent StringBuilder line by line.
        while (noteFileReader.hasNextLine()) {
          String data = noteFileReader.nextLine();
          noteFileContent.append(data + "\n");
        }

      // Remove the file extension from noteFileName before creating the note object.
      if (extensionPos > 0 && extensionPos < (noteFileName.length() - 1)) { // If '.' is not the first or last character.
          noteFileName = noteFileName.substring(0, extensionPos);
          System.out.println(noteFileName);
          notes.add(new Note(noteFileName, noteFileContent.toString()));
          System.out.println(notes.get(notes.size()-1));
      }

      // Close the file reader
      noteFileReader.close();

    } catch (FileNotFoundException e) {
        System.out.println("An error occurred.");
        e.printStackTrace();
    }
  }
  
  // Create a new note
  private void CreateNote(){
    // If note title is invalid display error message. Otherwise, create a new note.
    if(ValidateNoteTitle()){
      
        if(CheckNoteExists()){
          DisplayErrorDialog("A note with that name already exists.", "Error");
        }

        else{
          notes.add(new Note (noteTitleTxt.getText(), noteBodyTxt.getText())); 
          // Save note to a file
          SaveNote(notes.get(notes.size()-1));
          //Update the notes list and clear the note editor fields
          PopulateNotesTable(notes);
          ClearNoteEditor();
        }
    }
  }
    
  // Save the passed note object to a file.
  private void SaveNote(Note note){
    
    // Create a file. Catch any IO exceptions
    try {
      File noteFile = new File(notesDirectory + note.getTitle() + ".txt");
      noteFile.createNewFile();
      
    } catch (IOException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
    
    // Write to the file. Catch any IO exceptions
    try {
      FileWriter noteWriter = new FileWriter(notesDirectory + note.getTitle() + ".txt");
      noteWriter.write(note.getBody());
      noteWriter.close();
      
    } catch (IOException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
  }

  // Deletes a note object and it's associated file.
  private void DeleteNote(){
    // Initialization
    File noteFile = new File(notesDirectory + noteTitleTxt.getText() + ".txt");
    
    // Delete the file. Provider user feedback that the file has been deleted.
      if(noteFile.delete()){
        DisplayInfoDialog("File '" + noteFile.getName() + "'" + " Successfully Deleted.", "File Deleted");
      System.out.println("Deleted the file: " + noteFile.getName());
      }

    // Delete the note from the notes ArrayList
    for(Note note: notes){
      if(noteTitleTxt.getText().equals(note.getTitle())){
        notes.remove(note);
        break;
      }
    }
    // Update the notes table and clear the note editor fields
    PopulateNotesTable(notes);
    ClearNoteEditor();
  }

  // Check that a note exists
  private boolean CheckNoteExists(){
    if(notes.size() > 0){
      for(Note note: notes){
        if(noteTitleTxt.getText().equals(note.getTitle()) & noteBodyTxt.getText().equals(note.getBody())){
          return true;
        }
      }
    }
    return false;
  }  

  // Checks whether a note title is legal
  private boolean ValidateNoteTitle(){
    boolean isValid = false;

    if(noteTitleTxt.getText().isEmpty()){
      DisplayErrorDialog("Title cannot be empty", "Error");
    }
    
    else if(noteTitleTxt.getText().contains(".")){
      DisplayErrorDialog("Title cannot contain a period.", "Error");

    }

    else if(noteTitleTxt.getText().charAt(0) == ' '){
      DisplayErrorDialog("Title Cannot Begin With a Space.", "Error");
    }

    else{
      isValid = true;
    }

    return isValid;
    
  }

  // Populate the notes ArrayList with the note files held within the notes directory
  private void PopulateNotes(){
    // Initialization
    File directory = new File(notesDirectory);
    File[] directoryListing = directory.listFiles();
    
    // For each file create a note object
    for (File file : directoryListing) {
      if (file.isFile()){
        LoadNote(file);
      }
    }
    
    PopulateNotesTable(notes);
  }

  // Populates the notes table with the notes in the notes ArrayList
  private void PopulateNotesTable(ArrayList<Note> notes){

    // Initilization
    String[] columnName = {"Title"};
    Object[][] data = new Object[notes.size()][1];

    // For each note in the ArrayList, add the note's title to the data array
    for (int i = 0; i < notes.size(); i++){
      data[i][0] = notes.get(i).getTitle();
    }
    // Create the table model
    noteListTbl.setModel(new javax.swing.table.DefaultTableModel(data, columnName));
    noteListTbl.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
  }
  
  // Clear Note Editor input fields
  private void ClearNoteEditor(){
    noteTitleTxt.setText("");
    noteBodyTxt.setText("");
  }

  // Display an error dialog box
  private void DisplayErrorDialog(String msg, String title){
    JOptionPane.showMessageDialog(this, msg, title , JOptionPane.ERROR_MESSAGE);
  }

// Display an information dialog box
private void DisplayInfoDialog(String msg, String title){
  JOptionPane.showMessageDialog(this, msg, title , JOptionPane.INFORMATION_MESSAGE);
}

  // Display a confirmation request dialog box
  private int DisplayConfirmDialog(String msg, String title){
    int result = JOptionPane.showConfirmDialog(this, msg, title , JOptionPane.YES_NO_OPTION);
    return result;

  }
}