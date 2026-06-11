package com.example.demo.integration.notification;

import static com.example.demo.integration.conf.TestNotificationFixtures.*;
import static com.example.demo.integration.conf.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.SentryConf;
import com.example.demo.client.api.NotificationApi;
import com.example.demo.client.invoker.ApiClient;
import com.example.demo.client.model.CrupdateNotification;
import com.example.demo.client.model.Notification;
import com.example.demo.client.model.UnreadNotificationCountResponse;
import com.example.demo.endpoint.rest.security.jwt.JwtUtils;
import com.example.demo.integration.conf.AbstractContextInitializer;
import com.example.demo.integration.conf.TestDataSqlLoader;
import com.example.demo.integration.conf.TestUtils;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@ContextConfiguration(initializers = NotificationIT.ContextInitializer.class)
class NotificationIT {
  @Autowired private DataSource dataSource;

  @MockitoBean private SentryConf sentryConf;
  @MockitoBean private AuthenticationManager authenticationManagerMock;
  @MockitoBean private JwtUtils jwtServiceMock;

  private static ApiClient anApiClient(String token) {
    return TestUtils.anApiClient(token, ContextInitializer.SERVER_PORT);
  }

  @BeforeEach
  void setUp() throws Exception {
    TestUtils.setUpJwtService(jwtServiceMock);
    TestUtils.setUpAuthenticationManager(authenticationManagerMock);
    TestDataSqlLoader.executeAllSqlScripts(dataSource);
  }

  @Test
  void admin_can_get_own_notifications() throws Exception {
    var client = HttpClient.newHttpClient();
    var request =
        HttpRequest.newBuilder()
            .uri(
                URI.create(
                    "http://localhost:"
                        + ContextInitializer.SERVER_PORT
                        + "/users/"
                        + ADMIN_ID
                        + "/companies/"
                        + COMPANY1_ID
                        + "/notifications"))
            .header("Authorization", "Bearer " + ADMIN_TOKEN)
            .header("Accept", "application/json")
            .GET()
            .build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    System.err.println("RAW JSON: " + response.body());
    client.close();

    NotificationApi api = new NotificationApi(anApiClient(ADMIN_TOKEN));
    List<Notification> notifs = api.getNotifications(ADMIN_ID, COMPANY1_ID, null, null, null, null);
    assertEquals(4, notifs.size());
    assertTrue(notifs.stream().anyMatch(n -> "notif_admin_unread".equals(n.getId())));
    assertTrue(notifs.stream().anyMatch(n -> "notif_admin_read".equals(n.getId())));
    assertTrue(notifs.stream().anyMatch(n -> "notif_admin_completed".equals(n.getId())));
  }

  @Test
  void employee_sees_only_own_notifications() throws Exception {
    NotificationApi api = new NotificationApi(anApiClient(EMPLOYEE_TOKEN));
    List<Notification> notifs =
        api.getNotifications(EMPLOYEE_ID, COMPANY1_ID, null, null, null, null);
    assertEquals(2, notifs.size());
    assertTrue(notifs.stream().anyMatch(n -> "notif_employee_unread".equals(n.getId())));
    assertTrue(notifs.stream().anyMatch(n -> "notif_employee_read".equals(n.getId())));
  }

  @Test
  void user_with_bad_token_cannot_get_notifications() {
    NotificationApi api = new NotificationApi(anApiClient(BAD_TOKEN));
    assertThrowsNotAuthorizedException(
        () -> api.getNotifications(ADMIN_ID, COMPANY1_ID, null, null, null, null));
  }

  @Test
  void admin_can_filter_by_read() throws Exception {
    NotificationApi api = new NotificationApi(anApiClient(ADMIN_TOKEN));
    List<Notification> readNotifs =
        api.getNotifications(ADMIN_ID, COMPANY1_ID, null, null, true, null);
    assertEquals(1, readNotifs.size());
    assertEquals("notif_admin_read", readNotifs.get(0).getId());
  }

  @Test
  void admin_can_filter_by_completed() throws Exception {
    NotificationApi api = new NotificationApi(anApiClient(ADMIN_TOKEN));
    List<Notification> notifs =
        api.getNotifications(ADMIN_ID, COMPANY1_ID, null, null, null, false);
    assertEquals(3, notifs.size());
    assertTrue(notifs.stream().noneMatch(n -> "notif_admin_completed".equals(n.getId())));
  }

  @Test
  void admin_can_get_unread_count() throws Exception {
    NotificationApi api = new NotificationApi(anApiClient(ADMIN_TOKEN));
    UnreadNotificationCountResponse count = api.getUnreadNotificationCount(ADMIN_ID, COMPANY1_ID);
    assertEquals(Long.valueOf(3), count.getUnreadCount());
  }

  @Test
  void employee_can_get_unread_count() throws Exception {
    NotificationApi api = new NotificationApi(anApiClient(EMPLOYEE_TOKEN));
    UnreadNotificationCountResponse count =
        api.getUnreadNotificationCount(EMPLOYEE_ID, COMPANY1_ID);
    assertEquals(Long.valueOf(1), count.getUnreadCount());
  }

  @Test
  void admin_can_get_notification_by_id() throws Exception {
    NotificationApi api = new NotificationApi(anApiClient(ADMIN_TOKEN));
    Notification notif = api.getNotificationById(ADMIN_ID, COMPANY1_ID, "notif_admin_unread");
    assertEquals("notif_admin_unread", notif.getId());
    assertEquals("Nouveau rapport", notif.getTitle());
    assertFalse(notif.getRead());
    assertFalse(notif.getCompleted());
  }

