# Rapport des endpoints PUT / POST — Nullabilité des payloads

> Généré le 2026-06-05 par analyse croisée de `api.yml`, des DTO générés (`@Nonnull`/`@Nullable`), des entités JPA (`@NotNull`/`@NotBlank`) et des mappers.

---

## Légende

| Symbole | Signification |
|---|---|
| **🔴 Required** | Le champ est obligatoire dans la spec API (`required`) + `@Nonnull` dans le DTO |
| **🟡 Optional** | Le champ est optionnel (`@Nullable` dans le DTO) |
| **⚪ Ignoré** | Champ technique (id, company_id) géré par le contexte |
| **⚠️ Mismatch** | Divergence detectée entre spec/DAO/entité → décision prise |

---

## POST /auth/register
**Schema:** `CrupdateUser`

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `id` | string | 🟡 Optional | null → création |
| `first_name` | string | 🔴 Required | |
| `last_name` | string | 🔴 Required | |
| `sex` | string (Sex) | 🔴 Required | |
| `email` | string (email) | 🔴 Required | |
| `password` | string | 🔴 Required | |
| `role` | string (Role) | 🔴 Required | |
| `company_id` | string | 🟡 Optional | |
| `birth_date` | string (date) | 🟡 Optional | |
| `manager_id` | string | 🟡 Optional | |
| `department_id` | string | 🟡 Optional | |
| `comment` | string | 🟡 Optional | |

---

## POST /auth/login
**Schema:** `LoginRequest`

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `email` | string | 🔴 Required | |
| `password` | string | 🔴 Required | |

---

## POST /companies/{comp_id}/job/{job_id}/user/{user_id}/purchase_operations
**Schema:** `PurchaseOperationRequest`

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `comment` | string | 🟡 Optional | |
| `equipment_lines` | array | 🟡 Optional | |
| `material_lines` | array | 🟡 Optional | |
| `travel` | object | 🟡 Optional | |

---

## POST /companies/{comp_id}/job/{job_id}/user/{user_id}/travel_operations
**Schema:** `TravelOperationRequest`

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `comment` | string | 🟡 Optional | |
| `travel` | object | 🟡 Optional | |
| `equipment_lines` | array | 🟡 Optional | |
| `material_lines` | array | 🟡 Optional | |
| `people_lines` | array | 🟡 Optional | |

---

## PUT /companies/{comp_id}/leaves
**Schema:** `CrupdateLeave` (array items)

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `id` | string | 🟡 Optional | null → création |
| `user_id` | string | 🔴 Required | |
| `leave_type_id` | string | 🔴 Required | |
| `start_date` | string (date) | 🔴 Required | |
| `end_date` | string (date) | 🔴 Required | |
| `duration_days` | number | 🔴 Required | |
| `status` | string (LeaveStatus) | 🔴 Required | |
| `reason` | string | 🟡 Optional | |
| `comment` | string | 🟡 Optional | |

---

## PUT /companies/{comp_id}/users
**Schema:** `CrupdateUser` (array items)

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `id` | string | 🟡 Optional | null → création |
| `first_name` | string | 🔴 Required | |
| `last_name` | string | 🔴 Required | |
| `sex` | string (Sex) | 🔴 Required | |
| `email` | string (email) | 🔴 Required | |
| `password` | string | 🔴 Required | |
| `role` | string (Role) | 🔴 Required | |
| `company_id` | string | 🟡 Optional | |
| `birth_date` | string (date) | 🟡 Optional | |
| `manager_id` | string | 🟡 Optional | |
| `department_id` | string | 🟡 Optional | |
| `comment` | string | 🟡 Optional | |

---

## PUT /companies
**Schema:** `CrupdateCompany` (array items)

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `id` | string | 🟡 Optional | null → création |
| `name` | string | 🔴 Required | |
| `company_type` | string (CompanyType) | 🔴 Required | |
| `rib` | string | 🟡 Optional | |
| `description` | string | 🟡 Optional | |
| `comment` | string | 🟡 Optional | |

