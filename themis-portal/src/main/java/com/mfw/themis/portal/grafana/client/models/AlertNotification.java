/* Licensed under Apache-2.0 */
package com.mfw.themis.portal.grafana.client.models;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class AlertNotification {
  Integer id;
  String name;
  String type;
  Boolean isDefault;
  AlertNotificationSettings settings;
  String created;
  String updated;
}
