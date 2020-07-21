package com.aewinformatica.aewfotos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aewinformatica.aewfotos.model.Cliente;
import com.aewinformatica.aewfotos.model.Foto;

@Repository
public interface Fotos extends JpaRepository<Foto, Long> {
	
	public Foto findByCliente(Cliente cliente);
	public Optional<Foto> findByClienteCodigo(Cliente cliente);

	

}
