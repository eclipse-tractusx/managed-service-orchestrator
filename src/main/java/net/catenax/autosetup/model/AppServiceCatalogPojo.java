package net.catenax.autosetup.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppServiceCatalogPojo {

	private String canonicalServiceId;

	private String name;

	private String workflow;

	private String serviceTools;

}
