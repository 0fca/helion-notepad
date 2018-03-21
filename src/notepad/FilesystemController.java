package notepad;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class FilesystemController {

    private static final ObservableList<Path> RECENTS = FXCollections.observableArrayList();
    private static final String RECENTS_FILE = "recents.xml";
    private static File f;
    private static Properties p = new Properties();

    public static List<String> openFile(File file) {
        f = file;
        try {
            return Files.readAllLines(f.toPath());
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static void closeFile(){
        boolean isThere = false;

        for(Path p : RECENTS){
            if(p.toFile().getAbsolutePath().equals(f.getAbsolutePath())){
                isThere = true;
                break;
            }
        }

        if(!isThere){
            RECENTS.add(f.toPath());
        }

        f = null;
        try {
            saveRecentsToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveFile(Iterable<CharSequence> i, String charset){
        try{
            System.out.println(f);
            if(f != null){
                Charset c = getCharset(charset);
                Files.write(f.toPath(), i, c, StandardOpenOption.TRUNCATE_EXISTING);
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private static void saveRecentsToFile() throws IOException {
        RECENTS.forEach(recentPath ->{
            p.setProperty(recentPath.getFileName().toString(), recentPath.toFile().getAbsolutePath());
        });

        OutputStream os = new FileOutputStream(RECENTS_FILE);
        p.storeToXML(os, null);
    }

    static ObservableList<Path> loadRecents() throws IOException {
        if(new File(RECENTS_FILE).exists()){
            System.out.println(RECENTS_FILE);
            InputStream in = new FileInputStream(RECENTS_FILE);

            p.loadFromXML(in);
            p.forEach((x,y) -> RECENTS.add(Paths.get(y.toString())));
        }
        return RECENTS;
    }

    public static boolean newFile(String name) throws IOException {
        f = new File(System.getProperty("user.home")+File.separator+name.split("\\.")[0]+".txt");
        return f.createNewFile();
    }

    private static Charset getCharset(String charset) {
        return Charset.forName(charset);
    }
}
