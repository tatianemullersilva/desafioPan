package br.com.pan.controller;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

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

import br.com.pan.model.Endereco;
import br.com.pan.model.Usuario;
import br.com.pan.repository.EnderecoRepository;
import br.com.pan.repository.UsuarioRepository;
import br.com.pan.util.ValidaCPF;

@RestController
@RequestMapping("/usuario")
@CrossOrigin(origins = "*")
public class UsuarioController {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private EnderecoRepository enderecoRepository;


	private ValidaCPF validaCPF;

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Collection<Usuario>> buscarTodos() {
		return new ResponseEntity<Collection<Usuario>>(this.usuarioRepository.findAll(), HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Usuario> detalhes(@PathVariable Integer id) {
		Usuario teste = this.usuarioRepository.findById(id).get();
		return new ResponseEntity<Usuario>(teste, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{cpf}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Usuario> detalhes(@PathVariable String cpf) {
		Usuario teste = this.usuarioRepository.findByCpf(cpf).get(0);
		return new ResponseEntity<Usuario>(teste, HttpStatus.OK);
	}

	@RequestMapping(method = { RequestMethod.POST,
			RequestMethod.PUT }, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuarioRequest) {
		if (!ValidaCPF.isCPF(usuarioRequest.getCpf())) {
			return new ResponseEntity<Usuario>(HttpStatus.BAD_REQUEST);
		}

		List<Usuario> aux = this.usuarioRepository.findByCpf(usuarioRequest.getCpf());
		if (aux.size() > 0 && aux.get(0).getId() != usuarioRequest.getId()) {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		if (usuarioRequest.getId() == null) {
			usuarioRequest.setDtAbertura(LocalDate.now());
		}

		usuarioRequest.setDtAlteracao(LocalDate.now());
		if (usuarioRequest.getEndereco().getId() == null) {
		usuarioRequest.getEndereco().setId(this.verificaEndereco(usuarioRequest.getEndereco()));
		}
		
		Usuario usuario = this.usuarioRepository.save(usuarioRequest);
		HttpStatus status = usuario == null ? HttpStatus.BAD_REQUEST : HttpStatus.OK;
		return new ResponseEntity<Usuario>(usuario, status);
	}

	private Integer verificaEndereco(Endereco end) {
		String cep = end.getCep().length() == 8 ? end.getCep().substring(0, 5) + '-' + end.getCep().substring(5, 8) : end.getCep();
		Endereco result = this.enderecoRepository.findByCep(cep);
		if (result == null) {
			result = new EnderecoController().buscarViaCep(cep);
			result= this.enderecoRepository.save(result);
			
		}
		//ResponseEntity<Endereco> result = new EnderecoController().buscarCep(end.getCep());
		if(result!=null) {
			return result.getId();
		}
		return null;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> deletar(@PathVariable Integer id) {
		Usuario usuario = this.usuarioRepository.findById(id).orElse(null);
		HttpStatus status = usuario == null ? HttpStatus.NOT_FOUND : HttpStatus.OK;
		String mensagem = "";
		if (usuario != null) {
			this.usuarioRepository.delete(usuario);
			mensagem = "Registro deletado com sucesso!";
		}else {
			mensagem = "Não foi possível excluír o registro!";
		}

		return new ResponseEntity<Void>(status);
	}
}
