package br.com.pan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.pan.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{
	
	
	List<Usuario> findByCpf(String cpf);
	
	
//    @Query(value = "SELECT u " +
//        "FROM Usuario u " +
//        "WHERE " +
//        "(u.cpf) = :cpf ")
//    Usuario findBuscaCPFUsu(@Param("cpf") String cpf);
//	
	
	

	

}
