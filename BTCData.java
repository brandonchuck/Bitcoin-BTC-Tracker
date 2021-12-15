package BTC_PriceTracker;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.google.gson.stream.JsonReader;

import java.io.InputStreamReader;
import java.net.URL;

public class BTCData {

    private final IntegerProperty rawTime;
    private final IntegerProperty timeStamp;
    private final FloatProperty open;
    private final FloatProperty high;
    private final FloatProperty low;
    private final FloatProperty close;
    private final IntegerProperty timeFrom;
    private final IntegerProperty timeTo;

    public BTCData(int raw_time, int time, Float open, Float high, Float low, Float close, int timeFrom, int timeTo){
        this.rawTime = new SimpleIntegerProperty(this, "rawTime");
        this.timeStamp = new SimpleIntegerProperty(this, "time");
        this.open = new SimpleFloatProperty(this, "open");
        this.high = new SimpleFloatProperty(this, "high");
        this.low = new SimpleFloatProperty(this, "low");
        this.close = new SimpleFloatProperty(this, "close");
        this.timeFrom = new SimpleIntegerProperty(this, "timeFrom");
        this.timeTo = new SimpleIntegerProperty(this, "timeTo");

        this.setRawTime(raw_time);
        this.setTimeStamp(time);
        this.setOpen(open);
        this.setHigh(high);
        this.setLow(low);
        this.setClose(close);
        this.setTimeFrom(timeFrom);
        this.setTimeTo(timeTo);
    }

    // Getters
    public Integer getRawTime(){
        return rawTime.get();
    }
    public Integer getTimeStamp(){
        return timeStamp.get();
    }
    public Float getOpen(){
        return open.get();
    }
    public Float getHigh(){     // not used
        return high.getValue();
    }
    public Float getLow(){      // not used
        return low.get();
    }
    public Float getClose(){    // not used
        return close.get();
    }
    public Integer getTimeFrom(){
        return timeFrom.get();
    }
    public Integer getTimeTo(){
        return timeTo.get();
    }

    // Setters
    public void setRawTime(int value) {
        this.rawTime.set(value);
    }
    public void setTimeStamp(int value){
        this.timeStamp.set(value);
    }
    public void setOpen(float value) {
        this.open.set(value);
    }
    public void setClose(float value) {
        this.close.set(value);
    }
    public void setLow(float value) {
        this.low.set(value);
    }
    public void setHigh(float value) {
        this.high.set(value);
    }
    public void setTimeFrom(int value){
        this.timeFrom.set(value);
    }
    public void setTimeTo(int value){
        this.timeTo.set(value);
    }



    // Dynamically load API endpoints
    public static ObservableList<BTCData> getData(String t){

        int interval = 0;
        int multiple = 0;

        if (t.equals("day")){
            interval = 30; // for 30 days
            multiple = 86400;
        }
        if (t.equals("hour")){
            interval = 24; // for 24 hours
            multiple = 3600;
        }
        if (t.equals("minute")){
            interval = 60; // for 60 minutes
            multiple = 60;
        }

        String URL = String.format("https://min-api.cryptocompare.com/data/histo%s?aggregate=1&e=CCCAGG&extraParams=CryptoCompare&fsym=BTC&limit=%d&tryConversion=false&tsym=USD", t, interval);
        System.out.println(URL);

        ObservableList<BTCData> values = FXCollections.observableArrayList();

        try {
            URL address = new URL(URL);
            JsonReader reader = new JsonReader(new InputStreamReader(address.openStream()));

            Gson gson = new Gson();
            JsonObject root = gson.fromJson(reader, JsonObject.class); // get root JSON object

            int timeTo = root.get("TimeTo").getAsInt();
            int timeFrom = root.get("TimeFrom").getAsInt();

            JsonArray data = root.getAsJsonArray("Data"); // get "Data" JSON array

            for (JsonElement j: data){

                JsonObject jsonObject = j.getAsJsonObject();

                // grab time stamp
                int raw_time = jsonObject.get("time").getAsInt(); // time from epoch 01/01/1970
                int time;

                // check whether timestamp is start or end
                if (!(raw_time == timeFrom) && !(raw_time == timeTo)){
                    time = (raw_time - timeFrom)/multiple;
                } else {
                    if (raw_time == timeFrom){  // if timestamp is the start, then set to 0
                        time = 0;
                    } else {
                        time = (raw_time - timeFrom)/multiple;
                    }
                }

                Float open = jsonObject.get("open").getAsFloat();
                Float high = jsonObject.get("high").getAsFloat();   // not used
                Float low = jsonObject.get("low").getAsFloat();     // not used
                Float close = jsonObject.get("close").getAsFloat(); // not used

                values.add(new BTCData(raw_time, time, open, high, low, close, timeFrom, timeTo));
            }

            return values;

        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

}
