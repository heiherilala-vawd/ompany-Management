package com.example.demo.endpoint.rest.controller.money;

import com.example.demo.client.model.CrupdateIncomeType;
import com.example.demo.client.model.IncomeType;
import com.example.demo.endpoint.rest.mapper.money.IncomeTypeMapper;
import com.example.demo.service.money.IncomeTypeService;
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
public class IncomeTypeController {

  private final IncomeTypeService incomeTypeService;
  private final IncomeTypeMapper incomeTypeMapper;

  @GetMapping("/companies/{comp_id}/income_types")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<IncomeType> getIncomeTypes(@PathVariable String comp_id) {
    return incomeTypeMapper.toRestIncomeTypes(incomeTypeService.findAllByCompanyId(comp_id));
  }

  @PutMapping("/companies/{comp_id}/income_types")
  @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATION')")
  public List<IncomeType> crupdateIncomeTypes(
      @PathVariable String comp_id, @RequestBody List<CrupdateIncomeType> toWrite) {
    toWrite.forEach(
        incomeType -> {
          if (incomeType.getCompanyId() == null) {
            incomeType.setCompanyId(comp_id);
          }
        });
    return incomeTypeMapper.toRestIncomeTypes(
        incomeTypeService.createOrUpdateAll(
            toWrite.stream().map(incomeTypeMapper::toDomain).toList()));
  }

  @DeleteMapping("/companies/{comp_id}/income_types/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteIncomeTypeById(@PathVariable String comp_id, @PathVariable String id) {
    incomeTypeService.deleteById(id);
  }
}
