package com.example.demo.service.hr;

import com.example.demo.model.BoundedPageSize;
import com.example.demo.model.PageFromOne;
import com.example.demo.model.hr.LeaveType;
import com.example.demo.repository.hr.LeaveTypeRepository;
import com.example.demo.service.utils.ModificationUtils;
import com.example.demo.service.utils.PageUtils;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LeaveTypeService {

  private final LeaveTypeRepository leaveTypeRepository;
  private final ModificationUtils modificationUtils;

  public Optional<LeaveType> findById(String id) {
    return leaveTypeRepository.findById(id);
  }

  public List<LeaveType> findByCompanyId(String companyId) {
    return leaveTypeRepository.findByCompanyId(companyId);
  }

  public Page<LeaveType> findAll(PageFromOne page, BoundedPageSize pageSize, String companyId) {
    Pageable pageable = PageUtils.createPageable(page, pageSize);
    return leaveTypeRepository.findByCompanyId(companyId).stream()
        .collect(
            java.util.stream.Collectors.collectingAndThen(
                java.util.stream.Collectors.toList(),
                list -> {
                  int start = (int) pageable.getOffset();
                  int end = Math.min(start + pageable.getPageSize(), list.size());
                  return new org.springframework.data.domain.PageImpl<>(
                      list.subList(start, end), pageable, list.size());
                }));
  }

  @Transactional
  public List<LeaveType> createOrUpdateAll(List<LeaveType> leaveTypes) {
    for (LeaveType leaveType : leaveTypes) {
      LeaveType existing = leaveTypeRepository.findById(leaveType.getId()).orElse(null);
      modificationUtils.createOrUpdateModel(
          leaveType, existing, leaveType.getId(), modificationUtils.takePrimaryUser());
    }
    return leaveTypeRepository.saveAll(leaveTypes);
  }

  @Transactional
  public void deleteById(String id) {
    leaveTypeRepository.deleteById(id);
  }
}