---

## PUT /companies/{comp_id}/teams
**Schema:** `CrupdateTeam` (array items)

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `id` | string | 🟡 Optional | null → création |
| `name` | string | 🔴 Required | |
| `leader_id` | string | 🟡 Optional | |
| `member_ids` | array[string] | 🟡 Optional | |
| `comment` | string | 🟡 Optional | |

---

## PUT /companies/{comp_id}/jobs
**Schema:** `CrupdateJob` (array items)

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `id` | string | 🟡 Optional | null → création |
| `status` | string (JobStatus) | 🔴 Required | |
| `company_id` | string | 🟡 Optional | géré via path |
| `description` | string | 🟡 Optional | |
| `contract_signature_date` | string (date) | 🟡 Optional | |
| `start_date` | string (date) | 🟡 Optional | |
| `end_date` | string (date) | 🟡 Optional | |
| `comment` | string | 🟡 Optional | |

---

## PUT /companies/{comp_id}/warehouses
**Schema:** `CrupdateWarehouse` (array items)

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `id` | string | 🟡 Optional | null → création |
| `name` | string | 🔴 Required | |
| `description` | string | 🟡 Optional | |
| `job_id` | string | 🟡 Optional | |
| `comment` | string | 🟡 Optional | |

---

## PUT /companies/{comp_id}/equipment
**Schema:** `CrupdateEquipment` (array items)

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `id` | string | 🟡 Optional | null → création |
| `name` | string | 🔴 Required | |
| `warehouse_id` | string | 🔴 Required | |
| `est_en_panne` | boolean | 🔴 Required | ⚠️ Mismatch corrigé → required |
| `purchase_price` | number | 🔴 Required | ⚠️ Mismatch corrigé → required |
| `description` | string | 🟡 Optional | |
| `floor_number` | integer | 🟡 Optional | |
| `storage_number` | integer | 🟡 Optional | |
| `purchase_date` | string (date) | 🟡 Optional | |
| `category` | string | 🟡 Optional | |
| `comment` | string | 🟡 Optional | |

---

## PUT /companies/{comp_id}/maintenances
**Schema:** `CrupdateMaintenance` (array items)

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `id` | string | 🟡 Optional | null → création |
| `expense` | object (CrupdateExpenseMoney) | 🔴 Required | |
| `equipment_id` | string | 🔴 Required | |
| `description` | string | 🟡 Optional | |

---

## PUT /companies/{comp_id}/materials
**Schema:** `CrupdateMaterial` (array items)

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `id` | string | 🟡 Optional | null → création |
| `name` | string | 🔴 Required | |
| `unit` | string (MaterialUnit) | 🔴 Required | |
| `unit_price` | number | 🔴 Required | ⚠️ Mismatch corrigé → required |
| `description` | string | 🟡 Optional | |
| `company_id` | string | 🟡 Optional | géré via path |
| `comment` | string | 🟡 Optional | |

---

## PUT /companies/{comp_id}/material_warehouse
**Schema:** `CrupdateMaterialWarehouse` (array items)

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `material_id` | string | 🔴 Required | |
| `warehouse_id` | string | 🔴 Required | |
| `quantity` | integer | 🔴 Required | |

---

## PUT /companies/{comp_id}/job/{job_id}/user/{user_id}/expenses
**Schema:** `CrupdateExpenseMoney` (array items)

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `id` | string | 🟡 Optional | null → création |
| `job_id` | string | 🔴 Required | |
| `amount` | number | 🔴 Required | ⚠️ Mismatch corrigé → required |
| `description` | string | 🟡 Optional | |
| `comment` | string | 🟡 Optional | |

---

