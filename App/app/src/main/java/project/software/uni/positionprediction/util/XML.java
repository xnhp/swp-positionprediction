package project.software.uni.positionprediction.util;

import android.content.Context;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

import project.software.uni.positionprediction.R;
import project.software.uni.positionprediction.algorithms_new.PredictionAlgorithm;

public class XML {

    private static int hours_past;
    private static int hours_fut;
    private static String movebank_user;
    private static String movebank_password;
    private static Class[] algorithms;
    private static Class[] visualizations;
    private static int used_alg;
    private static int used_vis;

    public XML() {
    }

    public XML(int hours_past, int hours_fut, Class[] algorithms, Class[] visualizations, int used_alg, int used_vis, LinkedList<String> downloaded_studies) {
        this.hours_past = hours_past;
        this.hours_fut = hours_fut;
        this.algorithms = algorithms;
        this.visualizations = visualizations;
        this.used_alg = used_alg;
        this.used_vis = used_vis;
    }


    public int getHours_past() {
        return hours_past;
    }

    public void setHours_past(int hours_past) {
        this.hours_past = hours_past;
    }

    public int getHours_fut() {
        return hours_fut;
    }

    public void setHours_fut(int hours_fut) {
        this.hours_fut = hours_fut;
    }

    public Class[] getAlgorithms() {
        return algorithms;
    }

    public void setAlgorithms(Class[] algorithms) {
        this.algorithms = algorithms;
    }

    public Class[] getVisualizations() {
        return visualizations;
    }

    public void setVisualizations(Class[] visualizations) {
        this.visualizations = visualizations;
    }

    public int getUsed_alg() {
        return used_alg;
    }

    public void setUsed_alg(int used_alg) {
        this.used_alg = used_alg;
    }

    public int getUsed_vis() {
        return used_vis;
    }

    public void setUsed_vis(int used_vis) {
        this.used_vis = used_vis;
    }

    public String getMovebank_user() {
        return movebank_user;
    }

    public void setMovebank_user(String new_movebank_user){
        movebank_user = new_movebank_user;
    }

    public String getMovebank_password(){
        return movebank_password;
    }

    public void setMovebank_password(String new_movebank_password){
        movebank_password = new_movebank_password;
    }


    private void setTag(String tag, String val, XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag(null, tag);
        xmlSerializer.text(val + "");
        xmlSerializer.endTag(null, tag);
    }

    public void writeFile(Context context){
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(context.getResources().getString(R.string.settings_file_name), Context.MODE_PRIVATE);

            XmlSerializer xmlSerializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();

            xmlSerializer.setOutput(writer);
            xmlSerializer.startDocument("UTF-8", true);

            setTag("hours_past", hours_past + "", xmlSerializer);

            setTag("hours_fut", hours_fut + "", xmlSerializer);

            if(movebank_user != null)
            setTag("movebank_user", movebank_user, xmlSerializer);

            if(movebank_password != null)
            setTag("movebank_password", movebank_password, xmlSerializer);

            setTag("used_alg", used_alg + "", xmlSerializer);

            setTag("used_vis", used_vis + "", xmlSerializer);

            xmlSerializer.endDocument();
            xmlSerializer.flush();
            String dataWrite = writer.toString();
            fileOutputStream.write(dataWrite.getBytes());
            fileOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean fileExists( String filename, Context context) {
        File file = context.getFileStreamPath(filename);
        if(file == null || !file.exists()) {
            return false;
        }
        return true;
    }

    public void readFile(Context context){

        String filename = context.getResources().getString(R.string.settings_file_name);

        if(fileExists(filename, context)) {
            try {
                FileInputStream fileInputStream = context.openFileInput(filename);
                XmlPullParserFactory xmlParserFactory = XmlPullParserFactory.newInstance();
                XmlPullParser xmlParser = xmlParserFactory.newPullParser();
                xmlParser.setInput(fileInputStream, null);

                parseXML(xmlParser);

                fileInputStream.close();

            } catch (Exception e) {

                setStandardVals();

                e.printStackTrace();
            }
        }else{
            setStandardVals();
        }

    }

    private void setStandardVals(){
        movebank_user = null;
        movebank_password = null;

        used_vis = 0;
        used_alg = 0;

        hours_past = 5;
        hours_fut = 5;
    }

    private void parseXML(XmlPullParser parser) throws XmlPullParserException,IOException {

        int parserEvent = parser.getEventType();

        while (parserEvent != XmlPullParser.END_DOCUMENT) {
            switch (parserEvent) {
                case XmlPullParser.START_TAG:
                    String name = parser.getName();
                    switch (name) {
                        case "hours_past":
                            hours_past = Integer.parseInt(parser.nextText());
                            break;
                        case "hours_fut":
                            hours_fut = Integer.parseInt(parser.nextText());
                            break;
                        case "used_alg":
                            used_alg = Integer.parseInt(parser.nextText());
                            break;
                        case "used_vis":
                            used_vis = Integer.parseInt(parser.nextText());
                            break;
                        case "movebank_user":
                            movebank_user = parser.nextText();
                            break;
                        case "movebank_password":
                            movebank_password = parser.nextText();
                            break;
                    }
                    break;
            }
            parserEvent = parser.next();
        }
    }


    private PredictionAlgorithm getPredictionAlgorithm(final Context context){

        PredictionAlgorithm predictionAlgorithm = null;

        try {

            Class<?> type = algorithms[used_alg];
            Constructor<?> constructor = type.getConstructor(Context.class);

            Object obj = constructor.newInstance(context);

            if(obj instanceof PredictionAlgorithm) predictionAlgorithm = (PredictionAlgorithm) obj;


        } catch (IllegalAccessException | InstantiationException
                | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return predictionAlgorithm;
    }

}
