var cadCli = cadCli || {};

cadCli.UploadFoto = (function() {
	
	function UploadFoto() {
		this.inputNomeFoto = $('input[name="foto.nome"]');
		this.inputContentType = $('input[name="foto.contentType"]');
		this.novaFoto = $('input[name="foto.novaFoto"]');
		this.inputUrlFoto = $('input[name="foto.urlFoto"]');
		
		this.htmlFotoClienteTemplate = $('#foto-cliente').html();
		this.template = Handlebars.compile(this.htmlFotoClienteTemplate);
		
		this.containerFotoCliente = $('.js-container-foto-cliente');
		
		this.uploadDrop = $('#upload-drop');
		this.imgLoading = $('.js-img-loading');
	}
	
	UploadFoto.prototype.iniciar = function () {
		var settings = {
			type: 'json',
			filelimit: 1,
			allow: '*.(jpg|jpeg|png)',
			action: this.containerFotoCliente.data('url-fotos'),
			complete: onUploadCompleto.bind(this),
			beforeSend: adicionarCsrfToken,
			loadstart: onLoadStart.bind(this)
		}
		
		UIkit.uploadSelect($('#upload-select'), settings);
		UIkit.uploadDrop(this.uploadDrop, settings);
		
		if (this.inputNomeFoto.val()) {
			renderizarFoto.call(this, { 
				nome:  this.inputNomeFoto.val(), 
				contentType: this.inputContentType.val(), 
				url: this.inputUrlFoto.val()});
		}
	}
	
	function onLoadStart() {
		this.imgLoading.removeClass('hidden');
	}
	
	function onUploadCompleto(resposta) {
		this.novaFoto.val('true');
		this.inputUrlFoto.val(resposta.url);
		this.imgLoading.addClass('hidden');
		renderizarFoto.call(this, resposta);
	}
	
	function renderizarFoto(resposta) {
		this.inputNomeFoto.val(resposta.nome);
		this.inputContentType.val(resposta.contentType);
		
		this.uploadDrop.addClass('hidden');
		
		var htmlFotoCliente = this.template({url: resposta.url});
		this.containerFotoCliente.append(htmlFotoCliente);
		
		$('.js-remove-foto').on('click',onRemoverFoto.bind(this)
		);
	}
	
	function onRemoverFoto() {
			var deletafoto = '/fotos/delete/' + this.inputNomeFoto.val();
			$.get(deletafoto,
				 function(){
				  console.log("foto deletada com sucesso",deletafoto);
				 }
			);
		
		
		
		$('.js-foto-cliente').remove();
		this.uploadDrop.removeClass('hidden');
		this.inputNomeFoto.val('');
		this.inputContentType.val('');
		this.novaFoto.val('false');
	}
	
	function adicionarCsrfToken(xhr) {
		var token = $('input[name=_csrf]').val();
		var header = $('input[name=_csrf_header]').val();
		xhr.setRequestHeader(header, token);
	}
	
	return UploadFoto;
	
})();

$(function() {
	var uploadFoto = new cadCli.UploadFoto();
	uploadFoto.iniciar();
});