## PUT /companies/{comp_id}/job/{job_id}/user/{user_id}/incomes
**Schema:** `CrupdateIncomeMoney` (array items)

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `id` | string | 🟡 Optional | null → création |
| `amount` | number | 🔴 Required | ⚠️ Mismatch corrigé → required |
| `invoice_reference` | string | 🔴 Required | ⚠️ Mismatch corrigé → required |
| `billing_start_date` | string (date) | 🔴 Required | ⚠️ Mismatch corrigé → required |
| `facturation_date` | string (date-time) | 🔴 Required | ⚠️ Mismatch corrigé → required |
| `due_date` | string (date) | 🔴 Required | ⚠️ Mismatch corrigé → required |
| `organization_id` | string | 🔴 Required | |
| `job_id` | string | 🔴 Required | |
| `income_type_id` | string | 🔴 Required | |
| `description` | string | 🟡 Optional | |
| `comment` | string | 🟡 Optional | |
| `payment_terms` | string | 🟡 Optional | |
| `paid` | boolean | 🟡 Optional | |

---

## PUT /companies/{comp_id}/job/{job_id}/user/{user_id}/loans
**Schema:** `CrupdateLoan` (array items)

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `id` | string | 🟡 Optional | null → création |
| `amount` | number | 🔴 Required | |
| `interest_rate` | number | 🔴 Required | |
| `start_date` | string (date) | 🔴 Required | |
| `due_date` | string (date) | 🔴 Required | |
| `interest_type` | string (enum) | 🔴 Required | |
| `organization_id` | string | 🔴 Required | |
| `job_id` | string | 🔴 Required | |
| `description` | string | 🟡 Optional | |
| `comment` | string | 🟡 Optional | |

---

## PUT /companies/{comp_id}/job/{job_id}/user/{user_id}/loans_repayment
**Schema:** `CrupdateLoanRepayment` (array items)

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `id` | string | 🟡 Optional | null → création |
| `payment_date` | string (date) | 🔴 Required | |
| `amount` | number | 🔴 Required | |
| `loan_id` | string | 🔴 Required | |
| `principal_portion` | number | 🔴 Required | |
| `interest_portion` | number | 🔴 Required | |
| `comment` | string | 🟡 Optional | |

---

## PUT /companies/{comp_id}/job/{job_id}/user/{user_id}/travel_expenses
**Schema:** `CrupdateTravelExpense` (array items)

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `id` | string | 🟡 Optional | null → création |
| `expense` | object (CrupdateExpenseMoney) | 🔴 Required | |
| `departure_location` | object (CrupdateWarehouse) | 🔴 Required | ⚠️ Mismatch corrigé → required |
| `arrival_location` | object (CrupdateWarehouse) | 🔴 Required | ⚠️ Mismatch corrigé → required |
| `departure_date` | string (date-time) | 🔴 Required | ⚠️ Mismatch corrigé → required |
| `arrival_date` | string (date-time) | 🔴 Required | ⚠️ Mismatch corrigé → required |

---

## PUT /companies/{comp_id}/job/{job_id}/user/{user_id}/purchases
**Schema:** `CrupdatePurchase` (array items)

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `id` | string | 🟡 Optional | null → création |
| `expense` | object (CrupdateExpenseMoney) | 🔴 Required | |
| `source_warehouse_id` | string | 🔴 Required | |
| `quantity` | integer | 🔴 Required | |
| `is_equipment` | boolean | 🔴 Required | |
| `invoice_date` | string (date) | 🔴 Required | |
| `due_date` | string (date) | 🔴 Required | ⚠️ Mismatch corrigé → required |
| `supplier_id` | string | 🟡 Optional | |
| `equipment` | object (CrupdateEquipment) | 🟡 Optional | |
| `material` | string | 🟡 Optional | |
| `paid_at` | string (date) | 🟡 Optional | |

---

## PUT /companies/{comp_id}/job/{job_id}/user/{user_id}/bank_fees
**Schema:** `CrupdateBankFee` (array items)

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `id` | string | 🟡 Optional | null → création |
| `expense` | object (CrupdateExpenseMoney) | 🔴 Required | |
| `bank_name` | string | 🔴 Required | ⚠️ Mismatch corrigé → required |
| `description` | string | 🟡 Optional | |

