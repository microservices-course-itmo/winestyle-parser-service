package com.wine.to.up.winestyle.parser.service.repository;

import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlcoholRepository extends JpaRepository<Alcohol, Long>, JpaSpecificationExecutor<Alcohol> {
    @Query(value = "SELECT * FROM alcohol WHERE type SIMILAR TO '(Ш|И)%'", nativeQuery = true)
    List<Alcohol> findAllSparkling();
    @Query(value = "SELECT * FROM alcohol WHERE type NOT SIMILAR TO '(Ш|И)%'", nativeQuery = true)
    List<Alcohol> findAllWines();
    Optional<Alcohol> findByUrl(String url);
}