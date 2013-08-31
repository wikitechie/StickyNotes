/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stickynotes;

import java.awt.Dialog;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.plaf.OptionPaneUI;

/**
 *
 * @author mhdsyrwan
 */
public class StickyNotes {

    // a list to store the notes
    // Linked list is a Generic/Template Class so we should provide the Template Child class we want which is "Note"
    private LinkedList<Note> notes = new LinkedList<Note>();    
    
    /// Important Note:
    /// we chose the LinkedList because we dont care about getting elements at someindex quickly 
    /// if we care about that we should choose Vector or Array.
    
    /// LinkedList is Fast for adding elements ... and fast for getting all the elements
    /// But it's slow for getting a certain element (ex. list.get(3) // getting the third element).
    
    
    /**
     * Deletes a note from the notes list
     * @param note the note object to delete
     */
    public void delete(Note note) {
        notes.remove(note); // this method will delete by value (by comparing the given object to the lists' objects)
        note.dispose();
    }
    
    /**
     * Creates a Note by giving its body text
     * @param body the body text
     */
    protected Note create(String body) {
        // here we should create a new Note giving it our instance (this) as a parameter so it knows its parent :D
        Note my_note = new Note(this, body);
        notes.add(my_note);
        return my_note;
    }
    
    static private String readFile(String path, Charset encoding) 
    throws IOException
    {
      byte[] encoded = Files.readAllBytes(Paths.get(path));
      return encoding.decode(ByteBuffer.wrap(encoded)).toString();
    }
    
    private void loadNotes() {
        File notesFolder = new File(System.getProperty("user.home") + "/.sticky_notes/notes");
        notesFolder.mkdirs();
        // we are sure that $HOME/.sticky_notes/notes
        File[] notesFiles = notesFolder.listFiles();
        
        for(File note : notesFiles) {
            if (note.isFile()) {
                try {
                    create(readFile(note.getPath(), Charset.forName("UTF-8")));
                } catch (IOException ex) {
                    //TODO show error dialog
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "StickyNotes: Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        
    }
    
    private void saveNotes() {
        File notesFolder = new File(System.getProperty("user.home") + "/.sticky_notes/notes");
        notesFolder.mkdirs();
        File[] notesFiles = notesFolder.listFiles();
        
        // deleting old notes
        for(File note : notesFiles) {
            if (note.isFile()) {
                note.delete();
            }
        }
        
        int i = 0;
        for (Note note : this.notes) {
            try {
                PrintWriter writer = new PrintWriter(notesFolder.getPath() + '/' + i++ + ".txt");
                writer.write(note.getText());
                writer.close();
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "StickyNotes: Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * this method will run the whole program 
     * will load the notes then it will show them
     */
    public void run() {
        // loading notes
        loadNotes();
        
        if (notes.isEmpty())
            create("");
        
        // running/showing notes
        for(Note note : notes) { // this is foreach loop, it means : for each "note" as instance of "Note" in the collection "notes"
            // showing the note
            // the Note class is derived from JFrame Class so it can be shown by "setVisible(true)"
            note.setVisible(true);
        }
        
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                saveNotes();
            }
        }));
    }
    
    public void exit() {
        System.exit(0);
    }
    
    public void show_All(){
        for(Note note : notes) { // this is foreach loop, it means : for each "note" as instance of "Note" in the collection "notes"
            // showing the note
            // the Note class is derived from JFrame Class so it can be shown by "setVisible(true)"
            if(note.isVisible()==false)
                note.setVisible(true);
        }
    }
    
    public boolean all_hidden(){
        boolean state = true;
        for(Note note : notes) { // this is foreach loop, it means : for each "note" as instance of "Note" in the collection "notes"
            // showing the note
            // the Note class is derived from JFrame Class so it can be shown by "setVisible(true)"
            if(note.isVisible())
                state=false;
        }
        return state;
    }
    
    /**
     * this is the main method that JAVA will call
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // creating a new StickyNotes Object (Our Manager Object) then asking it to run :)
        new StickyNotes().run();
    }

    void newNote(String body) {
        this.create(body).setVisible(true);
    }
}