---

## PUT /companies/{comp_id}/job/{job_id}/user/{user_id}/other_expenses
**Schema:** `CrupdateOtherExpense` (array items)

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `id` | string | 🟡 Optional | null → création |
| `expense` | object (CrupdateExpenseMoney) | 🔴 Required | |
| `other_expense_type_id` | string | 🔴 Required | ⚠️ Mismatch corrigé → required |
| `description` | string | 🟡 Optional | |

---

## PUT /companies/{comp_id}/job/{job_id}/user/{user_id}/employee_payments
**Schema:** `CrupdateEmployeePayment` (array items)

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `id` | string | 🟡 Optional | null → création |
| `expense` | object (CrupdateExpenseMoney) | 🔴 Required | |
| `is_for_team` | boolean | 🔴 Required | |
| `payment_type` | string (PaymentType) | 🔴 Required | |
| `payment_description` | string | 🟡 Optional | ⚠️ Mismatch corrigé → nullable |
| `user_ids` | array[string] | 🟡 Optional | |
| `team_id` | string | 🟡 Optional | |

---

## PUT /companies/{comp_id}/job/{job_id}/user/{user_id}/travel_people
**Schema:** `CrupdateTravelPeople` (array items)

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `id` | string | 🟡 Optional | null → création |
| `travel_id` | string | 🔴 Required | |
| `user_id` | string | 🔴 Required | |
| `arrival_location` | string | 🔴 Required | ⚠️ Mismatch corrigé → required |
| `arrival_date` | string (date-time) | 🟡 Optional | |
| `comment` | string | 🟡 Optional | |

---

## PUT /companies/{comp_id}/job/{job_id}/user/{user_id}/travel_materials
**Schema:** `CrupdateTravelMaterials` (array items)

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `id` | string | 🟡 Optional | null → création |
| `travel_id` | string | 🔴 Required | |
| `material` | string | 🔴 Required | |
| `quantity` | integer | 🔴 Required | |
| `arrival_location` | string | 🔴 Required | ⚠️ Mismatch corrigé → required |
| `quantity_received` | integer | 🟡 Optional | |
| `arrival_date` | string (date-time) | 🟡 Optional | |
| `departure_location` | string | 🟡 Optional | |
| `comment` | string | 🟡 Optional | |

---

## PUT /companies/{comp_id}/job/{job_id}/user/{user_id}/travel_equipment
**Schema:** `CrupdateTravelEquipment` (array items)

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `id` | string | 🟡 Optional | null → création |
| `travel_id` | string | 🔴 Required | |
| `equipment` | string | 🔴 Required | |
| `quantity` | integer | 🔴 Required | |
| `status` | string (TransportStatus) | 🔴 Required | ⚠️ Mismatch corrigé → required |
| `arrival_location` | string | 🔴 Required | ⚠️ Mismatch corrigé → required |
| `arrival_date` | string (date-time) | 🟡 Optional | |
| `departure_location` | string | 🟡 Optional | |
| `comment` | string | 🟡 Optional | |

---

## PUT /companies/{comp_id}/fixed_costs
**Schema:** `CrupdateCompanyFixedCost` (array items)

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `id` | string | 🟡 Optional | null → création |
| `name` | string | 🔴 Required | |
| `amount` | number | 🔴 Required | |
| `start_date` | string (date) | 🔴 Required | |
| `description` | string | 🟡 Optional | |
| `company_id` | string | 🟡 Optional | géré via path |
| `end_date` | string (date) | 🟡 Optional | |
| `comment` | string | 🟡 Optional | |

---

## PUT /companies/{comp_id}/suppliers
**Schema:** `CrupdateSupplier` (array items)

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `id` | string | 🟡 Optional | null → création |
| `name` | string | 🔴 Required | |
| `siret` | string | 🔴 Required | |
| `email` | string (email) | 🔴 Required | |
| `address` | string | 🟡 Optional | |
| `company_id` | string | 🟡 Optional | géré via path |
| `phone` | string | 🟡 Optional | |
| `contact_name` | string | 🟡 Optional | |
| `comment` | string | 🟡 Optional | |

