package com.wine.to.up.winestyle.parser.service.repository;

import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlcoholRepository extends JpaRepository<Alcohol, Long>, JpaSpecificationExecutor<Alcohol> {
    Alcohol findByName(String name);
    List<Alcohol> findAllByType(String type);
    List<Alcohol> findAllByTypeIn(List<String> types);
    Alcohol findByUrl(String url);
}