package ru.geekbrains.qa.java.project;

import java.io.Serializable;

public class WeatherResponse implements Serializable {
    private String city;
    private String date;
    private String weatherText;
    private Double temperature;

    public String getCity() { return city; }

    public void setCity(String city) { this.city = city; }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public String getWeatherText() { return weatherText; }

    public void setWeatherText(String weatherText) { this.weatherText = weatherText; }

    public double getTemperature() { return temperature; }

    public void setTemperature(double temperature) { this.temperature = temperature; }

    WeatherResponse(){

    }

    @Override
    public String toString(){
        return String.format("В городе "+ city +" на дату " + date +
                " днем ожидается " + weatherText + ", температура %.2f C", temperature);
    }
}
