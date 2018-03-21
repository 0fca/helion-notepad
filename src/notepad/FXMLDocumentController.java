/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepad;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.intellij.util.PathUtilRt;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import notepad.threading.FileTaskFactory;
import notepad.threading.Operation;
import notepad.threading.TaskManager;

/**
 *
 * @author obsidiam
 */
public class FXMLDocumentController implements Initializable {
    private static ObservableList<Path> RECENTS = FXCollections.observableArrayList();
    @FXML
    private Button newButton, saveButton, openButton, closeButton;
    @FXML
    private ComboBox<String> encodingCbx;
    @FXML
    private ListView<Path> recentFilesList;
    @FXML
    private TextArea mainTextArea;
    @FXML
    private Label fileNameLabel;
    
    private final FileChooser FC = new FileChooser();

    private static Properties p = new Properties();
    private static TaskManager t = TaskManager.getTaskManager();
    private FileTaskFactory ftf = new FileTaskFactory();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Alert al = new Alert(Alert.AlertType.ERROR);
        try{
            init();
        }catch(IOException e){
            e.printStackTrace();
        }

        newButton.setOnAction(a ->{
            TextInputDialog tid = new TextInputDialog("New File.txt");
            tid.setHeaderText("Podaj nazwę pliku:");
            Optional<String> o = tid.showAndWait();
            o.ifPresent(con ->{
                    ftf.setOperation(Operation.CREATE);
                    ftf.setParams(new Object[]{con});

                    Task task = t.submitTask(ftf);
                    task.setOnSucceeded(handler ->{
                        if(!(Boolean)task.getValue()){
                            al.setTitle("Błąd");
                            al.setHeaderText("Błąd tworzenia pliku");
                            al.setContentText("Plik już istnieje.");
                            al.show();
                        }
                    });
                fileNameLabel.setText(con);
            });
        });

        saveButton.setOnAction(a ->{
            ftf.setOperation(Operation.SAVE);
            ftf.setParams(new Object[]{mainTextArea.getParagraphs().toArray(new CharSequence[mainTextArea.getParagraphs().size()]), encodingCbx.getSelectionModel().getSelectedItem() != null ? encodingCbx.getSelectionModel().getSelectedItem() : "UTF-8"});
            t.submitTask(ftf);
        });

        openButton.setOnAction(a ->{
            File f = FC.showOpenDialog(null);
            if(f != null) {
                ftf.setOperation(Operation.OPEN);
                ftf.setParams(new Object[]{f});
                mainTextArea.setText(null);
                    Task task = t.submitTask(ftf);
                    task.setOnSucceeded(handler ->{
                        ((ArrayList)task.getValue()).forEach(line ->{
                            mainTextArea.appendText(String.valueOf(line));
                        });
                    });
                fileNameLabel.setText(f.getAbsolutePath());
            }
        });

        closeButton.setOnAction(a ->{
            ftf.setOperation(Operation.CLOSE);
            t.submitTask(ftf);
            fileNameLabel.setText(null);
        });

        recentFilesList.setOnMouseClicked(a ->{
            if(recentFilesList.getItems().size() > 0){
                File f = Paths.get(recentFilesList.getSelectionModel().getSelectedItem().toString()).toFile();
                ftf.setOperation(Operation.OPEN);
                ftf.setParams(new Object[]{f});
                mainTextArea.setText(null);
                System.out.println(Platform.isFxApplicationThread());
                    Task task = t.submitTask(ftf);
                    task.setOnSucceeded(handler ->{
                        ((ArrayList)task.getValue()).forEach(line ->{
                            mainTextArea.appendText(String.valueOf(line));
                        });
                    });
                    fileNameLabel.setText(f.getAbsolutePath());
            }
        });
    }    

    private void initEncodingBoxValues() throws IllegalArgumentException, IllegalAccessException{
        Field[] fs = EncodingBoxModel.class.getDeclaredFields();
        for(Field f : fs){
            String s = f.get(null).toString();
            encodingCbx.getItems().add(s);
        }
    }

    private void init() throws IOException {
        FC.setTitle("Wybierz plik tesktowy...");
        FC.setSelectedExtensionFilter(new ExtensionFilter("Pliki tekstowe", ".txt",".ini",".html",".rtf",".xml"));
        
        FC.setInitialDirectory(new File(System.getProperty("user.home")));
        
        try {
            initEncodingBoxValues();
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        RECENTS = FilesystemController.loadRecents();
        setRecentsToView();
    }


    
    private void setRecentsToView(){
        recentFilesList.setItems(RECENTS);
    }
}
