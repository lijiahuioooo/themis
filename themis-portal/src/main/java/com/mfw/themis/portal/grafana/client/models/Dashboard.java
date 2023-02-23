/* Licensed under Apache-2.0 */
package com.mfw.themis.portal.grafana.client.models;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dashboard {
  Long id;
  String uid;
  String title;
  List<DashboardRow> rows;
  Integer version;
  String timezone;
  List<String> tags;
  Integer schemaVersion;
  DashboardTemplate templating;
}