---

## PUT /companies/{comp_id}/organizations
**Schema:** `CrupdateOrganization` (array items)

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `id` | string | 🟡 Optional | null → création |
| `name` | string | 🔴 Required | |
| `company_id` | string | 🔴 Required | |
| `email` | string (email) | 🟡 Optional | ⚠️ Mismatch → entité à corriger |
| `address` | string | 🟡 Optional | |
| `phone` | string | 🟡 Optional | |
| `contact_name` | string | 🟡 Optional | |
| `comment` | string | 🟡 Optional | |

---

## PUT /companies/{comp_id}/purchase_orders
**Schema:** `CrupdatePurchaseOrder` (array items)

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `id` | string | 🟡 Optional | null → création |
| `supplier_id` | string | 🔴 Required | |
| `order_date` | string (date) | 🔴 Required | |
| `status` | string (PurchaseOrderStatus) | 🔴 Required | |
| `total_amount` | number | 🔴 Required | |
| `job_id` | string | 🔴 Required | |
| `company_id` | string | 🟡 Optional | géré via path |
| `lines` | array | 🟡 Optional | |
| `comment` | string | 🟡 Optional | |

---

## PUT /companies/{comp_id}/tasks
**Schema:** `CrupdateTask` (array items)

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `id` | string | 🟡 Optional | null → création |
| `title` | string | 🔴 Required | |
| `priority` | string (TaskPriority) | 🔴 Required | |
| `completed` | boolean | 🔴 Required | ⚠️ Mismatch corrigé → required |
| `description` | string | 🟡 Optional | |
| `due_date` | string (date) | 🟡 Optional | |
| `company_id` | string | 🟡 Optional | géré via path |
| `assigned_user_ids` | array[string] | 🟡 Optional | |

---

## PUT /companies/{comp_id}/task_schedules
**Schema:** `CrupdateTaskSchedule` (array items)

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `id` | string | 🟡 Optional | null → création |
| `title` | string | 🔴 Required | |
| `priority` | string (TaskPriority) | 🔴 Required | |
| `frequency` | string | 🔴 Required | |
| `scheduled_date` | string (date) | 🔴 Required | |
| `description` | string | 🟡 Optional | |
| `company_id` | string | 🟡 Optional | géré via path |
| `assigned_user_ids` | array[string] | 🟡 Optional | |
| `comment` | string | 🟡 Optional | |

---

## PUT /companies/{comp_id}/departments
**Schema:** `CrupdateDepartment` (array items)

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `id` | string | 🟡 Optional | null → création |
| `name` | string | 🔴 Required | |
| `description` | string | 🟡 Optional | |
| `company_id` | string | 🟡 Optional | géré via path |
| `comment` | string | 🟡 Optional | |

---

## PUT /companies/{comp_id}/cash_accounts
**Schema:** `CrupdateCashAccount` (array items)

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `id` | string | 🟡 Optional | null → création |
| `name` | string | 🔴 Required | |
| `balance` | number | 🔴 Required | |
| `description` | string | 🟡 Optional | |
| `company_id` | string | 🟡 Optional | géré via path |
| `comment` | string | 🟡 Optional | |

---

## PUT /companies/{comp_id}/cash_accounts/{account_id}/transactions
**Schema:** `CrupdateCashTransaction` (array items)

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `id` | string | 🟡 Optional | null → création |
| `cash_account_id` | string | 🔴 Required | |
| `amount` | number | 🔴 Required | |
| `transaction_date` | string (date) | 🔴 Required | |
| `type` | string (CashTransactionType) | 🔴 Required | |
| `description` | string | 🟡 Optional | |
| `comment` | string | 🟡 Optional | |

---

