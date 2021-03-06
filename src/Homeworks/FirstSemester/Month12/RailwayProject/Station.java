package Homeworks.FirstSemester.Month12.RailwayProject;

import java.util.ArrayList;

public class Station implements Informative {
    private int id;
    private String name;
    ArrayList<Way> ways;

    public Station(int id, String name, ArrayList<Way> ways) {
        this.id = id;
        this.name = name;
        this.ways = ways;
    }

    static Station getStationByName(ArrayList<Station> stations, String stationName) {
        for (Station station : stations) {
            if (station.getName().equals(stationName)) {
                return station;
            }
        }
        return null;
    }

    static Station getStationById(ArrayList<Station> stations, int stationId) {
        for (Station station : stations) {
            if (station.getId() == stationId) {
                return station;
            }
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getInfo() {
        return "Station{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", ways=" + ways +
                '}';
    }

    @Override
    public String toString() {
        return "Station: " + name + " (ID: " + id + ")";
    }
}
