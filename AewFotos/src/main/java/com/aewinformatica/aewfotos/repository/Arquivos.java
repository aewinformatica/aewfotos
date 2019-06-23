package com.aewinformatica.aewfotos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aewinformatica.aewfotos.model.Arquivo;

@Repository
public interface Arquivos extends JpaRepository<Arquivo, Long>{

}
