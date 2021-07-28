package com.calculusmaster.pokecord.util.helpers;

import com.calculusmaster.pokecord.util.Mongo;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class TimeHelper
{
    public static void start()
    {
        ThreadPoolHandler.TIME.scheduleAtFixedRate(TimeHelper::updateTimes, 5, 15 * 60, TimeUnit.SECONDS);
    }

    private static void updateTimes()
    {
        Mongo.TimeData.find().forEach(d -> {
            SerializedTime interval = new SerializedTime(d);

            SerializedTime now = SerializedTime.now();
            SerializedTime cached = SerializedTime.cached();

            int difference = now.toSeconds() - cached.toSeconds();

            if(difference >= interval.toSeconds())
            {
                //Updates go here
            }
        });
    }

    private enum TimeTask
    {
        DAILY_TRAINERS;
    }

    private static class SerializedTime
    {
        private String target;
        private int days, hours, minutes, seconds;

        SerializedTime(String target, int days, int hours, int minutes, int seconds)
        {
            this.target = target;

            this.days = days;
            this.hours = hours;
            this.minutes = minutes;
            this.seconds = seconds;
        }

        SerializedTime(Document data)
        {
            this(data.getString("target"), data.getInteger("days"), data.getInteger("hours"), data.getInteger("minutes"), data.getInteger("seconds"));
        }

        public static SerializedTime now()
        {
            LocalDateTime current = LocalDateTime.now();
            return new SerializedTime("now", current.getDayOfYear(), current.getHour(), current.getMinute(), current.getSecond());
        }

        public static SerializedTime cached()
        {
            return new SerializedTime(Objects.requireNonNull(Mongo.TimeData.find(Filters.eq("target", "cache")).first()));
        }

        public int toSeconds()
        {
            return this.days * 24 * 60 * 60 + this.hours * 60 * 60 + this.minutes * 60 + this.seconds;
        }

        public Document serialize()
        {
            return new Document()
                    .append("target", this.target)
                    .append("days", this.days)
                    .append("hours", this.hours)
                    .append("minutes", this.minutes)
                    .append("seconds", this.seconds);
        }

        public static SerializedTime fromDB(String target)
        {
            return new SerializedTime(Objects.requireNonNull(Mongo.TimeData.find(Filters.eq("target", target)).first()));
        }
    }
}
