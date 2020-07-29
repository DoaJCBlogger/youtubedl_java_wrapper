/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package youtubedownloadgui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Vector;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

/**
 *
 * @author 777
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private TextField urlField;
    @FXML
    private CheckBox getThumbCB;
    @FXML
    private CheckBox getDescCB;
    @FXML
    private CheckBox getInfoJSONCB;
    @FXML
    private CheckBox reject60fpsCB;
    @FXML
    private CheckBox rejectAV1CB;
    @FXML
    private TextField maxResField;
    @FXML
    private Label titleLabel;
    @FXML
    private Label recommendedFormatLabel;
    @FXML
    private CheckBox forceMP4CB;
    @FXML
    private CheckBox preserveAsianCharsCB;
    @FXML
    private CheckBox getVideosCB;
    @FXML
    private CheckBox getMetadataCB;
    @FXML
    private CheckBox getCommentsCB;
    @FXML
    private Label maxResLabel;
    @FXML
    private CheckBox useDoublePercentCB;
    @FXML
    private CheckBox addIDToFilenameCB;
    @FXML
    private Spinner limitSpinner;
    @FXML
    private CheckBox useArchiveFileCB;
    @FXML
    private TextField archiveFileField;
    
    @FXML
    private void handleButtonAction(ActionEvent event) throws Exception {
        int limit = (int)limitSpinner.getValue();

        String url = urlField.getText();
        if (url.length() == 0) {
            recommendedFormatLabel.setText("No URL");
            return;
        }
        
        boolean getThumbnail = getThumbCB.isSelected();
        boolean getDesc = getDescCB.isSelected();
        boolean getInfoJSON = getInfoJSONCB.isSelected();
        int maxHRes = (maxResField.getText().length() > 0 ? Integer.parseInt(maxResField.getText()) : 0);
        boolean reject60fps = reject60fpsCB.isSelected();
        boolean rejectAV1 = rejectAV1CB.isSelected();
        boolean forceMP4 = forceMP4CB.isSelected();
        boolean preserveAsianTitles = preserveAsianCharsCB.isSelected();
        boolean getVideos = getVideosCB.isSelected();
        boolean getMetadata = getMetadataCB.isSelected();
        boolean getComments = getCommentsCB.isSelected();
        boolean useDoublePercent = useDoublePercentCB.isSelected();
        boolean waitToPrintCommentCommands = (getVideos || getMetadata);
        boolean addIDToFilename = addIDToFilenameCB.isSelected();
        boolean useArchiveFile = useArchiveFileCB.isSelected();
        String archiveFile = "archive.txt";
        if (useArchiveFile && !archiveFileField.getText().isEmpty()) {
            archiveFile = archiveFileField.getText();
        }
        
        if (!getVideos && !getMetadata && !getComments) {
            recommendedFormatLabel.setText("Nothing to download (videos, metadata, and comments are unchecked)");
            return;
        }
        
        recommendedFormatLabel.setText("Getting videos...");
        Runtime rt = Runtime.getRuntime();
        String[] cmd = {"youtube-dl.exe","--get-id","--ignore-errors"/*,"--proxy","http://149.56.106.104:3128"*/,url};
        Process p = rt.exec(cmd);
        
        BufferedReader stdin = new BufferedReader(new InputStreamReader(p.getInputStream()));
        
        ArrayList<String> commentCommands = new ArrayList<>();
        
        String s;
        int count = 0;
        while ((s = stdin.readLine()) != null) {
            if ((limit > 0) && (count >= limit)) {
                p.destroyForcibly();
                break;
            }
            if (s != null && s.length() == 11) {
                if (!getVideos && !getMetadata) {
                    //Do nothing
                } else if (!getVideos && getMetadata) {
                    //Just get the metadata
                    System.out.println("youtube-dl.exe"+(getThumbnail ?  " --write-thumbnail" : "")+(getDesc ?  " --write-description" : "")+(getInfoJSON ?  " --write-info-json" : "")+" --skip-download -w"+(useArchiveFile ? " --archive-file \"" + archiveFile + "\"" : "")+" -o \""+(preserveAsianTitles ? "%"+(useDoublePercent ? "%" : "")+"(title)s" : getWindowsSafeTitle("https://youtube.com/watch?v="+s))+ (addIDToFilename ? "("+(useDoublePercent ? "%" : "")+"%(id)s)" : "") + ".%"+(useDoublePercent ? "%" : "")+"(ext)s"+"\" "+"https://youtube.com/watch?v="+s);
                } else if (getVideos && !getMetadata) {
                    //Just get the videos
                    System.out.println("youtube-dl.exe -f " + getBestVideoAndAudioFormat("https://youtube.com/watch?v="+s, maxHRes, reject60fps, rejectAV1) + " -w"+(useArchiveFile ? " --archive-file \"" + archiveFile + "\"" : "")+" -o \""+(preserveAsianTitles ? "%"+(useDoublePercent ? "%" : "")+"(title)s" : getWindowsSafeTitle("https://youtube.com/watch?v="+s))+ (addIDToFilename ? "("+(useDoublePercent ? "%" : "")+"%(id)s)" : "") + ".%"+(useDoublePercent ? "%" : "")+"(ext)s"+"\" "+"https://youtube.com/watch?v="+s);
                } else if (getVideos && getMetadata) {
                    //Get the videos and metadata
                    System.out.println("youtube-dl.exe"+(getThumbnail ?  " --write-thumbnail" : "")+(getDesc ?  " --write-description" : "")+(getInfoJSON ?  " --write-info-json" : "")+" -f " + getBestVideoAndAudioFormat("https://youtube.com/watch?v="+s, maxHRes, reject60fps, rejectAV1) + " -w"+(useArchiveFile ? " --archive-file \"" + archiveFile + "\"" : "")+" -o \""+(preserveAsianTitles ? "%"+(useDoublePercent ? "%" : "")+"(title)s" : getWindowsSafeTitle("https://youtube.com/watch?v="+s))+ (addIDToFilename ? "("+(useDoublePercent ? "%" : "")+"%(id)s)" : "") + ".%"+(useDoublePercent ? "%" : "")+"(ext)s"+"\" "+"https://youtube.com/watch?v="+s);
                }
                
                if (getComments) {
                    if (waitToPrintCommentCommands) {
                        commentCommands.add("call youtube-comment-scraper -f json -s -o \""+getWindowsSafeTitle("https://youtube.com/watch?v="+s)+(addIDToFilename ? "(" + s + ")" : "")+".json\" -- " + s);
                    } else {
                        System.out.println("call youtube-comment-scraper -f json -s -o \""+getWindowsSafeTitle("https://youtube.com/watch?v="+s)+(addIDToFilename ? "(" + s + ")" : "")+".json\" -- " + s);
                    }
                }
                count++;
            }
        }
        //Wait for youtube-dl to end
        while (p.isAlive()) {}
        
        if (getComments && waitToPrintCommentCommands) {
            System.out.println();
            for (String c : commentCommands) {
                System.out.println(c);
            }
        }
        
        recommendedFormatLabel.setText("Done");
    }
    
    private static String getWindowsSafeTitle(String url) throws Exception {
        Runtime rt = Runtime.getRuntime();
        String[] cmd = {"youtube-dl.exe","-e"/*,"--proxy","\"http://149.56.106.104:3128\""*/,url};
        Process p = rt.exec(cmd);
        
        BufferedReader stdin = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String s = "";
        while ((s = stdin.readLine()) != null){
            if (s.length() > 0) break;
        }
        //Wait for youtube-dl to end
        while (p.isAlive()) {}
        
        //If s is empty, return "no_title"
        if (s == null) return "no_title";
        
        //Regex found at https://stackoverflow.com/questions/31563951/removing-invalid-characters-from-a-string-to-use-it-as-a-file
        String outStr = "";
        for (int i = 0; i < s.length(); i++) {
            if ((s.charAt(i) == '\\')||(s.charAt(i) == '/')||(s.charAt(i) == ':')||(s.charAt(i) == '*')||(s.charAt(i) == '?')||(s.charAt(i) == '\"')||(s.charAt(i) == '<')||(s.charAt(i) == '>')||(s.charAt(i) == '|')) {
                outStr += " ";
            } else {
                outStr += s.charAt(i);
            }
        }
        return outStr.trim();
    }
    
    private static String getBestVideoAndAudioFormat(String url) throws Exception {
        return getBestVideoAndAudioFormat(url, 0, false, false);
    }
    
    private static String getBestVideoAndAudioFormat(String url, int maxHRes) throws Exception {
        return getBestVideoAndAudioFormat(url, maxHRes, false, false);
    }
        
    private static String getBestVideoAndAudioFormat(String url, int maxHRes, boolean reject60fps, boolean rejectAV1) throws Exception {
        Runtime rt = Runtime.getRuntime();
        String[] cmd = {"youtube-dl.exe","-F",url};
        Process p = rt.exec(cmd);
        
        BufferedReader stdin = new BufferedReader(new InputStreamReader(p.getInputStream()));
        Vector<String> formatList = new Vector<>();
        String s;
        boolean add2List = false;
        while ((s = stdin.readLine()) != null){
            if (add2List) {
                formatList.add(s);
            }
            if (s.substring(0, 6).equals("format")) add2List = true;
        }
        //Wait for youtube-dl to end
        while (p.isAlive()) {}
        String bestFmt = getBestFormat(formatList, maxHRes, reject60fps, rejectAV1);
        String bestFmtNum = bestFmt.split(" ", 2)[0];
        if (bestFmt.length() < 18) System.out.println("Bad format line: "+bestFmt);
        String bestAudioFmt = (bestFmt.length() > 17 ? (bestFmt.substring(13, 17).trim().equals("webm") ? "251" : "140") : "error");
        return bestFmtNum+"+"+bestAudioFmt;
    }
    
    private static String getBestFormat(Vector<String> list, boolean reject60fps, boolean rejectAV1) throws Exception {
        return getBestFormat(list, 0, reject60fps, rejectAV1);
    }
    
    private static String getBestFormat(Vector<String> list) throws Exception {
        return getBestFormat(list, 0, false, false);
    }
    
    private static String getBestFormat(Vector<String> list, int maxHRes, boolean reject60fps, boolean rejectAV1) throws Exception {
        if (list.isEmpty()) return "";
        String bestFmt = list.get(0);
        String currentFmt;
        
        //Select the first video format
        int startIdx = 0;
        for (int i = 0; i < list.size(); i++){  
            currentFmt = list.get(i);
            if ((!currentFmt.substring(24, 29).equals("audio"))) {
                startIdx = i + i;
                break;
            }
        }
        
        for (int i = startIdx; i < list.size(); i++){
            currentFmt = list.get(i);
            if (currentFmt.length() == 0) break;
            
            //Skip audio formats and formats below 100
            if ((!currentFmt.substring(24, 29).equals("audio")) && (Integer.parseInt(currentFmt.split(" ",2)[0].replaceAll("[\\D]", "")) > 99)) {
                boolean currentFmtIsBetter = (compareVideoFmt(currentFmt, bestFmt, maxHRes, reject60fps, rejectAV1) == 1);
                if (currentFmtIsBetter) bestFmt = currentFmt;
            }
        }
        return bestFmt;
    }
    
    private static int compareVideoFmt(String f1, String f2, int maxHRes, boolean reject60fps, boolean rejectAV1) throws Exception {
        String shortCode1 = f1.substring(35).split(" ",2)[0];
        String shortCode2 = f2.substring(35).split(" ",2)[0];
        
        //Check if either one has "tiny" as a resolutin and report it as inferior if it does
        boolean f1IsTiny = false;
        boolean f2IsTiny = false;
        try {
            Integer.parseInt(shortCode1.replaceAll("[\\D]", ""));
        } catch (Exception e) {
            f1IsTiny = true;
        }
        try {
            Integer.parseInt(shortCode2.replaceAll("[\\D]", ""));
        } catch (Exception e) {
            f2IsTiny = true;
        }
        if (f1IsTiny && !f2IsTiny) {
            return -1; //f2 is better
        } else if (!f1IsTiny && f2IsTiny) {
            return 1; //f1 is better
        } else if (f1IsTiny && f2IsTiny) {
            return 0; //they are equal
        }
        
        //Check if either one has greater than the max allowed resolution
        //Skip this is maxHRes is 0
        if (maxHRes > 0) {
            if ((Integer.parseInt(shortCode1.split("p", 2)[0].replaceAll("[\\D]", "")) <= maxHRes) && (Integer.parseInt(shortCode2.split("p", 2)[0].replaceAll("[\\D]", "")) > maxHRes)) {
                return 1; //f1 is better only because it is within the max allowed resolution
            } else if ((Integer.parseInt(shortCode1.split("p", 2)[0].replaceAll("[\\D]", "")) > maxHRes) && (Integer.parseInt(shortCode2.split("p", 2)[0].replaceAll("[\\D]", "")) <= maxHRes)) {
                return -1; //f2 is better only because it is within the max allowed resolution
            } else if ((Integer.parseInt(shortCode1.split("p", 2)[0].replaceAll("[\\D]", "")) > maxHRes) && (Integer.parseInt(shortCode2.split("p", 2)[0].replaceAll("[\\D]", "")) > maxHRes)) {
                return 0; //neither format is better because neither is within the max allowed resolution
            }
        }
        
        //If rejectAV1 is true, check if one is AV1
        if (rejectAV1) {
            if (!f1.contains("av01") && f2.contains("av01")) {
                return 1; //f1 is better because it isn't AV1
            } else if (f1.contains("av01") && !f2.contains("av01")) {
                return -1; //f2 is better because it isn't AV1
            } else if (f1.contains("av01") && f2.contains("av01")) {
                return 0; //neither is better because they are both AV1
            }
        }
        
        //If reject60fps is true, check if one is 60fps
        boolean f1Is60fps = shortCode1.split("p", 2)[1].length() == 2;
        boolean f2Is60fps = shortCode2.split("p", 2)[1].length() == 2;
        if (reject60fps) {
            if (!f1Is60fps && f2Is60fps) {
                return 1; //f1 is better
            } else if (f1Is60fps && !f2Is60fps) {
                return -1; //f2 is better
            } else if (f1Is60fps && f2Is60fps) {
                return 0;
            }
        }
        
        //Resolution is the next most important parameter. 720p is better than 480p60
        if (Integer.parseInt(shortCode1.split("p", 2)[0].replaceAll("[\\D]", "")) > Integer.parseInt(shortCode2.split("p", 2)[0].replaceAll("[\\D]", ""))) {
            return 1; //f1 is better
        } else if (Integer.parseInt(shortCode1.split("p", 2)[0].replaceAll("[\\D]", "")) < Integer.parseInt(shortCode2.split("p", 2)[0].replaceAll("[\\D]", ""))) {
            return -1; //f2 is better
        }
        
        //If the resolutions are the same, check for high frame rate
        if (f1Is60fps && !f2Is60fps) {
            return 1; //f1 is better
        } else if (!f1Is60fps && f2Is60fps) {
            return -1; //f2 is better
        }
        
        //Finally, check the file size
        String[] f1split = f1.split(" ");
        String[] f2split = f2.split(" ");
        float f1Size = Float.parseFloat(f1split[f1split.length - 1].replaceAll("[^0-9.]", ""));
        String f1Unit = f1split[f1split.length - 1].replaceAll("[^a-zA-Z]", "");
        float f2Size = Float.parseFloat(f2split[f2split.length - 1].replaceAll("[^0-9.]", ""));
        String f2Unit = f2split[f2split.length - 1].replaceAll("[^a-zA-Z]", "");
        long f1ActualSize = (long)(f1Size);
        if (f1Unit.equals("KiB")) {
            f1ActualSize *= 1024;
        } else if (f1Unit.equals("MiB")) {
            f1ActualSize *= 1048576;
        } else if (f1Unit.equals("GiB")) {
            f1ActualSize *= 1073741824;
        }
        long f2ActualSize = (long)(f2Size);
        if (f2Unit.equals("KiB")) {
            f2ActualSize *= 1024;
        } else if (f2Unit.equals("MiB")) {
            f2ActualSize *= 1048576;
        } else if (f2Unit.equals("GiB")) {
            f2ActualSize *= 1073741824;
        }
        
        if (f1ActualSize < f2ActualSize) {
            return 1; //f1 is better
        } else if (f2ActualSize < f1ActualSize) {
            return -1; //f2 is better
        }
        
        return 0; //f1 and f2 are similar enough to be considered equal
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        limitSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE));
        
        //Copied from https://stackoverflow.com/a/13729491
        //Answer by "Uluk Biy"
        getCommentsCB.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                
            }
        });
        
        //Copied from https://stackoverflow.com/a/26527350
        //Answer by "James_D"
        getVideosCB.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected, Boolean isNowSelected) {
                if (isNowSelected) {
                    //Enable everything related to downloading videos
                    reject60fpsCB.setDisable(false);
                    rejectAV1CB.setDisable(false);
                    maxResField.setDisable(false);
                    forceMP4CB.setDisable(false);
                    maxResLabel.setDisable(false);
                } else {
                    //Disable everything
                    reject60fpsCB.setDisable(true);
                    rejectAV1CB.setDisable(true);
                    maxResField.setDisable(true);
                    forceMP4CB.setDisable(true);
                    maxResLabel.setDisable(true);
                }
            }
        });
        getMetadataCB.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected, Boolean isNowSelected) {
                if (isNowSelected) {
                    //Enable everything related to downloading videos
                    getThumbCB.setDisable(false);
                    getDescCB.setDisable(false);
                    getInfoJSONCB.setDisable(false);
                } else {
                    //Disable everything
                    getThumbCB.setDisable(true);
                    getDescCB.setDisable(true);
                    getInfoJSONCB.setDisable(true);
                }
            }
        });
        useArchiveFileCB.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected, Boolean isNowSelected) {
                if (isNowSelected) {
                    //Enable the archive filename field
                    archiveFileField.setDisable(false);
                } else {
                    //Disable the archive filename field
                    archiveFileField.setDisable(true);
                }
            }
        });
    }
}