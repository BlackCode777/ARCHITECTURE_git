package com.tesseracodelabs.biblioteca_monolito.autor.application;

import com.tesseracodelabs.biblioteca_monolito.autor.domain.Autor;
import com.tesseracodelabs.biblioteca_monolito.autor.domain.AutorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Cria um novo autor.
 */
@Service
public class CriarAutorUseCase {

    private final AutorRepository autorRepository;

    public CriarAutorUseCase(AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

    @Transactional
    public Autor executar(Comando comando) {
        Autor autor = Autor.novo(
                comando.nome(),
                comando.nacionalidade(),
                comando.nascimento(),
                comando.biografia());
        return autorRepository.salvar(autor);
    }

    public record Comando(String nome, String nacionalidade, LocalDate nascimento, String biografia) {
    }
}
