package Homeworks.Month12.RailwayProject;

import static Methods.Methods.*;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

class DatabaseHandler {
    // Contains trains for easy input, not needed in final build
    private static final String TRAINS_TXT_PATH = "src/Homeworks/Month12/RailwayProject/data/trains.txt";
    // Contains all station names in proper order
    private static final String STATIONS_TXT_PATH = "src/Homeworks/Month12/RailwayProject/data/stations.txt";
    // Contains all possible ways and their lengths for each station name
    // Being associated by station ID's: ID = line in waymatrix with proper ways description
    private static final String WAYMATRIX_TXT_PATH = "src/Homeworks/Month12/RailwayProject/data/waymatrix.txt";
    private static final String TABLE_NAME = "timetable";
    private static final String DB_PATH_PC = "D:\\Projects\\Java\\" +
            "PolyakovV_11005\\src\\Homeworks\\Month12\\" +
            "RailwayProject\\data\\" + TABLE_NAME + ".db";
    private static final String DB_PATH = "C:\\Projects\\Java\\" +
            "StudyProject\\src\\Homeworks\\Month12\\" +
            "RailwayProject\\data\\" + TABLE_NAME + ".db";

    private static final String[] COLUMNS = {
            "id",
            "train_name",
            "train_speed",
            "train_capacity",
            "train_ticket_cost",
            "train_type",
            "train_route"
    };
    private Connection connection;
    private static final ArrayList<String> waymatrixRows = getWayMatrix();
    private static final ArrayList<String> stationNames = getStationNames();
    private static final WaysHandler waysHandler = getAllWays();
    private static final ArrayList<Station> stations = parseStations(); // requires waysHandler
    // Contains all the trains from database
    private final ArrayList<Train> trains;

    DatabaseHandler() {
        openDatabase();
        waysHandler.setStations(stations); // requires stations list
        trains = parseTrains(); // requires connection and waysHandler's list of stations
    }

