package Bot.Weather;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by Jan on 10.01.2017.
 */
public class CurrentWeather {
    private Double lat;
    private Double lon;
    private String urlStr;

    public CurrentWeather(Double lat, Double lon){
        this.lat = lat;
        this.lon = lon;
        createURL();
    }

    private void createURL(){
        urlStr="http://api.openweathermap.org/data/2.5/weather?" + "lat=" + Double.toString(lat) +
                "&lon=" + Double.toString(lon) + "&units=metric&appid=17619c7c44e4c6d14f4a20fccec56bee";
    }

    public String getCurrentWeather(){
        Gson gson = new Gson();
        String weatherString = "";
        try {
            URL url = new URL(urlStr);

            Weather weather = gson.fromJson(new JsonReader(new InputStreamReader(url.openStream())), Weather.class);
            weatherString = "Aktualna pogoda w Krakowie: " +
                    "Temperatur: " + weather.getMain().getTemp() + " stopni Celsjusza " +
                    "Ciśnienie: " + weather.getMain().getPressure() + "hPa " +
                    "Wilgotność: " + weather.getMain().getHumidity() + "% " +
                    "Prędkość wiatru: " + weather.getWind().getSpeed() + "km/h " +
                    "Wszelakie limity smogowe przekroczone 10000 krotnie.";
        } catch (Exception e){
            e.printStackTrace();
        }

        return weatherString;
    }
}
