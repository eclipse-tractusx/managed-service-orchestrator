package net.catenax.autosetup.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_service_catalog_tbl")
public class AppServiceCatalog {

	@Id
	@Column(name = "service_catalog_id")
	private String serviceCatalogId;
	
	@Column(name = "ref_service_id")
	private String refServiceId;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "service_tools")
	private String serviceTools;
	
}
