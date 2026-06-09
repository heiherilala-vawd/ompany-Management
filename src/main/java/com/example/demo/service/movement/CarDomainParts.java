package com.example.demo.service.movement;

import com.example.demo.model.movement.Car;
import com.example.demo.model.movement.Equipment;
import com.example.demo.model.movement.Warehouse;

public record CarDomainParts(
    Car car,
    Warehouse warehouse,
    Equipment equipment) {}
