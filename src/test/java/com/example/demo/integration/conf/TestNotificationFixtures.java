package com.example.demo.integration.conf;

import static com.example.demo.integration.conf.TestUtils.*;

import com.example.demo.client.model.CrupdateNotification;
import com.example.demo.client.model.Notification;
import java.util.UUID;

public final class TestNotificationFixtures {

  private TestNotificationFixtures() {}

  static Notification notifAdminUnread() {
    Notification n = new Notification();
    n.setId("notif_admin_unread");
    n.setTitle("Nouveau rapport");
    n.setMessage("Le rapport mensuel est disponible");
    n.setRead(false);
    n.setCompleted(false);
    return n;
  }

  static Notification notifAdminRead() {
    Notification n = new Notification();
    n.setId("notif_admin_read");
    n.setTitle("Rapport consulté");
    n.setMessage("Vous avez consulté le rapport");
    n.setRead(true);
    n.setCompleted(false);
    return n;
  }

  static Notification notifAdminCompleted() {
    Notification n = new Notification();
    n.setId("notif_admin_completed");
    n.setTitle("Tâche terminée");
    n.setMessage("La tâche de maintenance est terminée");
    n.setRead(false);
    n.setCompleted(true);
    return n;
  }

  static Notification notifAdminTask() {
    Notification n = new Notification();
    n.setId("notif_admin_task");
    n.setTitle("Tâche assignée");
    n.setMessage("Vous avez été assigné à une tâche");
    n.setRead(false);
    n.setCompleted(false);
    return n;
  }

  static Notification notifEmployeeUnread() {
    Notification n = new Notification();
    n.setId("notif_employee_unread");
    n.setTitle("Nouvelle mission");
    n.setMessage("Une nouvelle mission vous est assignée");
    n.setRead(false);
    n.setCompleted(false);
    return n;
  }

  public static CrupdateNotification someCreatableNotification() {
    CrupdateNotification n = new CrupdateNotification();
    n.setId(UUID.randomUUID().toString());
    n.setTitle("Nouvelle notification");
    n.setMessage("Message de test");
    n.setRead(false);
    n.setCompleted(false);
    n.setUserId(ADMIN_ID);
    return n;
  }
}
