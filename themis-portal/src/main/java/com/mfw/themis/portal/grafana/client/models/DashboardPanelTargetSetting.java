/* Licensed under Apache-2.0 */
package com.mfw.themis.portal.grafana.client.models;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardPanelTargetSetting {
    String interval;
    List<String> percents;
    Integer min_doc_count;
    Integer trimEdges;
}
