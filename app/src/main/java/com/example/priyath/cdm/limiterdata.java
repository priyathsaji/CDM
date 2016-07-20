package com.example.priyath.cdm;

import java.io.Serializable;

/*
 * Created by PRIYATH SAJI on 06-07-2016.
 */
public class limiterdata implements Serializable {

    long days;
    long  limit;
    String sdate;
    String eDate;
    String unit;
    long dataDownloaded;
    long dataUploaded;
    boolean isLimiterSet;

}
