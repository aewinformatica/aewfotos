package com.aewinformatica.aewfotos.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.aewinformatica.aewfotos.model.Arquivo;
import com.aewinformatica.aewfotos.repository.Arquivos;


@Controller
@RequestMapping("/upload")
public class UploadController {

	 private static final Logger log = LoggerFactory.getLogger(UploadController.class);
	 	
	 	@Autowired
	 	private Arquivos arquivos;
	 
	    @Value("${file.upload.directory}")
	    private String fileUploadDirectory;
	 
	@PostMapping
    public @ResponseBody Map<String, Object> upload(MultipartHttpServletRequest request, HttpServletResponse response, Arquivo mArquivo) {
        log.debug("uploadPost called");
        Iterator<String> itr = request.getFileNames();
        MultipartFile mpf;
        List<Arquivo> list = new LinkedList<>();
        
        while (itr.hasNext()) {
            mpf = request.getFile(itr.next());
            log.debug("Uploading {}", mpf.getOriginalFilename());
            
            String newFilenameBase = UUID.randomUUID().toString();
            String originalFileExtension = mpf.getOriginalFilename().substring(mpf.getOriginalFilename().lastIndexOf("."));
            String newFilename = newFilenameBase + originalFileExtension;
            String storageDirectory = fileUploadDirectory;
            String contentType = mpf.getContentType();
            
            File newFile = new File(storageDirectory + "/" + newFilename);
            try {
                mpf.transferTo(newFile);
                
                Arquivo arquivo = new Arquivo();
                arquivo.setName(mpf.getOriginalFilename());
                arquivo.setTitulo(mArquivo.getTitulo());
                arquivo.setNewFilename(newFilename);
                arquivo.setContentType(contentType);
                arquivo.setSize(mpf.getSize());
                
                if(contentType.contains("image/")){
                	
                    BufferedImage thumbnail = Scalr.resize(ImageIO.read(newFile), 290);
                    String thumbnailFilename = newFilenameBase + "-thumbnail.png";
                    File thumbnailFile = new File(storageDirectory + "/" + thumbnailFilename);
                    ImageIO.write(thumbnail, "png", thumbnailFile);
                    
                    arquivo.setThumbnailFilename(thumbnailFilename);
                    arquivo.setThumbnailSize(thumbnailFile.length());
                    arquivo.setUrl("/picture/"+arquivo.getId());
                    arquivo.setThumbnailUrl("/thumbnail/"+arquivo.getId());
                    
                	System.out.println("é uma foto");
                }
                
                if(contentType.contains("video/")){
                arquivo.setUrl("/movie/"+arquivo.getId());
                arquivo.setMovieUrl("/movie/"+arquivo.getId());
                System.out.println("é um video");
                
                }
                
                if(contentType.contains("audio/")){
                arquivo.setUrl("/music/"+arquivo.getId());
                arquivo.setMovieUrl("/music/"+arquivo.getId());
                System.out.println("é um audio");
                
                }
                

                arquivo.setDeleteUrl("/delete/"+arquivo.getId());
                arquivo.setDeleteType("DELETE");
                
                arquivo = Criar(arquivo);
                
                list.add(arquivo);
                
            } catch(IOException e) {
                log.error("Could not upload file "+mpf.getOriginalFilename(), e);
            }
            
        }
        
        Map<String, Object> files = new HashMap<>();
        files.put("files", list);
        return files;
    }
	

	@GetMapping
    public @ResponseBody Map<String, Object> list() {
        log.debug("uploadGet called");
        List<Arquivo> list =	 arquivos.findAll();
        for(Arquivo arquivo : list) {
        	
        	if(arquivo.getContentType().contains("video/")) {
            arquivo.setUrl("/upload/movie/"+arquivo.getId());
            arquivo.setMovieUrl("/upload/movie/"+arquivo.getId());
            System.out.println(arquivo.getContentType());
        	}
        	
        	if(arquivo.getContentType().contains("image/")) {
        	arquivo.setUrl("/upload/picture/"+arquivo.getId());
            arquivo.setThumbnailUrl("/upload/thumbnail/"+arquivo.getId());
            }
        	
        	if(arquivo.getContentType().contains("audio/")) {
        		arquivo.setUrl("/upload/music/"+arquivo.getId());
                arquivo.setMovieUrl("/upload/music/"+arquivo.getId());
        	}
        	
            arquivo.setDeleteUrl("/upload/delete/"+arquivo.getId());
            arquivo.setDeleteType("DELETE");
        }
        Map<String, Object> files = new HashMap<>();
        files.put("files", list);
        log.debug("Returning: {}", files);
        return files;
    }
	
