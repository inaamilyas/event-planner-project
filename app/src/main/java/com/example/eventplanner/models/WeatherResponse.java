package com.example.eventplanner.models;

import com.google.gson.annotations.SerializedName;

public class WeatherResponse {

    @SerializedName("location")
    private Location location;

    @SerializedName("current")
    private Current current;

    @SerializedName("forecast")
    private Forecast forecast;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Current getCurrent() {
        return current;
    }

    public void setCurrent(Current current) {
        this.current = current;
    }

    public Forecast getForecast() {
        return forecast;
    }

    public void setForecast(Forecast forecast) {
        this.forecast = forecast;
    }

    public static class Location {
        @SerializedName("name")
        private String name;

        @SerializedName("region")
        private String region;

        @SerializedName("country")
        private String country;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }
    }

    public static class Current {
        @SerializedName("temp_c")
        private float tempC;

        @SerializedName("condition")
        private Condition condition;

        @SerializedName("humidity")
        private int humidity;

        @SerializedName("wind_kph")
        private float windKph;

        public float getTempC() {
            return tempC;
        }

        public void setTempC(float tempC) {
            this.tempC = tempC;
        }

        public Condition getCondition() {
            return condition;
        }

        public void setCondition(Condition condition) {
            this.condition = condition;
        }

        public int getHumidity() {
            return humidity;
        }

        public void setHumidity(int humidity) {
            this.humidity = humidity;
        }

        public float getWindKph() {
            return windKph;
        }

        public void setWindKph(float windKph) {
            this.windKph = windKph;
        }

        public static class Condition {
            @SerializedName("text")
            private String text;

            @SerializedName("icon")
            private String icon;

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }
        }
    }

    public static class Forecast {
        @SerializedName("forecastday")
        private ForecastDay[] forecastDays;

        public ForecastDay[] getForecastDays() {
            return forecastDays;
        }

        public void setForecastDays(ForecastDay[] forecastDays) {
            this.forecastDays = forecastDays;
        }

        public static class ForecastDay {
            @SerializedName("date")
            private String date;

            @SerializedName("day")
            private Day day;

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public Day getDay() {
                return day;
            }

            public void setDay(Day day) {
                this.day = day;
            }

            public static class Day {
                @SerializedName("maxtemp_c")
                private float maxTempC;

                @SerializedName("mintemp_c")
                private float minTempC;

                @SerializedName("condition")
                private Condition condition;

                public float getMaxTempC() {
                    return maxTempC;
                }

                public void setMaxTempC(float maxTempC) {
                    this.maxTempC = maxTempC;
                }

                public float getMinTempC() {
                    return minTempC;
                }

                public void setMinTempC(float minTempC) {
                    this.minTempC = minTempC;
                }

                public Condition getCondition() {
                    return condition;
                }

                public void setCondition(Condition condition) {
                    this.condition = condition;
                }

                public static class Condition {
                    @SerializedName("text")
                    private String text;

                    @SerializedName("icon")
                    private String icon;

                    public String getText() {
                        return text;
                    }

                    public void setText(String text) {
                        this.text = text;
                    }

                    public String getIcon() {
                        return icon;
                    }

                    public void setIcon(String icon) {
                        this.icon = icon;
                    }
                }
            }
        }
    }
}
