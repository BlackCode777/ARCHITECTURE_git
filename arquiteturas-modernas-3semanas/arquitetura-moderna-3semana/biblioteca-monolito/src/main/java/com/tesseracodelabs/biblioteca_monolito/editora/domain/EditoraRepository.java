package com.tesseracodelabs.biblioteca_monolito.editora.domain;

import com.tesseracodelabs.biblioteca_monolito.shared.domain.Pagina;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.PedidoPagina;

import java.util.Optional;

/**
 * Port de saída para persistência de {@code Editora}.
 */
public interface EditoraRepository {

    Editora salvar(Editora editora);

    Optional<Editora> buscarPorId(Long id);

    Pagina<Editora> listar(PedidoPagina pedido);

    void remover(Long id);

    boolean existePorId(Long id);

    /**
     * @return true se já existe uma editora com este CNPJ e id diferente de
     *         {@code ignorarId} (null ignora nada) — usado para checar unicidade.
     */
    boolean existePorCnpj(String cnpj, Long ignorarId);
}
