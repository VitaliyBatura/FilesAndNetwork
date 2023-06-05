import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;

import static java.nio.file.Files.readAllLines;

class ParsingParadise {

    static void parseHtmlFileFromNet(String url) throws IOException {
        Document document = Jsoup.connect(url).get();
        Elements metroStations = document.select("div.js-metro-stations");
        HashMap<String, String> stationsMap = new HashMap<>();
        metroStations.forEach(ms -> {
            stationsMap.put(ms.attr("data-line"), (ms.text()));
        });
        JSONObject object = new JSONObject();
        JSONObject resultObject = new JSONObject();
        stationsMap.forEach((numberLine, listStations) -> {
            JSONArray array = new JSONArray();
            String[] stationNames = listStations.replaceAll("[0-9]{1,2}[.]", "").trim().split("\\s{2}");
            for (int i = 0; i < stationNames.length; i++) {
                array.add(stationNames[i].trim());
                Station station = new Station(stationNames[i], numberLine);
                Main.stationList.add(station);
            }
            object.put(numberLine, array);
            resultObject.put("stations", object);
        });
        Elements metroLines = document.select("span.js-metro-line");
        HashMap<String, String> linesMap = new HashMap<>();
        metroLines.forEach(ml -> {
            linesMap.put(ml.attr("data-line"), ml.text());
        });
        JSONArray arrayLn = new JSONArray();
        linesMap.forEach((numberLine, nameLine) -> {
            JSONObject objectLn = new JSONObject();
            objectLn.put("number", numberLine);
            objectLn.put("name", nameLine);
            arrayLn.add(objectLn);
            for (Station station : Main.stationList) {
                if (numberLine.equals(station.getStationLineNumber())) {
                    station.setStationLineName(nameLine);
                }
            }
        });
        resultObject.put("lines", arrayLn);
        Files.write(Paths.get("DataCollector/data/map.json"), resultObject.toString().getBytes(), StandardOpenOption.APPEND);
        Elements connections = document.select("span.t-icon-metroln");
        connections.forEach(c -> {
            String[] text = (c.attr("title").replaceAll("«", "\"").replaceAll("»", "\"")).split("\"");
            if (text.length == 3) {
                String connectionStation = text[1];
                for (Station station : Main.stationList) {
                    if (connectionStation.equals(station.getStationName()) || station.isHasConnection()) {
                        station.setHasConnection(true);
                    } else {
                        station.setHasConnection(false);
                    }
                }
            }
        });
    }

    static void parseCsvFileFromFolder(String path) {
        try {
            JSONParser parser = new JSONParser();
            List<String> lines = readAllLines(Paths.get(path));
            JSONArray array = new JSONArray();
            JSONObject result = new JSONObject();
            for (String line : lines) {
                JSONObject object = new JSONObject();
                String[] fragments = line.split(",");
                if (fragments.length == 3) {
                    fragments[1] = fragments[1].replaceAll("−", "\\-");
                    fragments[1] = (fragments[1].replaceAll("\"", "") + "." +
                            fragments[2].replaceAll("\"", ""));
                    object.put("name", fragments[0]);
                    object.put("depth", Double.parseDouble(fragments[1]));
                    for (Station station : Main.stationList) {
                        if (fragments[0].equals(station.getStationName())) {
                            station.setStationDepth(Double.parseDouble(fragments[1]));
                        }
                    }
                    continue;
                }
                fragments[1] = fragments[1].replaceAll("−", "-");
                if (!fragments[1].trim().matches("^.?[0-9]+")
                        && !fragments[1].trim().matches("[0-9]{2}.[0-9]{2}.[0-9]{4}")) {
                    continue;
                }
                if (fragments[1].trim().matches("^.?[0-9]+")) {
                    object.put("name", fragments[0]);
                    object.put("depth", Double.parseDouble(fragments[1]));
                    for (Station station : Main.stationList) {
                        if (fragments[0].equals(station.getStationName())) {
                            station.setStationDepth(Double.parseDouble(fragments[1]));
                        }
                    }
                }
                if (fragments[1].trim().matches("[0-9]{2}.[0-9]{2}.[0-9]{4}")) {
                    object.put("name", fragments[0]);
                    object.put("date", fragments[1]);
                    for (Station station : Main.stationList) {
                        if (fragments[0].equals(station.getStationName())) {
                            station.setStationDate(fragments[1]);
                        }
                    }
                }
                array.add(object);
            }
            result.put("stations", array);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    static void parseJsonFileFromFolder(String path) {
        try {
            JSONParser parser = new JSONParser();
            String text = Main.getJsonFile(path).replaceAll("station_name", "name")
                    .replaceAll("depth_meters", "depth").replaceAll("−", "-");
            JSONArray jsonData = (JSONArray) parser.parse(text);
            JSONArray array = new JSONArray();
            JSONObject result = new JSONObject();
            for (Object data : jsonData) {
                JSONObject stationsData = (JSONObject) data;
                String stName = (String) stationsData.get("name");
                String stDate = (String) stationsData.get("date");
                String stDepth = String.valueOf(stationsData.get("depth")).replaceAll(",", ".");
                JSONObject object = new JSONObject();
                if (stDepth.equals("?")) {
                    continue;
                }
                if (stDepth.equals("null")) {
                    object.put("name", stName);
                    object.put("date", stDate);
                    for (Station station : Main.stationList) {
                        if (stName.equals(station.getStationName())) {
                            station.setStationDate(stDate);
                        }
                    }
                }
                if (stDate == null) {
                    object.put("name", stName);
                    object.put("depth", stDepth);
                    for (Station station : Main.stationList) {
                        if (stName.equals(station.getStationName())) {
                            station.setStationDepth(Double.parseDouble(stDepth));
                        }
                    }
                }
                array.add(object);
            }
            result.put("stations", array);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    static void parseMapJsonFile(String path) {
        try {
            JSONParser parser = new JSONParser();
            String text = Main.getJsonFile(path);
            JSONObject jsonData = (JSONObject) parser.parse(text);
            String stations = String.valueOf(jsonData.get("stations"));
            String[] lineStations = stations.split("],");
            for (String lineStation : lineStations) {
                String[] line = lineStation.replaceAll("\\{", "").replaceAll("\"", "").
                        replaceAll("\\[", "").replaceAll("\\s", "").
                        replaceAll(":", " ").replaceAll(",", " ").split("\\s");
                System.out.println("Линия " + line[0] + "  -->  " + " Количество станций: " + (line.length - 1));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
