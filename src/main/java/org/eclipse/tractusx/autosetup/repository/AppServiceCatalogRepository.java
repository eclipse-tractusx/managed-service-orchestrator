package org.eclipse.tractusx.autosetup.repository;

import java.util.Optional;

import org.eclipse.tractusx.autosetup.entity.AppServiceCatalog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppServiceCatalogRepository extends JpaRepository<AppServiceCatalog, String> {

	Optional<AppServiceCatalog> findByCanonicalServiceId(String appServiceCatalogId);

}