## PUT /companies/{comp_id}/budget_lines
**Schema:** `CrupdateBudgetLine` (array items)

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `id` | string | 🟡 Optional | null → création |
| `category` | string | 🔴 Required | |
| `planned_amount` | number | 🔴 Required | |
| `actual_amount` | number | 🔴 Required | |
| `period_start` | string (date) | 🔴 Required | |
| `period_end` | string (date) | 🔴 Required | |
| `company_id` | string | 🟡 Optional | géré via path |
| `description` | string | 🟡 Optional | |
| `comment` | string | 🟡 Optional | |

---

## PUT /companies/{comp_id}/equipment_usage
**Schema:** `CrupdateEquipmentUsage` (array items)

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `id` | string | 🟡 Optional | null → création |
| `equipment_id` | string | 🔴 Required | |
| `job_id` | string | 🔴 Required | |
| `start_time` | string (date-time) | 🔴 Required | |
| `usage_status` | string | 🔴 Required | |
| `used_by` | string | 🔴 Required | |
| `end_time` | string (date-time) | 🟡 Optional | |
| `comment` | string | 🟡 Optional | |

---

## PUT /companies/{comp_id}/material_consumption
**Schema:** `CrupdateMaterialConsumption` (array items)

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `id` | string | 🟡 Optional | null → création |
| `material_id` | string | 🔴 Required | |
| `warehouse_id` | string | 🔴 Required | |
| `quantity` | integer | 🔴 Required | |
| `consumption_date` | string (date) | 🔴 Required | |
| `job_id` | string | 🔴 Required | |
| `reason` | string | 🟡 Optional | |
| `comment` | string | 🟡 Optional | |

---

## PUT /companies/{comp_id}/leave_types
**Schema:** `CrupdateLeaveType` (array items)

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `id` | string | 🟡 Optional | null → création |
| `name` | string | 🔴 Required | |
| `paid` | boolean | 🔴 Required | ⚠️ Mismatch corrigé → required |
| `deduct_from_balance` | boolean | 🔴 Required | ⚠️ Mismatch corrigé → required |
| `days_per_year` | integer | 🔴 Required | ⚠️ Mismatch corrigé → required |
| `description` | string | 🟡 Optional | |
| `color` | string | 🟡 Optional | |
| `company_id` | string | 🟡 Optional | géré via path |
| `comment` | string | 🟡 Optional | |

---

## PUT /companies/{comp_id}/leave_configs
**Schema:** `CrupdateEmployeeLeaveConfig` (array items)

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `id` | string | 🟡 Optional | null → création |
| `user_id` | string | 🔴 Required | |
| `hire_date` | string (date) | 🔴 Required | |
| `contract_type` | string | 🔴 Required | |
| `vacation_days_per_month` | number | 🔴 Required | |
| `weekly_hours` | integer | 🔴 Required | |
| `company_id` | string | 🟡 Optional | géré via path |
| `end_date` | string (date) | 🟡 Optional | |
| `comment` | string | 🟡 Optional | |

---

## PUT /companies/{comp_id}/income_types
**Schema:** `CrupdateIncomeType` (array items)

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `id` | string | 🟡 Optional | null → création |
| `name` | string | 🔴 Required | |
| `description` | string | 🟡 Optional | |
| `company_id` | string | 🟡 Optional | géré via path |
| `comment` | string | 🟡 Optional | |

---

## PUT /companies/{comp_id}/other_expense_types
**Schema:** `CrupdateOtherExpenseType` (array items)

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `id` | string | 🟡 Optional | null → création |
| `name` | string | 🔴 Required | |
| `description` | string | 🟡 Optional | |
| `company_id` | string | 🟡 Optional | géré via path |
| `comment` | string | 🟡 Optional | |

---

## PUT /companies/{comp_id}/job/{job_id}/user/{user_id}/incomes_receipts
**Schema:** `CrupdateIncomeReceipt` (array items)

