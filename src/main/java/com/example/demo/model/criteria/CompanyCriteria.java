package com.example.demo.model.criteria;

import com.example.demo.model.Company;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyCriteria {
  private String name;
  private String rib;
  private String description;
  private Company.CompanyType companyType;
}
