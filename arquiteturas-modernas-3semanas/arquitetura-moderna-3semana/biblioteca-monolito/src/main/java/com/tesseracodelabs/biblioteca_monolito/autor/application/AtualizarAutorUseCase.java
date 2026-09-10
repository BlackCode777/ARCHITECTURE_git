package com.tesseracodelabs.biblioteca_monolito.autor.application;

import com.tesseracodelabs.biblioteca_monolito.autor.domain.Autor;
import com.tesseracodelabs.biblioteca_monolito.autor.domain.AutorRepository;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Atualiza os dados de um autor existente.
 */
@Service
public class AtualizarAutorUseCase {

    private final AutorRepository autorRepository;

    public AtualizarAutorUseCase(AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

    @Transactional
    public Autor executar(Long id, Comando comando) {
        Autor autor = autorRepository.buscarPorId(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Autor", id));
        autor.atualizar(
                comando.nome(),
                comando.nacionalidade(),
                comando.nascimento(),
                comando.biografia());
        return autorRepository.salvar(autor);
    }

    public record Comando(String nome, String nacionalidade, LocalDate nascimento, String biografia) {
    }
}
