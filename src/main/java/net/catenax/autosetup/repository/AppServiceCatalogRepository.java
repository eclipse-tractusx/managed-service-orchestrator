package net.catenax.autosetup.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.catenax.autosetup.entity.AppServiceCatalog;

public interface AppServiceCatalogRepository extends JpaRepository<AppServiceCatalog, String> {

}
