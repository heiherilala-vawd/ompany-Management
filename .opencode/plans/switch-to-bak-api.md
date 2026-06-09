# Plan : Remplacer api.yml par la version .bak + ajouter notifications

## Phase 1 : api.yml

```bash
cp src/main/resources/api/api.yml.bak src/main/resources/api/api.yml
```

Puis éditer `api.yml` pour ajouter :

### 1a. Tag Notification (après `Report`, ligne ~153)
```yaml
  - name: Notification
    description: Notification CRUD
```

### 1b. Paths Notification (avant `components:`, ligne ~11365)
Ajouter après `$ref: '#/components/schemas/TimeSeriesResponse'` (ligne ~11363) :

<details>
<summary>Contenu des 7 endpoints Notification (cliquer pour dérouler)</summary>

```yaml
  # ╔══════════════════════════════════════════════════════════╗
  # ║  NOTIFICATIONS                                          ║
  # ╚══════════════════════════════════════════════════════════╝

  '/users/{userId}/companies/{companyId}/notifications/unread_count':
    get:
      tags:
        - Notification
      summary: Get unread notification count for the current user
      operationId: getUnreadNotificationCount
      parameters:
        - name: userId
          in: path
          required: true
          schema:
            type: string
          example: "user_123456"
        - name: companyId
          in: path
          required: true
          schema:
            type: string
          example: "comp_btp001"
      responses:
        '200':
          description: Unread notification count
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/UnreadNotificationCountResponse'
        '400':
          $ref: '#/components/responses/400'
        '403':
          $ref: '#/components/responses/403'
        '429':
          $ref: '#/components/responses/429'
        '500':
          $ref: '#/components/responses/500'

  '/users/{userId}/companies/{companyId}/notifications':
    get:
      tags:
        - Notification
      summary: Get all notifications for the current user
      operationId: getNotifications
      parameters:
        - name: userId
          in: path
          required: true
          schema:
            type: string
          example: "user_123456"
        - name: companyId
          in: path
          required: true
          schema:
            type: string
          example: "comp_btp001"
        - name: page
          in: query
          schema:
            $ref: '#/components/schemas/Page'
          example: 1
        - name: page_size
          in: query
          schema:
            $ref: '#/components/schemas/PageSize'
          example: 20
        - name: read
          in: query
          schema:
            type: boolean
          example: false
        - name: completed
          in: query
          schema:
            type: boolean
          example: false
      responses:
        '200':
          description: List of notifications
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/Notification'
        '400':
          $ref: '#/components/responses/400'
        '403':
          $ref: '#/components/responses/403'
        '429':
          $ref: '#/components/responses/429'
        '500':
          $ref: '#/components/responses/500'
    put:
      tags:
        - Notification
      summary: Create or update notifications
      operationId: crupdateNotifications
      parameters:
        - name: userId
          in: path
          required: true
          schema:
            type: string
          example: "user_123456"
        - name: companyId
          in: path
          required: true
          schema:
            type: string
          example: "comp_btp001"
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: array
              items:
                $ref: '#/components/schemas/CrupdateNotification'
      responses:
        '200':
          description: List of created/updated notifications
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/Notification'
        '400':
          $ref: '#/components/responses/400'
        '403':
          $ref: '#/components/responses/403'
        '429':
          $ref: '#/components/responses/429'
        '500':
          $ref: '#/components/responses/500'

  '/users/{userId}/companies/{companyId}/notifications/{id}':
    get:
      tags:
        - Notification
      summary: Get notification by identifier
      operationId: getNotificationById
      parameters:
        - name: userId
          in: path
          required: true
          schema:
            type: string
          example: "user_123456"
        - name: companyId
          in: path
          required: true
          schema:
            type: string
          example: "comp_btp001"
        - name: id
          in: path
          required: true
          schema:
            type: string
          example: "notif_001"
      responses:
        '200':
          description: The identified notification
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Notification'
        '400':
          $ref: '#/components/responses/400'
        '403':
          $ref: '#/components/responses/403'
        '404':
          $ref: '#/components/responses/404'
        '429':
          $ref: '#/components/responses/429'
        '500':
          $ref: '#/components/responses/500'
    delete:
      tags:
        - Notification
      summary: Delete notification by identifier
      operationId: deleteNotificationById
      parameters:
        - name: userId
          in: path
          required: true
          schema:
            type: string
          example: "user_123456"
        - name: companyId
          in: path
          required: true
          schema:
            type: string
          example: "comp_btp001"
        - name: id
          in: path
          required: true
          schema:
            type: string
          example: "notif_001"
      responses:
        '204':
          description: Notification deleted successfully
        '400':
          $ref: '#/components/responses/400'
        '403':
          $ref: '#/components/responses/403'
        '404':
          $ref: '#/components/responses/404'
        '429':
          $ref: '#/components/responses/429'
        '500':
          $ref: '#/components/responses/500'

  '/users/{userId}/companies/{companyId}/notifications/{id}/read':
    put:
      tags:
        - Notification
      summary: Mark notification as read
      operationId: markNotificationAsRead
      parameters:
        - name: userId
          in: path
          required: true
          schema:
            type: string
          example: "user_123456"
        - name: companyId
          in: path
          required: true
          schema:
            type: string
          example: "comp_btp001"
        - name: id
          in: path
          required: true
          schema:
            type: string
          example: "notif_001"
      responses:
        '200':
          description: Notification marked as read
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Notification'
        '400':
          $ref: '#/components/responses/400'
        '403':
          $ref: '#/components/responses/403'
        '404':
          $ref: '#/components/responses/404'
        '429':
          $ref: '#/components/responses/429'
        '500':
          $ref: '#/components/responses/500'

  '/users/{userId}/companies/{companyId}/notifications/{id}/complete':
    put:
      tags:
        - Notification
      summary: Mark notification as completed
      operationId: markNotificationAsCompleted
      parameters:
        - name: userId
          in: path
          required: true
          schema:
            type: string
          example: "user_123456"
        - name: companyId
          in: path
          required: true
          schema:
            type: string
          example: "comp_btp001"
        - name: id
          in: path
          required: true
          schema:
            type: string
          example: "notif_001"
      responses:
        '200':
          description: Notification marked as completed
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Notification'
        '400':
          $ref: '#/components/responses/400'
        '403':
          $ref: '#/components/responses/403'
        '404':
          $ref: '#/components/responses/404'
        '429':
          $ref: '#/components/responses/429'
        '500':
          $ref: '#/components/responses/500'
```
</details>

