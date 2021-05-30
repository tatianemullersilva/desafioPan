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

import br.com.pan.model.Endereco;
import br.com.pan.repository.EnderecoRepository;

@RestController
@RequestMapping("/endereco")
@CrossOrigin(origins = "*")
public class EnderecoController {

	@Autowired
	private EnderecoRepository enderecoRepository;

//	private ValidaCPF validaCPF;

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Collection<Endereco>> buscarTodos() {
		return new ResponseEntity<Collection<Endereco>>(this.enderecoRepository.findAll(), HttpStatus.OK);
	}

	@RequestMapping(value = "/cep/{cep}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Endereco> buscarCep(@PathVariable String cep) {
		HttpStatus status = HttpStatus.OK;
		cep = cep.length() == 8 ? cep.substring(0, 5) + '-' + cep.substring(5, 8) : cep;
		Endereco result = this.enderecoRepository.findByCep(cep);
		if (result == null) {
			result = this.buscarViaCep(cep);
			if (result == null) {
				status = HttpStatus.NOT_FOUND;
			}
			else {
			result = this.enderecoRepository.save(result);
			}
		}
		return new ResponseEntity<Endereco>(result, status);
	}

	public Endereco buscarViaCep(String cep) {
		Gson gson = new Gson();
		try {
			URL url = new URL("http://viacep.com.br/ws/" + cep + "/json");
			URLConnection urlConnection = url.openConnection();
			InputStream is = urlConnection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			Endereco end = gson.fromJson(br, Endereco.class);
			// Endereco aux= this.enderecoRepository.save(end);
			return end;
		} catch (Exception e) {
			return null;
		}
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Endereco> cadastrar(@RequestBody Endereco enderecoRequest) {

		Endereco endereco = this.enderecoRepository.save(enderecoRequest);
		HttpStatus status = endereco == null ? HttpStatus.BAD_REQUEST : HttpStatus.OK;
		return new ResponseEntity<Endereco>(endereco, status);
	}

	@RequestMapping(method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Endereco> atualizar(@RequestBody Endereco enderecoRequest) {
		Endereco endereco = this.enderecoRepository.save(enderecoRequest);
		HttpStatus status = endereco == null ? HttpStatus.BAD_REQUEST : HttpStatus.OK;
		return new ResponseEntity<Endereco>(endereco, status);
	}

}
