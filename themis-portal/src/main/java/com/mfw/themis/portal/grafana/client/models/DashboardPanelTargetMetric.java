/* Licensed under Apache-2.0 */
package com.mfw.themis.portal.grafana.client.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardPanelTargetMetric {

    String field;
    String id;
    String type;
    DashboardPanelTargetSetting settings;
}
