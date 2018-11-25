package com.example.a67024.coolweatherapp.gson;

import com.google.gson.annotations.SerializedName;

public class Suggestion {
    @SerializedName("comf")
    public Comfortable comfort;

    @SerializedName("cw")
    public CarWash carWash;

    public Sport sport;

    public class Comfortable {
        @SerializedName("txt")
        public String info;
    }

    public class CarWash {
        @SerializedName("txt")
        public String info;
    }

    public class Sport {
        @SerializedName("txt")
        public String info;
    }
}
