package com.aewinformatica.aewfotos.storage.local;

import static java.nio.file.FileSystems.getDefault;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.aewinformatica.aewfotos.storage.FotoStorage;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;


@Profile("local")
@Component
public class FotoStorageLocal implements FotoStorage {
	
	
	
	private static final Logger logger = LoggerFactory.getLogger(FotoStorageLocal.class);
	private static final String THUMBNAIL_PREFIX = "thumbnail.";

	
	@Value("${sistema.foto-storage-local.local}")
	private Path local;
	private Path mockPath = Paths.get(System.getProperty("user.dir")+ "\\src\\main\\resources\\static\\images");

	
	
//	@Value("${sistema.foto-storage-local.url-base}")
	private String urlBase;
	
	@Value("${server.port}")
	private String port;
	


	
	
	@Override
	public String salvar(MultipartFile[] files) {
		String novoNome = null;
		if (files != null && files.length > 0) {
			MultipartFile arquivo = files[0];
			novoNome = renomearArquivo(arquivo.getOriginalFilename());
			try {
				arquivo.transferTo(new File(this.local.toAbsolutePath().toString() + getDefault().getSeparator() + novoNome));
			} catch (IOException e) {
				throw new RuntimeException("Erro salvando a foto", e);
			}
		}
		
		try {
			Thumbnails.of(this.local.resolve(novoNome).toString()).size(40, 68).toFiles(Rename.PREFIX_DOT_THUMBNAIL);
		} catch (IOException e) {
			throw new RuntimeException("Erro gerando thumbnail", e);
		}
		
		return novoNome;
	}
	
	@Override
	public byte[] recuperar(String nome) {
		try {
			return Files.readAllBytes(this.local.resolve(nome));
		} catch (IOException e) {
			throw new RuntimeException("Erro lendo a foto", e);
		}
	}
	
	@Override
	public byte[] recuperarThumbnail(String foto) {
		return recuperar(THUMBNAIL_PREFIX + foto);
	}
	
	@Override
	public void excluir(String foto) {
		try {
			Files.deleteIfExists(this.local.resolve(foto));
			Files.deleteIfExists(this.local.resolve(THUMBNAIL_PREFIX + foto));
		} catch (IOException e) {
			logger.warn(String.format("Erro apagando foto '%s'. Mensagem: %s", foto, e.getMessage()));
		}
		
	}
	
	@Override
	public String getUrl(String foto) {
		try {

			urlBase  = "http://" + InetAddress.getLocalHost().getHostAddress() +":"+ port+"/fotos/";
			System.out.println(urlBase.toString());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return urlBase + foto;
	}

	@PostConstruct
	private void criarPastas() {
		String mockNome = "mock-cliente.png";
		
		try {			
			
			
			if(!Files.exists(this.local)) {
			
				Files.createDirectories(this.local);
				Files.copy(this.mockPath.resolve(mockNome),this.local.resolve(mockNome));
			
			try {
				Thumbnails.of(this.local.resolve(mockNome).toString()).size(40, 68).toFiles(Rename.PREFIX_DOT_THUMBNAIL);
			} catch (IOException e) {
				throw new RuntimeException("Erro gerando thumbnail", e);
			}
			
			}
			

			 
			
			if (logger.isDebugEnabled()) {
				logger.debug("Pastas criadas para salvar fotos.");
				logger.debug("Pasta default: " + this.local.toAbsolutePath());
			}
		} catch (IOException e) {
			throw new RuntimeException("Erro criando pasta para salvar foto", e);
		}
	}

}
