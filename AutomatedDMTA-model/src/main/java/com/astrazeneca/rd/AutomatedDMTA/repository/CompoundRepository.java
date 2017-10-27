package com.astrazeneca.rd.AutomatedDMTA.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.astrazeneca.rd.AutomatedDMTA.model.Compound;
import com.astrazeneca.rd.AutomatedDMTA.model.StageType;

//TODO: Manu: Do we need more attributes?, ie: SMILE?
public interface CompoundRepository extends JpaRepository<Compound, Long> {
	
	//TODO: Manu: confirm stage datatype is correct (ie: no String)
	public List<Compound> findByCompoundIdAndStage(String compoundId, StageType stage);
	
	@Query("select c from Compound c where c.compoundId = :compoundId or c.stage = :stage")
	public List<Compound> findByCompoundIdOrStage(
			@Param("compoundId") String compoundId,
			@Param("stage") StageType stage);
	
	public Page<Compound> findAll(Pageable pageable);
}
