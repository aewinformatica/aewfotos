package com.aewinformatica.aewfotos.service;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aewinformatica.aewfotos.model.Cliente;
import com.aewinformatica.aewfotos.model.Foto;
import com.aewinformatica.aewfotos.repository.Clientes;
import com.aewinformatica.aewfotos.repository.Fotos;
import com.aewinformatica.aewfotos.service.exception.ImpossivelExcluirEntidadeException;
import com.aewinformatica.aewfotos.storage.FotoStorage;

@Service
public class CadastroClienteService {
	
	@Autowired
	Clientes clientes;
	
	@Autowired
	Fotos fotos;
	
	@Autowired
	private FotoStorage fotoStorage;
	
	public void salvar(Cliente cliente){
		
//		cliente.foto.setNome(fotoDTO.getNome());
		
		clientes.save(cliente);
		
		//verificar se existe
	
//		Foto fotoCliente = fotos.getOne(cliente.getCodigo());
		
		Foto fotoCliente = cliente.getFoto();
			 fotoCliente.setCliente(cliente);
			 fotoCliente.setContentType(cliente.getFoto().getContentType());
			 fotoCliente.setNome(cliente.getFoto().getNome());
		
		fotos.save(fotoCliente);
		
	}
	
	public void excluir(Cliente cliente){
	
		Foto foto = fotos.getOne(cliente.getCodigo());
		
		try{
			String nomeFoto = foto.getNome();
			clientes.delete(cliente);
			clientes.flush();
			fotoStorage.excluir(nomeFoto);
			
		}catch(PersistenceException e){
			
			throw new ImpossivelExcluirEntidadeException("Impossível apagar o cliente. Já foi usado em alguma operaçao.");
		}
	}
		
}
