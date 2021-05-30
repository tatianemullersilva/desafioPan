package br.com.pan.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import br.com.pan.model.Estado;
import br.com.pan.repository.EstadoRepository;

@RestController
@RequestMapping("/estado")
@CrossOrigin(origins = "*")
public class EstadoController {

	@Autowired
	private EstadoRepository estadoRepository;
	
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Collection<Estado>> buscarTodos() {
		return new ResponseEntity<Collection<Estado>>(this.estadoRepository.findAll(), HttpStatus.OK);
	}

	@RequestMapping(value = "/nome/{nome}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Estado> buscarNome(@PathVariable String nome) {
		HttpStatus status = HttpStatus.OK;
		Estado result = this.estadoRepository.findByNome(nome);
		if (result == null) {
			result = this.buscarViaNome(nome);
			if (result == null) {
				status = HttpStatus.NOT_FOUND;
			}
			else {
			result = this.estadoRepository.save(result);
			}
		}
		return new ResponseEntity<Estado>(result, status);
	}

	public Estado buscarViaNome(String nome) {
		Gson gson = new Gson();
		try {
			URL url = new URL("http://servicodados.ibge.gov.br/api/v1/localidades/estados/ " + nome + "/json");
			URLConnection urlConnection = url.openConnection();
			InputStream is = urlConnection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			Estado estado = gson.fromJson(br, Estado.class);
			return estado;
		} catch (Exception e) {
			return null;
		}
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Estado> cadastrar(@RequestBody Estado estadoRequest) {
		Estado estado = this.estadoRepository.save(estadoRequest);
		HttpStatus status = estado == null ? HttpStatus.BAD_REQUEST : HttpStatus.OK;
		return new ResponseEntity<Estado>(estado, status);
	}

	@RequestMapping(method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Estado> atualizar(@RequestBody Estado enderecoRequest) {
		Estado estado = this.estadoRepository.save(enderecoRequest);
		HttpStatus status = estado == null ? HttpStatus.BAD_REQUEST : HttpStatus.OK;
		return new ResponseEntity<Estado>(estado, status);
	}

}
