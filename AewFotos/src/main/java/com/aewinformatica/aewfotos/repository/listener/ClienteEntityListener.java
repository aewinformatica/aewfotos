package com.aewinformatica.aewfotos.repository.listener;

import javax.persistence.PostLoad;

import com.aewinformatica.aewfotos.AewFotosApplication;
import com.aewinformatica.aewfotos.model.Cliente;
import com.aewinformatica.aewfotos.storage.FotoStorage;



public class ClienteEntityListener {

	@PostLoad
	public void postLoad(final Cliente cliente) {
		FotoStorage fotoStorage = AewFotosApplication.getBean(FotoStorage.class);

		
		cliente.setUrlFoto(fotoStorage.getUrl(cliente.getFotoOuMock()));
		cliente.setUrlThumbnailFoto(fotoStorage.getUrl(FotoStorage.THUMBNAIL_PREFIX + cliente.getFotoOuMock()));
		
	}
	

	
}
