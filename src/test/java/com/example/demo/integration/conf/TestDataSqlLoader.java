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
          "db/testdata/V100_2__testdata_companies.sql",
          "db/testdata/V100_3__testdata_users.sql",
          "db/testdata/V100_4__testdata_jobs.sql",
          "db/testdata/V100_5__testdata_warehouses.sql",
          "db/testdata/V100_6__testdata_equipment.sql",
          "db/testdata/V100_7__testdata_materials.sql",
          "db/testdata/V100_7_1__testdata_income_types.sql",
          "db/testdata/V100_33__testdata_leave_types.sql",
          "db/testdata/V100_8__testdata_incomes.sql",
          "db/testdata/V100_22__testdata_income_receipts.sql",
          "db/testdata/V100_9__testdata_expenses.sql",
          "db/testdata/V100_10__testdata_employee_payments.sql",
          "db/testdata/V100_34__testdata_employee_leave_configs.sql",
          "db/testdata/V100_28__testdata_employee_payment_users.sql",
          "db/testdata/V100_29__testdata_teams.sql",
          "db/testdata/V100_11__testdata_travel_expenses.sql",
          "db/testdata/V100_12__testdata_travel_details.sql",
          "db/testdata/V100_13__testdata_purchases.sql",
          "db/testdata/V100_14__testdata_bank_fees.sql",
          "db/testdata/V100_15__testdata_other_expenses.sql",
          "db/testdata/V100_30__testdata_other_expense_types.sql",
          "db/testdata/V100_31__testdata_maintenances.sql",
          "db/testdata/V100_32__testdata_company_fixed_costs.sql",
          "db/testdata/V100_35__testdata_leaves.sql",
          "db/testdata/V100_16__testdata_history.sql",
          "db/testdata/V100_17__testdata_material_waterhouse.sql",
          "db/testdata/V100_20__testdata_loans.sql",
          "db/testdata/V100_21__testdata_loan_repayments.sql",
          "db/testdata/V100_23__testdata_loans_edge_cases.sql",
          "db/testdata/V100_24__testdata_loan_repayments_edge_cases.sql",
          "db/testdata/V100_25__testdata_incomes_edge_cases.sql",
          "db/testdata/V100_26__testdata_income_receipts_edge_cases.sql",
          "db/testdata/V100_27__testdata_user_job.sql",
          "db/testdata/V100_36__testdata_tasks.sql",
          "db/testdata/V100_37__testdata_task_schedules.sql",
          "db/testdata/V100_42__testdata_equipment_usages.sql",
          "db/testdata/V100_43__testdata_material_consumptions.sql",
          "db/testdata/V100_44__testdata_suppliers.sql",
          "db/testdata/V100_45__testdata_purchase_orders.sql",
          "db/testdata/V100_46__testdata_maintenance_schedules.sql");

  private TestDataSqlLoader() {}

  public static void executeAllSqlScripts(DataSource dataSource) throws Exception {
    try (Connection conn = dataSource.getConnection()) {
      for (String script : TESTDATA_SCRIPTS_IN_ORDER) {
        ScriptUtils.executeSqlScript(conn, new ClassPathResource(script));
      }
    }
  }
}
