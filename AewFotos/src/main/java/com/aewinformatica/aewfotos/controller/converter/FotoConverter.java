package com.aewinformatica.aewfotos.controller.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.aewinformatica.aewfotos.model.Foto;

@Component
public class FotoConverter implements Converter<String,Foto> {

	@Override
	public Foto convert(String codigo) {
		
		if (!StringUtils.isEmpty(codigo)) {
			Foto foto = new Foto();
			foto.setCodigo(Long.valueOf(codigo));
			return foto;
		}
		
		return null;
	}

}
