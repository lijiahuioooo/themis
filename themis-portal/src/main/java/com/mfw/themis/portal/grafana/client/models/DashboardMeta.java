/* Licensed under Apache-2.0 */
package com.mfw.themis.portal.grafana.client.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardMeta {
  String uid;
  String url;
  String type;
  Boolean canSave;
  Boolean canEdit;
  Boolean canStar;
  String slug;
  String expires;
  String created;
  String updated;
  String updatedBy;
  String createdBy;
  Integer version;
}
