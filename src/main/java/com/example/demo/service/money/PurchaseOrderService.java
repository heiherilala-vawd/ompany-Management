package com.example.demo.service.money;

import com.example.demo.model.exception.NotFoundException;
import com.example.demo.model.money.PurchaseOrder;
import com.example.demo.repository.money.PurchaseOrderRepository;
import com.example.demo.repository.specification.SpecificationUtils;
import com.example.demo.service.utils.ModificationUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PurchaseOrderService {

  private final PurchaseOrderRepository purchaseOrderRepository;
  private final ModificationUtils modificationUtils;

  public PurchaseOrder findById(String id) {
    return purchaseOrderRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("PurchaseOrder with id " + id + " not found"));
  }

  public List<PurchaseOrder> findByCompanyId(String companyId, String jobId) {
    if (jobId == null) {
      return purchaseOrderRepository.findByCompany_Id(companyId);
    }
    return purchaseOrderRepository.findAll(
        SpecificationUtils.<PurchaseOrder>equal(companyId, "company", "id")
            .and(SpecificationUtils.<PurchaseOrder>equal(jobId, "job", "id")));
  }

  public List<PurchaseOrder> findByCompanyId(String companyId) {
    return findByCompanyId(companyId, null);
  }

  @Transactional
  public List<PurchaseOrder> createOrUpdateAll(List<PurchaseOrder> orders) {
    List<PurchaseOrder> processed = new ArrayList<>();
    for (PurchaseOrder order : orders) {
      PurchaseOrder existing =
          order.getId() != null
              ? purchaseOrderRepository.findById(order.getId()).orElse(null)
              : null;
      modificationUtils.createOrUpdateModel(
          order, existing, order.getId(), modificationUtils.takePrimaryUser());
      // Ensure lines reference the parent
      if (order.getLines() != null) {
        order.getLines().forEach(line -> line.setPurchaseOrder(order));
      }
      processed.add(order);
    }
    return purchaseOrderRepository.saveAll(processed);
  }

  @Transactional
  public void deleteById(String id) {
    purchaseOrderRepository.deleteById(id);
  }
}
