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

//TODO: May need to refuctor if SMILE is a default attribute
public interface CompoundRepository extends JpaRepository<Compound, Long> {
	
	@Query("select c from Compound c where c.compoundId = :compoundId")
	public List<Compound> findByCompoundId(@Param("compoundId") String compoundId);
	
/*	In case you need more than one attribs (ie: also SMILE)
  @Query("select c from Compound c where c.compoundId = :compoundId or c.smile = :smile")
	public List<Compound> findByCompoundIdOrSmile(@Param("compoundId") String compoundId, @Param("smile") String smile);
*/
	
	public Page<Compound> findAll(Pageable pageable);
}
