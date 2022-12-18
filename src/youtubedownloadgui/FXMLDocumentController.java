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
import java.util.Arrays;
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
        String[] cmd = {"yt-dlp.exe","--get-id","--ignore-errors"/*,"--proxy","http://149.56.106.104:3128"*/,url};
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
                    //System.out.println("youtube-dl.exe"+(getThumbnail ?  " --write-thumbnail" : "")+(getDesc ?  " --write-description" : "")+(getInfoJSON ?  " --write-info-json" : "")+" --skip-download -w"+(useArchiveFile ? " --download-archive \"" + archiveFile + "\"" : "")+" -o \""+(preserveAsianTitles ? "%"+(useDoublePercent ? "%" : "")+"(title)s" : getWindowsSafeTitle("https://youtube.com/watch?v="+s))+ (addIDToFilename ? "("+(useDoublePercent ? "%" : "")+"%(id)s)" : "") + ".%"+(useDoublePercent ? "%" : "")+"(ext)s"+"\" "+"https://youtube.com/watch?v="+s);
                    System.out.println("yt-dlp"+(getThumbnail ?  " --write-thumbnail" : "")+(getDesc ?  " --write-description" : "")+(getInfoJSON ?  " --write-info-json" : "")+(getComments ?  " --get-comments" : "")+" --skip-download -w"+(useArchiveFile ? " --download-archive \"" + archiveFile + "\"" : "")+" -o \""+(preserveAsianTitles ? "%"+(useDoublePercent ? "%" : "")+"(title)s" : getWindowsSafeTitle("https://youtube.com/watch?v="+s, useDoublePercent))+ (addIDToFilename ? "("+(useDoublePercent ? "%" : "")+"%(id)s)" : "") + ".%"+(useDoublePercent ? "%" : "")+"(ext)s"+"\" "+"https://youtube.com/watch?v="+s);
                } else if (getVideos && !getMetadata) {
                    //Just get the videos
                    //System.out.println("youtube-dl.exe -f " + getBestVideoAndAudioFormat("https://youtube.com/watch?v="+s, maxHRes, reject60fps, rejectAV1) + " -w"+(useArchiveFile ? " --download-archive \"" + archiveFile + "\"" : "")+" -o \""+(preserveAsianTitles ? "%"+(useDoublePercent ? "%" : "")+"(title)s" : getWindowsSafeTitle("https://youtube.com/watch?v="+s))+ (addIDToFilename ? "("+(useDoublePercent ? "%" : "")+"%(id)s)" : "") + ".%"+(useDoublePercent ? "%" : "")+"(ext)s"+"\" "+"https://youtube.com/watch?v="+s);
                    System.out.println("yt-dlp -f " + getBestVideoAndAudioFormat("https://youtube.com/watch?v="+s, maxHRes, reject60fps, rejectAV1) + " -w"+(useArchiveFile ? " --download-archive \"" + archiveFile + "\"" : "")+" -o \""+(preserveAsianTitles ? "%"+(useDoublePercent ? "%" : "")+"(title)s" : getWindowsSafeTitle("https://youtube.com/watch?v="+s, useDoublePercent))+ (addIDToFilename ? "("+(useDoublePercent ? "%" : "")+"%(id)s)" : "") + ".%"+(useDoublePercent ? "%" : "")+"(ext)s"+"\" "+"https://youtube.com/watch?v="+s);
                } else if (getVideos && getMetadata) {
                    //Get the videos and metadata
                    //System.out.println("youtube-dl.exe"+(getThumbnail ?  " --write-thumbnail" : "")+(getDesc ?  " --write-description" : "")+(getInfoJSON ?  " --write-info-json" : "")+" -f " + getBestVideoAndAudioFormat("https://youtube.com/watch?v="+s, maxHRes, reject60fps, rejectAV1) + " -w"+(useArchiveFile ? " --download-archive \"" + archiveFile + "\"" : "")+" -o \""+(preserveAsianTitles ? "%"+(useDoublePercent ? "%" : "")+"(title)s" : getWindowsSafeTitle("https://youtube.com/watch?v="+s))+ (addIDToFilename ? "("+(useDoublePercent ? "%" : "")+"%(id)s)" : "") + ".%"+(useDoublePercent ? "%" : "")+"(ext)s"+"\" "+"https://youtube.com/watch?v="+s);
                    System.out.println("yt-dlp"+(getThumbnail ?  " --write-thumbnail" : "")+(getDesc ?  " --write-description" : "")+(getInfoJSON ?  " --write-info-json" : "")+(getComments ?  " --get-comments" : "")+" -f " + getBestVideoAndAudioFormat("https://youtube.com/watch?v="+s, maxHRes, reject60fps, rejectAV1) + " -w"+(useArchiveFile ? " --download-archive \"" + archiveFile + "\"" : "")+" -o \""+(preserveAsianTitles ? "%"+(useDoublePercent ? "%" : "")+"(title)s" : getWindowsSafeTitle("https://youtube.com/watch?v="+s, useDoublePercent))+ (addIDToFilename ? "("+(useDoublePercent ? "%" : "")+"%(id)s)" : "") + ".%"+(useDoublePercent ? "%" : "")+"(ext)s"+"\" "+"https://youtube.com/watch?v="+s);
                }
                
                /*if (getComments) {
                    if (waitToPrintCommentCommands) {
                        commentCommands.add("call youtube-comment-scraper -f json -s -o \""+getWindowsSafeTitle("https://youtube.com/watch?v="+s)+(addIDToFilename ? "(" + s + ")" : "")+".json\" -- " + s);
                    } else {
                        System.out.println("call youtube-comment-scraper -f json -s -o \""+getWindowsSafeTitle("https://youtube.com/watch?v="+s)+(addIDToFilename ? "(" + s + ")" : "")+".json\" -- " + s);
                    }
                }*/
                count++;
            }
        }
        //Wait for youtube-dl to end
        while (p.isAlive()) {}
        
        /*if (getComments && waitToPrintCommentCommands) {
            System.out.println();
            for (String c : commentCommands) {
                System.out.println(c);
            }
        }*/
        
        recommendedFormatLabel.setText("Done");
    }
    
    private static String getWindowsSafeTitle(String url, boolean useDoublePercent) throws Exception {
        Runtime rt = Runtime.getRuntime();
        String[] cmd = {"yt-dlp.exe","-e"/*,"--proxy","\"http://149.56.106.104:3128\""*/,url};
        Process p = rt.exec(cmd);
        
        BufferedReader stdin = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String s = "";
        while ((s = stdin.readLine()) != null){
            if (s.length() > 0) break;
        }
        //Wait for youtube-dl to end
        while (p.isAlive()) {}
        
        //If s is empty, just use the title template string
        if (s == null) return "%"+(useDoublePercent ? "%" : "")+"(title)s";
        
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
        ArrayList<String> cmdArrayList = new ArrayList<String>();
        cmdArrayList.add("yt-dlp.exe");
        cmdArrayList.add("--format-sort-force");
        cmdArrayList.add("--format-sort");
        cmdArrayList.add("\"" + (maxHRes > 0 ? "res:" + maxHRes + "," : "") + "hasvid,res,+filesize,\"");
        cmdArrayList.add("-F");
        cmdArrayList.add(url);
        String[] cmd = cmdArrayList.toArray(new String[6]);
        Process p = rt.exec(cmd);
        
        BufferedReader stdin = new BufferedReader(new InputStreamReader(p.getInputStream()));
        ArrayList<String> formatList = new ArrayList<>();
        String s;
        boolean shouldAddLine = false;
        while ((s = stdin.readLine()) != null){
            if (shouldAddLine && !s.trim().isEmpty()) {
                formatList.add(s);
            }
            if (!s.isEmpty() && s.charAt(0) == '-') {
                shouldAddLine = true;
            }
        }
        //Wait for youtube-dl to end
        while (p.isAlive()) {}
        String bestFmt = getBestFormat(formatList, maxHRes, reject60fps, rejectAV1);
        String bestFmtNum = bestFmt.split(" ", 2)[0];
        if (bestFmt.length() < 18) System.out.println("Bad format line: "+bestFmt);
        String bestAudioFmt = (bestFmt.contains("avc1") ? "ba[ext=m4a]" : "ba[ext=webm]");
        return bestFmtNum+"+"+bestAudioFmt;
    }
    
    private static String getBestFormat(ArrayList<String> list, boolean reject60fps, boolean rejectAV1) throws Exception {
        return getBestFormat(list, 0, reject60fps, rejectAV1);
    }
    
    private static String getBestFormat(ArrayList<String> list) throws Exception {
        return getBestFormat(list, 0, false, false);
    }
    
    private static String getBestFormat(ArrayList<String> list, int maxHRes, boolean reject60fps, boolean rejectAV1) throws Exception {
        if (list.isEmpty()) return "";
        String bestFmt = list.get(0);
        String currentFmt;
        
        //Select the last video format
        int startIdx = 0;
        for (int i = list.size() - 1; i > 4; i--) {
            currentFmt = list.get(i);
            int framerate = Integer.parseInt(currentFmt.substring(22, 24).trim());
            if (reject60fps && framerate == 60) continue;
            if (rejectAV1 && currentFmt.contains("av01")) continue;
            return currentFmt;
        }
        
        return "";
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