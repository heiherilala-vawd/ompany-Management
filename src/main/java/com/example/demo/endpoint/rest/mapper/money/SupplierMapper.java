package com.example.demo.endpoint.rest.mapper.money;

import com.example.demo.model.Company;
import com.example.demo.model.money.Supplier;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper {

  public Supplier toDomain(com.example.demo.client.model.CrupdateSupplier rest, String companyId) {
    if (rest == null) return null;
    return Supplier.builder()
        .id(rest.getId())
        .name(rest.getName())
        .siret(rest.getSiret())
        .address(rest.getAddress())
        .email(rest.getEmail())
        .phone(rest.getPhone())
        .contactName(rest.getContactName())
        .company(companyId != null ? Company.builder().id(companyId).build() : null)
        .build();
  }

  public com.example.demo.client.model.Supplier toRest(Supplier domain) {
    if (domain == null) return null;
    var rest = new com.example.demo.client.model.Supplier();
    rest.setId(domain.getId());
    rest.setName(domain.getName());
    rest.setSiret(domain.getSiret());
    rest.setAddress(domain.getAddress());
    rest.setEmail(domain.getEmail());
    rest.setPhone(domain.getPhone());
    rest.setContactName(domain.getContactName());
    if (domain.getCompany() != null) {
      rest.setCompanyId(domain.getCompany().getId());
    }
    return rest;
  }

  public com.example.demo.client.model.CrupdateSupplier toCrupdate(Supplier domain) {
    if (domain == null) return null;
    var rest = new com.example.demo.client.model.CrupdateSupplier();
    rest.setId(domain.getId());
    rest.setName(domain.getName());
    rest.setSiret(domain.getSiret());
    rest.setAddress(domain.getAddress());
    rest.setEmail(domain.getEmail());
    rest.setPhone(domain.getPhone());
    rest.setContactName(domain.getContactName());
    return rest;
  }
}
