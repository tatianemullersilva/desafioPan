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
import br.com.pan.model.Municipio;
import br.com.pan.repository.MunicipioRepository;

@RestController
@RequestMapping("/municipio")
@CrossOrigin(origins = "*")
public class MunicipioController {

	@Autowired
	private MunicipioRepository municipioRepository;
	
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Collection<Municipio>> buscarTodos() {
		return new ResponseEntity<Collection<Municipio>>(this.municipioRepository.findAll(), HttpStatus.OK);
	}

	@RequestMapping(value = "/id/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Municipio> buscarId(@PathVariable Integer id) {
		HttpStatus status = HttpStatus.OK;
		Municipio result = this.municipioRepository.findById(id).get();
		if (result == null) {
			result = this.buscarViaId(id);
			if (result == null) {
				status = HttpStatus.NOT_FOUND;
			}
			else {
			result = this.municipioRepository.save(result);
			}
		}
		return new ResponseEntity<Municipio>(result, status);
	}

	public Municipio buscarViaId(Integer id) {
		Gson gson = new Gson();
		try {
			URL url = new URL("http://servicodados.ibge.gov.br/api/v1/localidades/estados/ " + id + "/json");
			URLConnection urlConnection = url.openConnection();
			InputStream is = urlConnection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			Municipio municipio = gson.fromJson(br, Municipio.class);
			return municipio;
		} catch (Exception e) {
			return null;
		}
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Municipio> cadastrar(@RequestBody Municipio municipioRequest) {
		Municipio municipio = this.municipioRepository.save(municipioRequest);
		HttpStatus status = municipio == null ? HttpStatus.BAD_REQUEST : HttpStatus.OK;
		return new ResponseEntity<Municipio>(municipio, status);
	}

	@RequestMapping(method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Municipio> atualizar(@RequestBody Municipio municipioRequest) {
		Municipio municipio = this.municipioRepository.save(municipioRequest);
		HttpStatus status = municipio == null ? HttpStatus.BAD_REQUEST : HttpStatus.OK;
		return new ResponseEntity<Municipio>(municipio, status);
	}

}
