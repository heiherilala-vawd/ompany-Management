package com.example.demo.model.money;

import com.example.demo.model.CreatAndUpdateEntity;
import jakarta.persistence.*;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class MonetaryMovement extends CreatAndUpdateEntity implements Serializable {
  private Integer amount;

  private String description;
}
