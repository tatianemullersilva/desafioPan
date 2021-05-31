package br.com.pan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import br.com.pan.model.Usuario;
import br.com.pan.repository.UsuarioRepository;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UsuarioTeste {

	private Usuario usuario = new Usuario();

	@Autowired
	private UsuarioRepository usuarioRepository;

	private MockMvc mvc;

	public Usuario criarEntidadeAnimal(EntityManager em) {
		Usuario usuario = new Usuario();
		usuario.setId(1);
		usuario.setCpf("106.889.680-90");
		usuario.setNome("Joao Teste da Silva");
		usuario.setSexo("F");
		usuario.setMae("Maria da Silva");
		usuario.setEmail("joaotestedasilva@gmail.com");
		usuario.setDtnascimento(LocalDate.now());
		usuarioRepository.save(usuario);
		return usuario;
	}

	@Transactional
	void getUsuarioByCpf_cpfInexistente() throws Exception {
		usuarioRepository.saveAndFlush(usuario);
		mvc.perform(MockMvcRequestBuilders.get("/usuario/detalhes").param("cpf", "111.111.111-11")
				.contentType("application/json")).andExpect(status().isOk()).andExpect(jsonPath("$.[*].id").isEmpty());
	}

}
