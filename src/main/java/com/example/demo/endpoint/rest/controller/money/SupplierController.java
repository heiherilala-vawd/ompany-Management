package com.example.demo.endpoint.rest.controller.money;

import com.example.demo.client.model.CrupdateSupplier;
import com.example.demo.client.model.Supplier;
import com.example.demo.endpoint.rest.mapper.money.SupplierMapper;
import com.example.demo.service.money.SupplierService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class SupplierController {

  private final SupplierService supplierService;
  private final SupplierMapper supplierMapper;

  @GetMapping("/companies/{comp_id}/suppliers")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<Supplier> getSuppliers(@PathVariable("comp_id") String companyId) {
    return supplierService.findByCompanyId(companyId).stream().map(supplierMapper::toRest).toList();
  }

  @GetMapping("/companies/{comp_id}/suppliers/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public Supplier getSupplierById(
      @PathVariable("comp_id") String companyId, @PathVariable String id) {
    return supplierMapper.toRest(supplierService.findById(id));
  }

  @PutMapping("/companies/{comp_id}/suppliers")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<Supplier> crupdateSuppliers(
      @PathVariable("comp_id") String companyId,
      @Valid @RequestBody List<CrupdateSupplier> toWrite) {
    var domains = toWrite.stream().map(s -> supplierMapper.toDomain(s, companyId)).toList();
    return supplierService.createOrUpdateAll(domains).stream().map(supplierMapper::toRest).toList();
  }

  @DeleteMapping("/companies/{comp_id}/suppliers/{id}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deleteSupplierById(@PathVariable String id) {
    supplierService.deleteById(id);
  }
}
