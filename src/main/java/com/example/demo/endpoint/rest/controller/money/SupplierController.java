package com.example.demo.endpoint.rest.controller.money;

import com.example.demo.client.model.CrupdateSupplier;
import com.example.demo.client.model.Supplier;
import com.example.demo.endpoint.rest.PaginatedResponse;
import com.example.demo.endpoint.rest.mapper.money.SupplierMapper;
import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.service.money.SupplierService;
import com.example.demo.service.utils.PageUtils;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class SupplierController {

  private final SupplierService supplierService;
  private final SupplierMapper supplierMapper;

  @GetMapping("/users/{userId}/companies/{companyId}/suppliers")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public PaginatedResponse getSuppliers(
      @PathVariable String userId,
      @PathVariable String companyId,
      @RequestParam(name = "page", required = false) PageFromOne page,
      @RequestParam(name = "page_size", required = false) BoundedPageSize pageSize) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    var result = supplierService.findByCompanyId(companyId, pageable);
    var list = result.stream().map(supplierMapper::toRest).toList();
    return new PaginatedResponse(list, (int) result.getTotalElements());
  }

  @GetMapping("/users/{userId}/companies/{companyId}/suppliers/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public Supplier getSupplierById(
      @PathVariable String userId, @PathVariable String companyId, @PathVariable String id) {
    return supplierMapper.toRest(supplierService.findById(id));
  }

  @PutMapping("/users/{userId}/companies/{companyId}/suppliers")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<Supplier> crupdateSuppliers(
      @PathVariable String userId,
      @PathVariable String companyId,
      @Valid @RequestBody List<CrupdateSupplier> toWrite) {
    var domains = toWrite.stream().map(s -> supplierMapper.toDomain(s, companyId)).toList();
    return supplierService.createOrUpdateAll(domains).stream().map(supplierMapper::toRest).toList();
  }

  @DeleteMapping("/users/{userId}/companies/{companyId}/suppliers/{id}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void deleteSupplierById(@PathVariable String id) {
    supplierService.deleteById(id);
  }
}
