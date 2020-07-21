package com.aewinformatica.aewfotos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aewinformatica.aewfotos.model.Cliente;

@Repository
public interface Clientes extends JpaRepository<Cliente,Long> {

	public List<Cliente> findByCodigoIn(List<Long> codigos);
	public List<Cliente> findByCodigoIn(Long[] codigos);
}
