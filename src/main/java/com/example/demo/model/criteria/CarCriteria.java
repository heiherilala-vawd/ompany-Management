package com.example.demo.model.criteria;

import com.example.demo.model.movement.Car.StatutVoiture;
import com.example.demo.model.movement.Car.TypeCarburant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarCriteria {

  private String equipmentId;
  private String warehouseId;
  private String immatriculation;
  private TypeCarburant typeCarburant;
  private String marque;
  private String modele;
  private Integer annee;
  private String couleur;
  private Integer kilometrageMin;
  private Integer kilometrageMax;
  private StatutVoiture statut;
}
