package org.eclipse.tractusx.autosetup.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.eclipse.tractusx.autosetup.entity.AppServiceCatalog;

public interface AppServiceCatalogRepository extends JpaRepository<AppServiceCatalog, String> {

	public AppServiceCatalog findTop1ByRefServiceId(String serviceId);

}
