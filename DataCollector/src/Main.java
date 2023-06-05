
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

import static java.nio.file.Files.readAllLines;

public class Main {
    public static final String path = "C:/Users/Vitaliy/Desktop/Metro/data";
    static File folder = new File(path);
    public static String url = "https://skillbox-java.github.io//";
    public static List<Station> stationList = new ArrayList<>();
    public static String stationsPath = "DataCollector/data/stations.json";
    public static String mapPath = "DataCollector/data/map.json";

    public static void main(String[] args) throws IOException {
        ParsingParadise.parseHtmlFileFromNet(url);
        checkFile(folder);
        writeJsonFile(stationsPath);
        ParsingParadise.parseMapJsonFile(mapPath);
    }

    private static void writeJsonFile(String stationsPath) {
        JSONObject resultObject = new JSONObject();
        JSONArray array = new JSONArray();
        for (Station station : stationList) {
            JSONObject object = new JSONObject();
            object.put("name", station.getStationName());
            object.put("line", station.getStationLineName());
            if (station.getStationDate() != null) {
                object.put("date", station.getStationDate());
            }
            if (station.getStationDepth() != 0.0) {
                object.put("depth", station.getStationDepth());
            }
            object.put("hasConnection", station.isHasConnection());
            array.add(object);
            resultObject.put("stations", array);
        }
        try {
            Files.write(Paths.get(stationsPath), resultObject.toString().getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void checkFile(File folder) {
        if (folder.isFile() && folder.getName().toLowerCase().endsWith(".json")) {
            ParsingParadise.parseJsonFileFromFolder(String.valueOf(folder));
        } else if (folder.isFile() && folder.getName().toLowerCase().endsWith(".csv")) {
            ParsingParadise.parseCsvFileFromFolder(folder.getPath());
        }
        try {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    checkFile(file);
                }
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public static String getJsonFile(String path) {
        StringBuilder builder = new StringBuilder();
        try {

            List<String> lines = readAllLines(Path.of(path));
            lines.forEach(line -> builder.append(line));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return builder.toString();
    }
}
