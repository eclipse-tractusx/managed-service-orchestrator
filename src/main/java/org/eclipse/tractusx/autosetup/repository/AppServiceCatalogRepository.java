package org.eclipse.tractusx.autosetup.repository;

import java.util.Optional;

import org.eclipse.tractusx.autosetup.entity.AppServiceCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AppServiceCatalogRepository extends JpaRepository<AppServiceCatalog, String> {

	@Query(value = "SELECT * FROM app_service_catalog_tbl a WHERE a.canonical_service_id = ?1", nativeQuery = true)
	Optional<AppServiceCatalog> findByCanonicalServiceId(String appServiceCatalogId);

}
