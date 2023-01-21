package com.driver.dtos;

public class DriverDTO {

    private int driverId;
    private String Mobile;
    private String password;

    public DriverDTO(int driverId, String mobile, String password) {
        this.driverId = driverId;
        this.Mobile = mobile;
        this.password = password;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
