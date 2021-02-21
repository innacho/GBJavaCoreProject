package ru.geekbrains.qa.java.project;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.io.Serializable;

public class WeatherResponse implements Serializable {
    private String date;
    private String weatherText;
    private float temperature;

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public String getWeatherText() { return weatherText; }

    public void setWeatherText(String weatherText) { this.weatherText = weatherText; }

    public float getTemperature() { return temperature; }

    public void setTemperature(float temperature) { this.temperature = temperature; }

    WeatherResponse(){

    }

    @Override
    public String toString(){
        return " на дату " + date +  " днем ожидается " + weatherText + ", температура " + temperature +"C";
    }
}