### 1c. Schémas Notification (à la fin du fichier)
Ajouter après le dernier `comment: type: string` du fichier (qui clôt `CrupdateMaintenanceSchedule`) :

```yaml

    # =========================
    # NOTIFICATION
    # =========================
    Notification:
      allOf:
        - $ref: '#/components/schemas/AuditFields'
      properties:
        id:
          type: string
          example: "notif_001"
        user:
          $ref: '#/components/schemas/CrupdateUser'
        task_id:
          type: string
          nullable: true
          example: "task_001"
        title:
          type: string
          example: "Nouvelle t\u00e2che assign\u00e9e"
        message:
          type: string
          nullable: true
          example: "Vous avez \u00e9t\u00e9 assign\u00e9 \u00e0 la t\u00e2che 'R\u00e9paration moteur'"
        read:
          type: boolean
          example: false
        read_at:
          type: string
          format: date-time
          nullable: true
        completed:
          type: boolean
          example: false
        completed_at:
          type: string
          format: date-time
          nullable: true
        effective_completed:
          type: boolean
          example: false
    CrupdateNotification:
      required:
        - title
        - user_id
      allOf:
        - $ref: '#/components/schemas/comment'
      properties:
        id:
          type: string
          example: "notif_001"
        user_id:
          type: string
          example: "usr_123456"
        task_id:
          type: string
          nullable: true
          example: "task_001"
        title:
          type: string
          example: "Nouvelle t\u00e2che assign\u00e9e"
        message:
          type: string
          example: "Vous avez \u00e9t\u00e9 assign\u00e9 \u00e0 la t\u00e2che 'R\u00e9paration moteur'"
        read:
          type: boolean
          example: false
        read_at:
          type: string
          format: date-time
          nullable: true
        completed:
          type: boolean
          example: false
        completed_at:
          type: string
          format: date-time
          nullable: true
    UnreadNotificationCountResponse:
      properties:
        unread_count:
          type: integer
          format: int64
          example: 3
```

## Phase 2 : Régénérer le client

```bash
./gradlew publishJavaClientToMavenLocal
```

## Phase 3 : Mettre à jour les 36 controllers

Pour chaque controller, le pattern de changement est :

**AVANT :**
```java
@GetMapping("/companies/{comp_id}/resources")
public List<Resource> getResources(@PathVariable String comp_id, ...) {
```

**APRÈS :**
```java
@GetMapping("/users/{userId}/companies/{companyId}/resources")
public List<Resource> getResources(@PathVariable String userId, @PathVariable String companyId, ...) {
```

### Liste des 36 controllers à modifier :

1. `UserController.java`
2. `JobController.java`
3. `DepartmentController.java`
4. `TeamController.java`
5. `LeaveController.java`
6. `LeaveTypeController.java`
7. `EmployeeLeaveConfigController.java`
8. `TaskController.java`
9. `TaskScheduleController.java`
10. `YearlyReportController.java`
11. `DashboardController.java`
12. `ExpenseController.java`
13. `IncomeController.java`
14. `EmployeePaymentController.java`
15. `TravelExpenseController.java`
16. `PurchaseController.java`
17. `BankFeeController.java`
18. `OtherExpenseController.java`
19. `LoanController.java`
20. `CompanyFixedCostController.java`
21. `SupplierController.java`
22. `PurchaseOrderController.java`
23. `PurchaseOperationController.java`
24. `IncomeTypeController.java`
25. `OtherExpenseTypeController.java`
26. `CashTransactionController.java`
27. `CashAccountController.java`
28. `BudgetLineController.java`
29. `EquipmentController.java`
30. `WarehouseController.java`
31. `TravelEquipmentController.java`
32. `TravelPeopleController.java`
33. `TravelMaterialsController.java`
34. `MaterialConsumptionController.java`
35. `EquipmentUsageController.java`
36. `MaintenanceController.java`

Sous-ressources (ex: `/companies/{comp_id}/job/{job_id}/user/{user_id}/...`) deviennent :
`/users/{userId}/companies/{companyId}/jobs/{jobId}/users/{targetUserId}/...`

## Phase 4 : Ajustements supplémentaires

- `MaterialController` (déjà en nouveau pattern, nettoyer `comp_id` → `companyId`)
- `TravelOperationController` (déjà en nouveau pattern, nettoyer `job_id`/`user_id` → `jobId`/`userId`)

## Phase 5 : Compilation et test

```bash
./gradlew compileJava && ./gradlew bootRun
```
