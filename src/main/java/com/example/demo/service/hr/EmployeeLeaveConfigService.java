package com.example.demo.service.hr;

import com.example.demo.model.hr.EmployeeLeaveConfig;
import com.example.demo.repository.hr.EmployeeLeaveConfigRepository;
import com.example.demo.service.utils.ModificationUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeLeaveConfigService {

  private final EmployeeLeaveConfigRepository employeeLeaveConfigRepository;
  private final ModificationUtils modificationUtils;

  public List<EmployeeLeaveConfig> findByCompanyId(String companyId) {
    return employeeLeaveConfigRepository.findByCompanyId(companyId);
  }

  @Transactional
  public List<EmployeeLeaveConfig> createOrUpdateAll(List<EmployeeLeaveConfig> configs) {
    for (EmployeeLeaveConfig config : configs) {
      EmployeeLeaveConfig existing =
          employeeLeaveConfigRepository.findById(config.getId()).orElse(null);
      modificationUtils.createOrUpdateModel(
          config, existing, config.getId(), modificationUtils.takePrimaryUser());
    }
    return employeeLeaveConfigRepository.saveAll(configs);
  }

  @Transactional
  public void deleteById(String id) {
    employeeLeaveConfigRepository.deleteById(id);
  }
}
