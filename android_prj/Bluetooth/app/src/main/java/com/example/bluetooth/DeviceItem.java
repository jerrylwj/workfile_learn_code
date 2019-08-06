package com.example.bluetooth;

public class DeviceItem {

    private String name;
    private String address;
    private int bondState;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getBondState() {
        return bondState;
    }

    public void setBondState(int bondState) {
        this.bondState = bondState;
    }

    @Override
    public String toString() {
        return "deviceItem = {name:" + name
                + ", address:" + address
                + ", bondState=" + bondState +"}";
    }
}
