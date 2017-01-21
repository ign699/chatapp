package Bot;

import Bot.Weather.CurrentWeather;
import org.joda.time.DateTime;

/**
 * Created by Jan on 10.01.2017.
 */
public class Bot {
    private CurrentWeather currentWeather = new CurrentWeather(50.06, 19.94);



    public String sayWeather(){
        return currentWeather.getCurrentWeather();
    }

    public String sayHour(){
        DateTime date = new DateTime();
        return date.toLocalTime().toString().substring(0,5);
    }

    public String sayDay(){
        DateTime date = new DateTime();
        return date.dayOfWeek().getAsText();
    }
}
