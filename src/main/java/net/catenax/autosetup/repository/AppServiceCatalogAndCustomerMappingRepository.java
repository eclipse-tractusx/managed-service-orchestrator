package net.catenax.autosetup.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.catenax.autosetup.entity.AppServiceCatalogAndCustomerMapping;

public interface AppServiceCatalogAndCustomerMappingRepository extends JpaRepository<AppServiceCatalogAndCustomerMapping, String> {
	
	public AppServiceCatalogAndCustomerMapping findTop1ByServiceId(String serviceId);
	
	public List<AppServiceCatalogAndCustomerMapping> findAllByServiceIdIn(List<String> serviceIds);

}
