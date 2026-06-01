package com.example.demo.endpoint.rest.controller.money;

import com.example.demo.client.model.CrupdateOtherExpenseType;
import com.example.demo.client.model.OtherExpenseType;
import com.example.demo.endpoint.rest.mapper.money.OtherExpenseTypeMapper;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.service.money.OtherExpenseTypeService;
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
public class OtherExpenseTypeController {

  private final OtherExpenseTypeService otherExpenseTypeService;
  private final OtherExpenseTypeMapper otherExpenseTypeMapper;

  @GetMapping("/companies/{comp_id}/other_expense_types/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public OtherExpenseType getOtherExpenseTypeById(
      @PathVariable String comp_id, @PathVariable String id) {
    return otherExpenseTypeMapper.toRestOtherExpenseType(
        otherExpenseTypeService
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException("OtherExpenseType with id " + id + " not found")));
  }

  @GetMapping("/companies/{comp_id}/other_expense_types")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<OtherExpenseType> getOtherExpenseTypes(@PathVariable String comp_id) {
    return otherExpenseTypeMapper.toRestOtherExpenseTypes(
        otherExpenseTypeService.findAllByCompanyId(comp_id));
  }

  @PutMapping("/companies/{comp_id}/other_expense_types")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<OtherExpenseType> crupdateOtherExpenseTypes(
      @PathVariable String comp_id, @Valid @RequestBody List<CrupdateOtherExpenseType> toWrite) {
    return otherExpenseTypeMapper.toRestOtherExpenseTypes(
        otherExpenseTypeService.createOrUpdateAll(
            toWrite.stream().map(rest -> otherExpenseTypeMapper.toDomain(rest, comp_id)).toList()));
  }

  @DeleteMapping("/companies/{comp_id}/other_expense_types/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteOtherExpenseTypeById(@PathVariable String comp_id, @PathVariable String id) {
    otherExpenseTypeService.deleteById(id);
  }
}
