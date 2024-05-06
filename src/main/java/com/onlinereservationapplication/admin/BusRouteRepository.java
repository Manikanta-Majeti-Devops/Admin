package com.onlinereservationapplication.admin;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BusRouteRepository extends JpaRepository<Busroute, String>
{
    // You can add custom query methods here if needed
    Busroute findByBusNumber(String busNumber);
}
