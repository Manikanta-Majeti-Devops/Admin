package com.onlinereservationapplication.admin;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "busroute")
public class Busroute {
    @Id
    @Column(name = "id", nullable = false, length = 500)
    private String id;

    @Column(name = "source", length = 100)
    private String source;

    @Column(name = "destination", length = 100)
    private String destination;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "bus_number", length = 100)
    private String busNumber;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

}