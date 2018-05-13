package com.example.hunain.emergencydriverapp;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hunain on 12/3/2017.
 */

public class Book implements Serializable{
    int CID;
    public String phoneNumber;
    public String customerName;
    String problem;
    List<Integer> departmentId;
    double longitude;
    double latitude;
    boolean requestStatus;
    String customerDeviceToken;

}
