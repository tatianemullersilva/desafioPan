package br.com.pan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import br.com.pan.model.Endereco;
import br.com.pan.repository.EnderecoRepository;
import javax.persistence.EntityManager;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EnderecoTeste {

	private Endereco endereco = new Endereco();

	@Autowired
	private EnderecoRepository enderecoRepository;

	private MockMvc mvc;

	public Endereco criarEntidadeAnimal(EntityManager em) {
		Endereco endereco = new Endereco();
		endereco.setId(1);
		endereco.setCep("79094-100");
		endereco.setLogradouro("Rua de Teste");
		endereco.setLocalidade("Campo Grande");
		enderecoRepository.save(endereco);
		return endereco;
	}

	@Transactional
	void getEnderecoByCep() throws Exception {
		enderecoRepository.saveAndFlush(endereco);
		mvc.perform(MockMvcRequestBuilders.get("/endereco/buscarCep").param("cep", "79094-120")
				.contentType("application/json")).andExpect(status().isOk()).andExpect(jsonPath("$.[*].id").isEmpty());
	}

}
