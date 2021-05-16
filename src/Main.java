import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


public class Main {
    public static void main(String[] args) {

        JSONParser parser = new JSONParser(); //Creating json parser

        try {
            JSONArray a = (JSONArray) parser.parse(new FileReader("c:/data.json")); // parsing the first file to json array
            JSONObject b = (JSONObject) parser.parse(new FileReader("c:/repDef.json"));//parsing the second file to json obj

            //Getting the information from the object from the report definition file
            Long topPerformersThreshold = (Long) b.get("topPerformersThreshold");
            boolean useExprienceMultiplier = (boolean) b.get("useExprienceMultiplier");
            Long periodLimit = (Long) b.get("periodLimit");
            Map<String, Double> results = new LinkedHashMap<>(); //creating a map that will hold the top performers

            for (Object o : a) { //iterating trough the json array
                JSONObject person = (JSONObject) o; //creating object person
                double score = 0;  //setting the score to 0

                //setting the obj parameters, getting them from the data.json file
                String name = (String) person.get("name");
                Long totalSales = (Long) person.get("totalSales");
                Long salesPeriod = (Long) person.get("salesPeriod");
                Double experienceMultiplier = (Double) person.get("experienceMultiplier");


                if (useExprienceMultiplier) {  //check if experience multipliplier should be used in the calculation
                    score = totalSales / salesPeriod * experienceMultiplier; //calculating the score
                } else {
                    score = totalSales / salesPeriod; //calculating the score
                }

                if (salesPeriod <= periodLimit) { //check if the sales period exceeds the period limit
                    results.put(name, score); // if not, put the person's name and score are put into the map;
                }


            }

            // countPeopleInTheFile - shows how many top performers should be in the file
            double countPeopleInTheFile = Math.floor(topPerformersThreshold * 0.01 * results.size());


            FileWriter csvWriter = new FileWriter("Result.csv"); //opening file writer for cvs format, creating "Result.csv"
            csvWriter.append("Name");       //Appending "Name,Score"
            csvWriter.append(",");
            csvWriter.append("Score");
            csvWriter.append("\n");




            if (countPeopleInTheFile >= 1) { //checks if the result should contain 1 person or more
                int count = (int) countPeopleInTheFile; // cast the people count to int
                //creating map called "sortedMap", sorting the results map
                Map<String, Double> sortedMap = results.entrySet().stream()
                        .sorted(Comparator.comparingDouble(e -> -e.getValue()))
                        .limit(count)  //!!!limiting the size of the new map!!!
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (x, y) -> {
                                    throw new AssertionError();
                                },
                                LinkedHashMap::new
                        ));


                //writing the new map into the cvs file "Result.csv"
                for (Map.Entry<String, Double> entry : sortedMap.entrySet()) {
                    csvWriter.append(String.format(entry.getKey() + "," + "%.0f", entry.getValue()));
                    csvWriter.append("\n");
                }

            }
            //closing the writer
            csvWriter.flush();
            csvWriter.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
