package com.example.nuclerone.To_Excel;

public class Neutron_Time {
    private double time;
    private double neutron;
    public Neutron_Time(double t,double n){
        this.time = t;
        this.neutron = n;
    }
    public double getTime(){return time;}
    public double getNeutron(){return neutron;}
    public void setTime(double t){this.time = t;}
    public void setNeutron(double n){this.neutron = n;}
}