    @RequestMapping(value = "/picture/{id}", method = RequestMethod.GET)
    public void picture(HttpServletResponse response, @PathVariable Long id) {

        Arquivo arquivo = PesquisaPorCodigo(id);
        File arquivoFile = new File(fileUploadDirectory+"/"+arquivo.getNewFilename());
        response.setContentType(arquivo.getContentType());
        response.setContentLength(arquivo.getSize().intValue());
        try {
            InputStream is = new FileInputStream(arquivoFile);
            IOUtils.copy(is, response.getOutputStream());
        } catch(IOException e) {
            log.error("Could not show picture "+id, e);
            e.printStackTrace();
        }
    }
    
    @RequestMapping(value = "/movie/{id}", method = RequestMethod.GET)
    public void movie(HttpServletResponse response, @PathVariable Long id) {

        Arquivo arquivo = PesquisaPorCodigo(id);
        File arquivoFile = new File(fileUploadDirectory+"/"+arquivo.getNewFilename());
        response.setContentType(arquivo.getContentType());
        response.setContentLength(arquivo.getSize().intValue());
        try {
            InputStream is = new FileInputStream(arquivoFile);
            IOUtils.copy(is, response.getOutputStream());
        } catch(IOException e) {
            log.error("Could not show movie "+id, e.getMessage());
            e.printStackTrace();
        }
    }
    
    @RequestMapping(value = "/music/{id}", method = RequestMethod.GET)
    public void music(HttpServletResponse response, @PathVariable Long id) {

        Arquivo arquivo = PesquisaPorCodigo(id);
        File arquivoFile = new File(fileUploadDirectory+"/"+arquivo.getNewFilename());
        response.setContentType(arquivo.getContentType());
        response.setContentLength(arquivo.getSize().intValue());
        try {
            InputStream is = new FileInputStream(arquivoFile);
            IOUtils.copy(is, response.getOutputStream());
        } catch(IOException e) {
            log.error("Could not show music "+id, e);
        }
    }
    
    @RequestMapping(value = "/thumbnail/{id}", method = RequestMethod.GET)
    public void thumbnail(HttpServletResponse response, @PathVariable Long id) {

        Arquivo arquivo = PesquisaPorCodigo(id);
        File arquivoFile = new File(fileUploadDirectory+"/"+arquivo.getThumbnailFilename());
        response.setContentType(arquivo.getContentType());
        if(arquivo.getThumbnailSize()!=null)
        response.setContentLength(arquivo.getThumbnailSize().intValue());
    		
        try {
            InputStream is = new FileInputStream(arquivoFile);
            IOUtils.copy(is, response.getOutputStream());
        } catch(IOException e) {
            log.error("Could not show thumbnail "+id, e);
        }
    }
    
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public @ResponseBody List<Map<String, Object>> delete(@PathVariable Long id) {
        Arquivo arquivo = PesquisaPorCodigo(id);
        File arquivoFile = new File(fileUploadDirectory+"/"+arquivo.getNewFilename());
        arquivoFile.delete();
        
        if(arquivo.getContentType().contains("image/")) {
        File thumbnailFile = new File(fileUploadDirectory+"/"+arquivo.getThumbnailFilename());
        thumbnailFile.delete();
        }
        
        arquivos.delete(arquivo);
        
        List<Map<String, Object>> results = new ArrayList<>();
        Map<String, Object> success = new HashMap<>();
        success.put("success", true);
        results.add(success);
        return results;
    }
    
	public Arquivo PesquisaPorCodigo(Long id) {
		Optional<Arquivo>imageOpt = arquivos.findById(id);
		
		if(imageOpt.isPresent()) {
		return imageOpt.get();
		}
		
		return null;
	}
	public Arquivo Criar(Arquivo image) {

		arquivos.save(image);
		return image;
	}
}
