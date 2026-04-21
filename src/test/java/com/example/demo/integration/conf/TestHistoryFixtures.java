package com.example.demo.integration.conf;

import static com.example.demo.integration.conf.TestUtils.*;

import com.example.demo.client.model.EntityType;
import com.example.demo.client.model.History;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class TestHistoryFixtures {

  public static final String HISTORY1_ID = "history1_id";
  public static final String HISTORY2_ID = "history2_id";
  public static final String HISTORY3_ID = "history3_id";
  public static final String HISTORY4_ID = "history4_id";
  public static final String HISTORY5_ID = "history5_id";

  public static History history1() {
    History history = new History();
    history.setId(HISTORY1_ID);
    history.setPreviousValue("{\"name\": \"Old Company Name\"}");
    history.setNewValue("{\"name\": \"BTP Construction\"}");
    history.setUserId(ADMIN_ID);
    history.setModifiedAt(OffsetDateTime.of(2024, 1, 15, 10, 30, 0, 0, ZoneOffset.UTC).toInstant());
    history.setEntityType(EntityType.COMPANY);
    history.setEntityId(COMPANY1_ID);
    return history;
  }

  public static History history2() {
    History history = new History();
    history.setId(HISTORY2_ID);
    history.setPreviousValue("{\"description\": \"Old description\"}");
    history.setNewValue("{\"description\": \"Entreprise de construction\"}");
    history.setUserId(ADMIN_ID);
    history.setModifiedAt(OffsetDateTime.of(2024, 1, 16, 14, 0, 0, 0, ZoneOffset.UTC).toInstant());
    history.setEntityType(EntityType.COMPANY);
    history.setEntityId(COMPANY1_ID);
    return history;
  }

  public static History history3() {
    History history = new History();
    history.setId(HISTORY3_ID);
    history.setPreviousValue("{\"status\": \"PENDING_SIGNATURE\"}");
    history.setNewValue("{\"status\": \"IN_PROGRESS\"}");
    history.setUserId(ADMINISTRATION_ID);
    history.setModifiedAt(OffsetDateTime.of(2024, 2, 1, 9, 0, 0, 0, ZoneOffset.UTC).toInstant());
    history.setEntityType(EntityType.JOB);
    history.setEntityId(JOB1_ID);
    return history;
  }

  public static History history4() {
    History history = new History();
    history.setId(HISTORY4_ID);
    history.setPreviousValue("{\"firstName\": \"John\"}");
    history.setNewValue("{\"firstName\": \"Johnny\"}");
    history.setUserId(ADMIN_ID);
    history.setModifiedAt(OffsetDateTime.of(2024, 2, 10, 11, 30, 0, 0, ZoneOffset.UTC).toInstant());
    history.setEntityType(EntityType.USER);
    history.setEntityId(EMPLOYEE_ID);
    return history;
  }

  public static History history5() {
    History history = new History();
    history.setId(HISTORY5_ID);
    history.setPreviousValue("{\"quantity\": 10}");
    history.setNewValue("{\"quantity\": 15}");
    history.setUserId(WAREHOUSE_ID);
    history.setModifiedAt(OffsetDateTime.of(2024, 2, 15, 16, 45, 0, 0, ZoneOffset.UTC).toInstant());
    history.setEntityType(EntityType.EQUIPMENT);
    history.setEntityId(EQUIPMENT1_ID);
    return history;
  }
}
