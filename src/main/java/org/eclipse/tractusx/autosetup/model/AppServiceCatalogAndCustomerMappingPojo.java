package org.eclipse.tractusx.autosetup.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppServiceCatalogAndCustomerMappingPojo {

	private String customer;

	private String serviceId;

	private AppServiceCatalogPojo serviceCatalog;

	private String canonicalId;

}
