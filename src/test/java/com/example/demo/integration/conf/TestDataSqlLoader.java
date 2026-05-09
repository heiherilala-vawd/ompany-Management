package com.example.demo.integration.conf;

import java.sql.Connection;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

/**
 * Réexécute les scripts Flyway de {@code db/testdata} dans l'ordre (reset des données entre tests
 * d'intégration). L'ordre doit correspondre aux dépendances FK entre jeux de données.
 */
public final class TestDataSqlLoader {

  private static final List<String> TESTDATA_SCRIPTS_IN_ORDER =
      List.of(
          "db/testdata/V100_1__testdata_cleanup.sql",
          "db/testdata/V100_2__testdata_users.sql",
          "db/testdata/V100_3__testdata_companies.sql",
          "db/testdata/V100_4__testdata_jobs.sql",
          "db/testdata/V100_5__testdata_warehouses.sql",
          "db/testdata/V100_6__testdata_equipment.sql",
          "db/testdata/V100_7__testdata_materials.sql",
          "db/testdata/V100_7_1__testdata_income_types.sql",
          "db/testdata/V100_8__testdata_incomes.sql",
          "db/testdata/V100_9__testdata_expenses.sql",
          "db/testdata/V100_10__testdata_employee_payments.sql",
          "db/testdata/V100_11__testdata_travel_expenses.sql",
          "db/testdata/V100_12__testdata_travel_details.sql",
          "db/testdata/V100_13__testdata_purchases.sql",
          "db/testdata/V100_14__testdata_bank_fees.sql",
          "db/testdata/V100_15__testdata_other_expenses.sql",
          "db/testdata/V100_16__testdata_history.sql",
          "db/testdata/V100_17__testdata_material_waterhouse.sql",
          "db/testdata/V100_20__testdata_loans.sql",
          "db/testdata/V100_21__testdata_loan_repayments.sql");

  private TestDataSqlLoader() {}

  public static void executeAllSqlScripts(DataSource dataSource) throws Exception {
    try (Connection conn = dataSource.getConnection()) {
      for (String script : TESTDATA_SCRIPTS_IN_ORDER) {
        ScriptUtils.executeSqlScript(conn, new ClassPathResource(script));
      }
    }
  }
}
