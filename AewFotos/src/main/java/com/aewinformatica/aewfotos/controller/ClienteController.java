package com.aewinformatica.aewfotos.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.aewinformatica.aewfotos.model.Arquivo;
import com.aewinformatica.aewfotos.model.Cliente;
import com.aewinformatica.aewfotos.model.Foto;
import com.aewinformatica.aewfotos.repository.Clientes;
import com.aewinformatica.aewfotos.repository.Fotos;
import com.aewinformatica.aewfotos.service.CadastroClienteService;
import com.aewinformatica.aewfotos.service.exception.ImpossivelExcluirEntidadeException;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

	@Autowired
	private CadastroClienteService cadastroClienteService;

	// temporario ate fazer o filtro de busca
	@Autowired
	private Clientes clientes;

	@Autowired
	private Fotos fotos;

	@RequestMapping(value = "/novo")
	public ModelAndView novo(Cliente cliente) {
		ModelAndView mv = new ModelAndView("cliente/CadastroCliente");
		List<Foto> listaFotos = fotos.findAll();
		mv.addObject("todasfotos", listaFotos);
		return mv;
	}

	@RequestMapping(value = { "/novo", "{\\d+}" }, method = RequestMethod.POST)
	public ModelAndView salvar(@Valid Cliente cliente, BindingResult result, Model model,
			RedirectAttributes attributes) {
		if (result.hasErrors()) {
			System.out.println("DEU RUIMM ASPIRA");
			System.out.println(cliente.getIsEmpresa());
			return novo(cliente);
		}
		
		Optional<Foto> fotoEncontrada = fotos.findByCliente(cliente);
		if (fotoEncontrada.isPresent()) 
			cliente.setFoto(fotoEncontrada.get());
		
		
		cadastroClienteService.salvar(cliente);
		attributes.addFlashAttribute("mensagem", "Cliente salvo com sucesso!");
		return new ModelAndView("redirect:/clientes/novo");
	}
	
	@GetMapping
	public ModelAndView pesquisar(Cliente cliente) {
		ModelAndView mv = new ModelAndView("cliente/PesquisaClientes");

		List<Cliente> clientesComFoto = new ArrayList<Cliente>();

		for (Cliente c : clientes.findAll()) {
//			Foto f = new Foto();
			Optional<Foto> f = fotos.findByCliente(c);
			if (f.isPresent())
				c.setFoto(f.get());

			// quando exclui a foto da NULL pointer
			// System.out.println("CLIENTE" + c.getFoto().getUrlThumbnailFoto());
			clientesComFoto.add(c);
		}

		mv.addObject("clientes", clientesComFoto);

		return mv;
	}

//	@GetMapping
//	public ModelAndView pesquisar(Cliente cliente, Foto foto) {
//		ModelAndView mv = new ModelAndView("cliente/PesquisaClientes");
//
//		List<Cliente> listaClientes = clientes.findAll();
//		List<Foto> listaFotos = fotos.findAll();
//
//		mv.addObject("todasfotos", listaFotos);
//
//		for (int i = 0; listaFotos.size() > i; i++) {
//			listaFotos.get(i).getUrlThumbnailFoto();
//			listaClientes.get(i).setUrlThumbnailFoto(listaFotos.get(i).getUrlThumbnailFoto());
//
//		}
//
//		mv.addObject("todosclientes", listaClientes);
//
//		return mv;
//	}

	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Cliente cliente) {
		try {
			cadastroClienteService.excluir(cliente);
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/{codigo}/foto")
	public @ResponseBody ResponseEntity<?> excluirFoto(@PathVariable("codigo") Cliente cliente) {
		try {
			cadastroClienteService.excluirFoto(cliente);
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable("codigo") Cliente cliente) {
		
		
		Optional<Foto> foto = fotos.findByCliente(cliente);
		if(foto.isPresent()  && !foto.get().nome.isEmpty())
		cliente.setFoto(foto.get());
		
		ModelAndView mv = novo(cliente);
		mv.addObject(cliente);
		return mv;
	}

	@GetMapping("/{codigo}/arquivos")
	public ModelAndView arquivos(@PathVariable("codigo") Cliente cliente, Arquivo mArquivo) {
		ModelAndView mv = new ModelAndView("cliente/Upload");
		mv.addObject(cliente);

		mv.addObject(mArquivo);
		return mv;
	}

	@GetMapping("/cliente")
	public ModelAndView excluirEmMassa(@RequestParam("codigo") Long[] codigos) {
//		public @ResponseBody ResponseEntity<?> excluirEmMassa(@RequestParam("codigo") List<Long> codigos) {
		try {
//			codigos.forEach(
//			l->{System.out.println("CODIGO: " + l);}
//			);
			cadastroClienteService.excluirEmMassa(codigos);
			/*
			 * public void excluirEmMassa(Long[] codigos, Clientes clientes) {
			 * usuarios.findByCodigoIn(codigos).forEach( u->usuarios.delete(u.getCodigo()));
			 * }
			 */
		} catch (ImpossivelExcluirEntidadeException e) {

		}
//		return ResponseEntity.ok().build();
		return new ModelAndView("redirect:/clientes");
	}

}
