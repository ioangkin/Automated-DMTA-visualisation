package com.astrazeneca.rd.AutomatedDMTA.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.astrazeneca.rd.AutomatedDMTA.model.Compound;

/**
 * @author klfl423
 * 
 * Compound model class
 *
 */

public interface CompoundRepository extends JpaRepository<Compound, Long> {
	
	@Query("select c from Compound c where c.serialNumber = :serialNumber")
	public List<Compound> findBySerialNumber(@Param("serialNumber") String serialNumber);
	
	public Page<Compound> findAll(Pageable pageable);
}
