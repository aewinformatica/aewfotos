package com.aewinformatica.aewfotos.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.util.StringUtils;

import com.aewinformatica.aewfotos.repository.listener.FotoEntityListener;



@EntityListeners(FotoEntityListener.class)
@Entity
@Table(name="cliente_foto")
public class Foto {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long codigo;
	
	public String nome;
	
	@OneToOne
	@JoinColumn(name = "codigo_cliente")
	private Cliente cliente;
		
	@Column(name = "content_type")
	private String contentType;
	
	@Transient
	private boolean novaFoto;

	@Transient
	private String urlFoto;

	@Transient
	private String urlThumbnailFoto;
	
	
	public String getFotoOuMock() {
		return !StringUtils.isEmpty(nome) ? nome : "mock-cliente.png";
	}

	public boolean temFoto() {
		return !StringUtils.isEmpty(this.nome);
	}

	
	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public boolean isNovaFoto() {
		return novaFoto;
	}

	public void setNovaFoto(boolean novaFoto) {
		this.novaFoto = novaFoto;
	}

	public String getUrlFoto() {
		return urlFoto;
	}

	public void setUrlFoto(String urlFoto) {
		this.urlFoto = urlFoto;
	}

	public String getUrlThumbnailFoto() {
		return urlThumbnailFoto;
	}
	
	

	public void setUrlThumbnailFoto(String urlThumbnailFoto) {
		this.urlThumbnailFoto = urlThumbnailFoto;
	}
	
	public String getFotoPorCodigo(Long codigo) {
		
		return this.nome;
	}
	

}