  @Test
  void admin_cannot_get_others_notification() {
    NotificationApi api = new NotificationApi(anApiClient(ADMIN_TOKEN));
    assertThrowsApiException(
        "Notification not found with id: notif_employee_unread",
        () -> api.getNotificationById(ADMIN_ID, COMPANY1_ID, "notif_employee_unread"));
  }

  @Test
  void get_notification_returns_404_for_unknown() {
    NotificationApi api = new NotificationApi(anApiClient(ADMIN_TOKEN));
    assertThrowsApiException(
        "Notification not found with id: unknown_id",
        () -> api.getNotificationById(ADMIN_ID, COMPANY1_ID, "unknown_id"));
  }

  @Test
  @DirtiesContext
  void admin_can_mark_notification_as_read() throws Exception {
    NotificationApi api = new NotificationApi(anApiClient(ADMIN_TOKEN));
    Notification notif = api.markNotificationAsRead(ADMIN_ID, COMPANY1_ID, "notif_admin_unread");
    assertTrue(notif.getRead());
    assertNotNull(notif.getReadAt());
  }

  @Test
  @DirtiesContext
  void admin_can_mark_notification_as_completed() throws Exception {
    NotificationApi api = new NotificationApi(anApiClient(ADMIN_TOKEN));
    Notification notif =
        api.markNotificationAsCompleted(ADMIN_ID, COMPANY1_ID, "notif_admin_unread");
    assertTrue(notif.getCompleted());
    assertNotNull(notif.getCompletedAt());
  }

  @Test
  @DirtiesContext
  void employee_can_mark_own_notification_as_read() throws Exception {
    NotificationApi api = new NotificationApi(anApiClient(EMPLOYEE_TOKEN));
    Notification notif =
        api.markNotificationAsRead(EMPLOYEE_ID, COMPANY1_ID, "notif_employee_unread");
    assertTrue(notif.getRead());
  }

  @Test
  void employee_cannot_mark_admins_notification_as_read() {
    NotificationApi api = new NotificationApi(anApiClient(EMPLOYEE_TOKEN));
    assertThrowsApiException(
        "Notification not found with id: notif_admin_unread",
        () -> api.markNotificationAsRead(EMPLOYEE_ID, COMPANY1_ID, "notif_admin_unread"));
  }

  @Test
  @DirtiesContext
  void admin_can_create_notification() throws Exception {
    NotificationApi api = new NotificationApi(anApiClient(ADMIN_TOKEN));
    CrupdateNotification toCreate = new CrupdateNotification();
    toCreate.setId("new_notif_id");
    toCreate.setTitle("Notification de test");
    toCreate.setMessage("Créée via l API");
    toCreate.setRead(false);
    toCreate.setCompleted(false);
    toCreate.setUserId(ADMIN_ID);
    List<Notification> created =
        api.crupdateNotifications(ADMIN_ID, COMPANY1_ID, List.of(toCreate));
    assertEquals(1, created.size());
    assertEquals("new_notif_id", created.get(0).getId());
    assertEquals("Notification de test", created.get(0).getTitle());

    List<Notification> all = api.getNotifications(ADMIN_ID, COMPANY1_ID, null, null, null, null);
    assertTrue(all.stream().anyMatch(n -> "new_notif_id".equals(n.getId())));
  }

  @Test
  void employee_cannot_create_notifications() {
    NotificationApi api = new NotificationApi(anApiClient(EMPLOYEE_TOKEN));
    assertThrowsForbiddenException(
        () ->
            api.crupdateNotifications(
                EMPLOYEE_ID, COMPANY1_ID, List.of(someCreatableNotification())));
  }

  @Test
  @DirtiesContext
  void notifications_are_sorted_by_created_at_desc() throws Exception {
    NotificationApi api = new NotificationApi(anApiClient(ADMIN_TOKEN));
    List<Notification> notifs = api.getNotifications(ADMIN_ID, COMPANY1_ID, null, null, null, null);
    assertEquals(4, notifs.size());
    for (int i = 1; i < notifs.size(); i++) {
      var prev = notifs.get(i - 1).getCreatedAt();
      var curr = notifs.get(i).getCreatedAt();
      assertTrue(prev.isAfter(curr) || prev.equals(curr));
    }
  }

  @Test
  @DirtiesContext
  void admin_can_delete_own_notification() throws Exception {
    NotificationApi api = new NotificationApi(anApiClient(ADMIN_TOKEN));
    api.deleteNotificationById(ADMIN_ID, COMPANY1_ID, "notif_admin_unread");
    List<Notification> notifs = api.getNotifications(ADMIN_ID, COMPANY1_ID, null, null, null, null);
    assertEquals(3, notifs.size());
  }

  @Test
  void effective_completed_is_true_when_notification_completed() throws Exception {
    NotificationApi api = new NotificationApi(anApiClient(ADMIN_TOKEN));
    Notification notif = api.getNotificationById(ADMIN_ID, COMPANY1_ID, "notif_admin_completed");
    assertTrue(notif.getEffectiveCompleted());
  }

  @Test
  void effective_completed_is_false_when_notification_not_completed() throws Exception {
    NotificationApi api = new NotificationApi(anApiClient(ADMIN_TOKEN));
    Notification notif = api.getNotificationById(ADMIN_ID, COMPANY1_ID, "notif_admin_unread");
    assertFalse(notif.getEffectiveCompleted());
  }

  @Test
  void effective_completed_is_true_when_linked_task_is_completed() throws Exception {
    NotificationApi api = new NotificationApi(anApiClient(ADMIN_TOKEN));
    Notification notif = api.getNotificationById(ADMIN_ID, COMPANY1_ID, "notif_admin_task");
    assertTrue(notif.getEffectiveCompleted());
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
