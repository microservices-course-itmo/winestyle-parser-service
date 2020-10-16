package com.wine.to.up.winestyle.parser.service.repository;

import com.wine.to.up.winestyle.parser.service.domain.entity.Sparkling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SparklingRepository extends JpaRepository<Sparkling, Long>, JpaSpecificationExecutor<Sparkling> {
    Sparkling findByUrl(String url);
}
