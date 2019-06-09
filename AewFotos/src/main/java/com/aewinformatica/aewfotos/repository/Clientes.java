package com.aewinformatica.aewfotos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aewinformatica.aewfotos.model.Cliente;

@Repository
public interface Clientes extends JpaRepository<Cliente,Long> {

}
