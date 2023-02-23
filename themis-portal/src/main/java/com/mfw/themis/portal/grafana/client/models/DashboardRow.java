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
public class DashboardRow {
  Boolean collapse;
  String title;
  String titleSize;
  Integer height;
  List<DashboardPanel> panels;
}
