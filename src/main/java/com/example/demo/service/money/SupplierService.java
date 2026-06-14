package com.example.demo.service.money;

import com.example.demo.model.User;
import com.example.demo.model.exception.ForbiddenException;
import com.example.demo.model.exception.NotFoundException;
import com.example.demo.model.money.Supplier;
import com.example.demo.repository.money.SupplierRepository;
import com.example.demo.service.utils.ModificationUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SupplierService {

  private final SupplierRepository supplierRepository;
  private final ModificationUtils modificationUtils;

  public Supplier findById(String id) {
    return supplierRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Supplier with id " + id + " not found"));
  }

  public List<Supplier> findByCompanyId(String companyId) {
    return supplierRepository.findByCompany_Id(companyId);
  }

  public Page<Supplier> findByCompanyId(String companyId, Pageable pageable) {
    return supplierRepository.findByCompany_Id(companyId, pageable);
  }

  @Transactional
  public List<Supplier> createOrUpdateAll(List<Supplier> suppliers) {
    User currentUser = modificationUtils.takePrimaryUser();
    List<Supplier> processed = new ArrayList<>();
    for (Supplier supplier : suppliers) {
      Supplier existing =
          supplier.getId() != null
              ? supplierRepository.findById(supplier.getId()).orElse(null)
              : null;

      if (currentUser.getRole() == User.Role.WAREHOUSE_WORKER && existing != null) {
        if (existing.getCreatedBy() == null
            || !existing.getCreatedBy().getId().equals(currentUser.getId())) {
          throw new ForbiddenException("Warehouse worker can only update suppliers they created");
        }
      }

      modificationUtils.createOrUpdateModel(supplier, existing, supplier.getId(), currentUser);
      processed.add(supplier);
    }
    return supplierRepository.saveAll(processed);
  }

  @Transactional
  public void deleteById(String id) {
    supplierRepository.deleteById(id);
  }
}