| Propriété | Type | Nullabilité | Note |
|---|---|---|---|
| `id` | string | 🟡 Optional | null → création |
| `payment_date` | string (date) | 🔴 Required | |
| `amount` | number | 🔴 Required | |
| `income_id` | string | 🔴 Required | |
| `comment` | string | 🟡 Optional | |

---

## PUT endpoints sans request body (action endpoints)

| Path | operationId | Note |
|---|---|---|
| `/companies/{comp_id}/jobs/{job_id}/users/{user_id}` | `assignUserToJob` | Pas de body |
| `/companies/{comp_id}/equipment_usage/{id}/return` | `returnEquipment` | Pas de body |
| `/companies/{comp_id}/material_consumption/{id}/complete` | `completeMaterialConsumption` | Pas de body |
| `/companies/{comp_id}/material_consumption/{id}/return` | `returnMaterialsFromConsumption` | Pas de body |

---

## Annexe : Mismatches détectés et décisions

| # | Schéma | Champ | Spec (api.yml) | Entité JPA | Décision |
|---|---|---|---|---|---|
| 1 | `CrupdateExpenseMoney` | `amount` | Optional | `@NotNull` | → **Required** |
| 2 | `CrupdateIncomeMoney` | `amount` | Optional | `@NotNull` | → **Required** |
| 3 | `CrupdateIncomeMoney` | `invoice_reference` | Optional | `@NotBlank` | → **Required** |
| 4 | `CrupdateIncomeMoney` | `billing_start_date` | Optional | `@NotNull` | → **Required** |
| 5 | `CrupdateIncomeMoney` | `facturation_date` | Optional | `@NotNull` | → **Required** |
| 6 | `CrupdateIncomeMoney` | `due_date` | Optional | `@NotNull` | → **Required** |
| 7 | `CrupdateTravelExpense` | `departure_location` | Optional | `@NotNull` | → **Required** |
| 8 | `CrupdateTravelExpense` | `arrival_location` | Optional | `@NotNull` | → **Required** |
| 9 | `CrupdateTravelExpense` | `departure_date` | Optional | `@NotNull` | → **Required** |
| 10 | `CrupdateTravelExpense` | `arrival_date` | Optional | `@NotNull` | → **Required** |
| 11 | `CrupdateEquipment` | `est_en_panne` | Optional | `@NotNull` | → **Required** |
| 12 | `CrupdateEquipment` | `purchase_price` | Optional | `@NotNull` | → **Required** |
| 13 | `CrupdateMaterial` | `unit_price` | Optional | `@NotNull` | → **Required** |
| 14 | `CrupdatePurchase` | `due_date` | Optional | `@NotNull` | → **Required** |
| 15 | `CrupdateBankFee` | `bank_name` | Optional | `@NotBlank` | → **Required** |
| 16 | `CrupdateOtherExpense` | `other_expense_type_id` | Optional | `@NotNull` | → **Required** |
| 17 | `CrupdateLeaveType` | `paid` | Optional | `@NotNull` | → **Required** |
| 18 | `CrupdateLeaveType` | `deduct_from_balance` | Optional | `@NotNull` | → **Required** |
| 19 | `CrupdateLeaveType` | `days_per_year` | Optional | `@NotNull` | → **Required** |
| 20 | `CrupdateTask` | `completed` | Optional | `@NotNull` | → **Required** |
| 21 | `CrupdateTravelEquipment` | `status` | Optional | `@NotNull` | → **Required** |
| 22 | `CrupdateTravelEquipment` | `arrival_location` | Optional | `@NotNull` | → **Required** |
| 23 | `CrupdateTravelMaterials` | `arrival_location` | Optional | `@NotNull` | → **Required** |
| 24 | `CrupdateTravelPeople` | `arrival_location` | Optional | `@NotNull` | → **Required** |
| 25 | `CrupdateEmployeePayment` | `payment_description` | Required | nullable | → **Optional** |
| 26 | `Organization` (entité) | `email` | Optional | `@NotBlank` | → **Nullable** (entité à corriger) |
