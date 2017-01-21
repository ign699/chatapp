package Bot;

import Bot.Weather.CurrentWeather;
import org.joda.time.DateTime;

/**
 * Created by Jan on 10.01.2017.
 */
public class Bot {
    private CurrentWeather currentWeather = new CurrentWeather(50.06, 19.94);

    public String askQuestion(String question){
        String answer = "";
        switch (question){
            case "!weather":
                answer = sayWeather();
                break;
            case "!day":
                answer = sayDay();
                break;
            case "!hour":
                answer = sayHour();
                break;
            default:
                answer = "Only valid questions are !weather, !day, !hour";
        }
        return answer;
    }

    private String sayWeather(){
        return currentWeather.getCurrentWeather();
    }

    private String sayHour(){
        DateTime date = new DateTime();
        return date.toLocalTime().toString().substring(0,5);
    }

    private String sayDay(){
        DateTime date = new DateTime();
        return date.dayOfWeek().getAsText();
    }
}
