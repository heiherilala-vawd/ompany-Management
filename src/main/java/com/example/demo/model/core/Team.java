package com.example.demo.model.core;

import com.example.demo.model.CreatAndUpdateEntity;
import com.example.demo.model.User;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;

@Entity
@Table(name = "team")
@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Team extends CreatAndUpdateEntity {

  @Id private String id;

  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "leader_id")
  private User leader;

  @ManyToMany
  @JoinTable(
      name = "team_members",
      joinColumns = @JoinColumn(name = "team_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id"))
  @Builder.Default
  private List<User> members = new ArrayList<>();

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    Team team = (Team) o;
    return id != null && Objects.equals(id, team.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
