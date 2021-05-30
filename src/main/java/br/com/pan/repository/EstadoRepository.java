package br.com.pan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.pan.model.Estado;

@Repository
public interface EstadoRepository extends JpaRepository<Estado, Integer> {
	
	Estado findByNome(String nome);

}
