package org.eclipse.tractusx.autosetup.repository;

import java.util.List;

import org.eclipse.tractusx.autosetup.entity.AppServiceCatalogAndCustomerMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppServiceCatalogAndCustomerMappingRepository extends JpaRepository<AppServiceCatalogAndCustomerMapping, String> {
	
	public AppServiceCatalogAndCustomerMapping findTop1ByServiceId(String serviceId);
	
	public List<AppServiceCatalogAndCustomerMapping> findAllByServiceIdIn(List<String> serviceIds);

}
