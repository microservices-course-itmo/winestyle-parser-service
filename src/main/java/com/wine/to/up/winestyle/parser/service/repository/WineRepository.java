package com.wine.to.up.winestyle.parser.service.repository;

import com.wine.to.up.winestyle.parser.service.domain.entity.Wine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface WineRepository extends JpaRepository<Wine, Long>, JpaSpecificationExecutor<Wine> {
    Wine findByName(String name);
    Wine findByUrl(String url);
}