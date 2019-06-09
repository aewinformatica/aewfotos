package com.aewinformatica.aewfotos.repository.listener;

import javax.persistence.PostLoad;

import com.aewinformatica.aewfotos.AewFotosApplication;
import com.aewinformatica.aewfotos.model.Foto;
import com.aewinformatica.aewfotos.storage.FotoStorage;

public class FotoEntityListener {

	
	@PostLoad
	public void postLoadtwo(final Foto foto) {
		FotoStorage fotoStorage = AewFotosApplication.getBean(FotoStorage.class);
		
		foto.setUrlFoto(fotoStorage.getUrl(foto.getFotoOuMock()));
		foto.setUrlThumbnailFoto(fotoStorage.getUrl(FotoStorage.THUMBNAIL_PREFIX + foto.getFotoOuMock()));
	}
}
