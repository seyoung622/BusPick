package com.example.buspick;

public class Dist {
    public static String getDistName(String code){
        String areaName = "";
        switch (code){
            case "1":
                areaName = "서울시";
            case "2":
                areaName = "경기도";
            case"3":
                areaName = "인천시";
            default:
                areaName = "";
        }
        return areaName;
    }
}