    private void openDatabase() {
        try {
            Class.forName("org.sqlite.JDBC"); // class loading
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH_PC);
        } catch (SQLException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    void addToDatabase(Train train) {
        String query = "INSERT INTO " + TABLE_NAME + " " +
                "( id, train_name, train_speed, train_capacity, train_ticket_cost, train_type, train_route )\n" +
                "VALUES ( " +
                "0, '" +
                train.getName() + "', " +
                train.getSpeed() + ", " +
                train.getCapacity() + ", " +
                train.getTicketCost() + ", " +
                train.getTrainType().getId() + ", '" +
                train.getRouteCode() + "' );";

        System.out.println(query);
        try {
            Statement statement = connection.createStatement();
            //statement.executeUpdate(query);
            statement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    void updateDatabase(Train train) {
        String query = "UPDATE " + TABLE_NAME + " SET\n" +
                COLUMNS[1] + " = '" + train.getName() + "',\n" +
                COLUMNS[2] + " = " + train.getSpeed() + ",\n" +
                COLUMNS[3] + " = " + train.getCapacity() + ",\n" +
                COLUMNS[4] + " = " + train.getTicketCost() + ",\n" +
                COLUMNS[5] + " = " + train.getTrainType().getId() + ",\n" +
                COLUMNS[6] + " = '" + train.getRouteCode() + "'\n" +
                "WHERE\n" +
                COLUMNS[0] + " = " + train.getId() + ";";

        System.out.println(query);
        try {
            Statement statement = connection.createStatement();
            // statement.executeUpdate(query);
            statement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    ArrayList<Train> getTrains() {
        return trains;
    }

    private ArrayList<Train> parseTrains() {
        ArrayList<Train> trains = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME + ";";

        try {
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery(query);
            while (results.next()) {
                trains.add(new Train(
                        results.getInt(COLUMNS[0]),
                        results.getString(COLUMNS[1]),
                        results.getInt(COLUMNS[2]),
                        results.getInt(COLUMNS[3]),
                        results.getInt(COLUMNS[4]),
                        TrainType.values()[results.getInt(COLUMNS[5])],
                        results.getString(COLUMNS[6]),
                        new Route(stations, waysHandler.getRouteForTrain(results.getString(COLUMNS[6]))))
                );

            }
            results.close();
            statement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return trains;
    }

    void displayDatabase() {
        System.out.println("Total amount of elements: " + trains.size());
        line("-");
        for (Train train : trains) {
            System.out.println(train);
        }
        line("-");
    }

    void addAllTrainsFromTxtToDatabase() {
        ArrayList<Train> trains = getTrainsFromTxt();
        for (Train train : trains) {
            addToDatabase(train);
        }
    }

    // trains.txt parser used just for faster initial set of trains input
    private static ArrayList<Train> getTrainsFromTxt() {
        int idCounter = 0;
        ArrayList<Train> trains = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new FileReader(TRAINS_TXT_PATH));
            while (scanner.hasNext()) {
                trains.add(new Train(
                        idCounter++,
                        scanner.next(),
                        scanner.nextInt(),
                        scanner.nextInt(),
                        scanner.nextInt(),
                        TrainType.values()[scanner.nextInt()],
                        scanner.next())
                );
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return trains;
    }

    // Get all stations and set their ways
    private static ArrayList<Station> parseStations() {
        ArrayList<Station> stations = new ArrayList<>();
        for (int i = 0; i < stationNames.size(); i++) {
            stations.add(new Station(
                    i,
                    stationNames.get(i),
                    waysHandler.getWaysForStation(stationNames.get(i))
            ));
        }
        return stations;
    }

    // stations.txt parser returns ArrayList of station names
    // To add a new station, stations.txt and waymatrix.txt updates needed
    static ArrayList<String> getStationNames() {
        ArrayList<String> stationNames = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new FileReader(STATIONS_TXT_PATH));
            while (scanner.hasNext()) {
                stationNames.add(scanner.next());
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stationNames;
    }

    // waymatrix.txt parser returns ArrayList of Strings each of which contains one row
    static ArrayList<String> getWayMatrix() {
        ArrayList<String> wayRows = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new FileReader(WAYMATRIX_TXT_PATH));
            while (scanner.hasNext()) {
                wayRows.add(scanner.nextLine());
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wayRows;
    }

    // waymatrixRows parser for all ways referring to each station
    private static WaysHandler getAllWays() {
        ArrayList<Way> ways = new ArrayList<>();
        for (int i = 0; i < waymatrixRows.size(); i++) {
            String wayRow = waymatrixRows.get(i);
            if (wayRow.length() == 0) continue;
            String[] splittedRow = wayRow.split(" ");
            for (int j = 0; j < splittedRow.length; j += 2) {
                ways.add(new Way(
                        stationNames.get(i),
                        stationNames.get(Integer.parseInt(splittedRow[j])),
                        Integer.parseInt(splittedRow[j + 1])
                ));
            }
        }
        return new WaysHandler(ways);
    }

    WaysHandler getWaysHandler() {
        return waysHandler;
    }

    static ArrayList<Station> getStations() {
        return stations;
    }


    // Legacy method once used to divide old route codes which was without spaces
    private void divide() {
        for (int i = 0; i < trains.size(); i++) {
            Train train = trains.get(i);
            String initialRoute = train.getRouteCode();
            StringBuilder newRoute = new StringBuilder();
            if(initialRoute.charAt(1) == ' ') continue;
            for (int j = 0; j < initialRoute.length() - 1; j++) {
                newRoute.append(initialRoute.charAt(j) - 48);
                newRoute.append(" ");
            }
            newRoute.append(initialRoute.charAt(initialRoute.length() - 1) - 48);
            train.setRouteCode(newRoute.toString());

            //updateDatabase(train);
        }
    }
}
