package spring.bricole.util;

import lombok.Data;

@Data
public class Address {
    private double latitude;
    private double longitude;

    public Address(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Address() {
        this.latitude = 0.0;
        this.longitude = 0.0;
    }
}
