package com.aewinformatica.aewfotos.service;

import java.util.List;
import java.util.Optional;

import javax.persistence.PersistenceException;
import javax.transaction.Transactional;

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

	public void salvar(Cliente cliente) {

		Cliente clienteSalvo = clientes.save(cliente);

		Foto fotoCliente = new Foto();
			 fotoCliente = cliente.getFoto();
		/*
		fotoCliente.setCodigo(cliente.getFoto().getCodigo());
		fotoCliente.setContentType(cliente.getFoto().getContentType());
		fotoCliente.setNome(cliente.getFoto().getNome());*/
		fotoCliente.setCliente(clienteSalvo);
		
		//if (!fotoCliente.getNome().isEmpty())
		//	fotos.save(fotoCliente);

	}

	@Transactional
	public void excluirEmMassa(Long[] codigos) {
//	public void excluirEmMassa(List<Long> codigos) {

		List<Cliente> listaClientes = clientes.findByCodigoIn(codigos);

		for (Cliente c : listaClientes) {
			excluir(c);
		}
	}

	public void excluir(Cliente cliente) {

		Foto foto = fotos.getOne(cliente.getCodigo());

		try {
			String nomeFoto = foto.getNome();
			clientes.delete(cliente);
			clientes.flush();
			fotos.deleteById(cliente.getCodigo());
			fotoStorage.excluir(nomeFoto);

		} catch (PersistenceException e) {

			throw new ImpossivelExcluirEntidadeException(
					"Impossível apagar o cliente. Já foi usado em alguma operaçao.");
		}
	}

	@Transactional
	public void excluirFoto(Cliente cliente) {
		Foto foto = fotos.findByCliente(cliente).get();

		System.out.println("FOTO" + foto.getNome());
		try {
			String nomeFoto = foto.getNome();
			fotos.deleteById(foto.getCodigo());
			fotoStorage.excluir(nomeFoto);
		} catch (PersistenceException e) {

			throw new ImpossivelExcluirEntidadeException("Impossível apagar a Foto. Já foi usado em alguma operaçao.");
		}
	}

}
