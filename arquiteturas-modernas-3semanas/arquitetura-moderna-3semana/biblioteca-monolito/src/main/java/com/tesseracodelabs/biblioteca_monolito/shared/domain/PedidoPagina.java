package com.tesseracodelabs.biblioteca_monolito.shared.domain;

/**
 * Requisição de paginação vinda da camada de aplicação para os ports de saída,
 * sem acoplar o domínio ao {@code Pageable} do Spring Data.
 *
 * @param pagina  índice base 0 (negativo é normalizado para 0)
 * @param tamanho itens por página (fora de [1, 200] é normalizado)
 */
public record PedidoPagina(int pagina, int tamanho) {

    private static final int TAMANHO_MAX = 200;
    private static final int TAMANHO_PADRAO = 20;

    public PedidoPagina {
        if (pagina < 0) {
            pagina = 0;
        }
        if (tamanho < 1) {
            tamanho = TAMANHO_PADRAO;
        }
        if (tamanho > TAMANHO_MAX) {
            tamanho = TAMANHO_MAX;
        }
    }

    public static PedidoPagina de(Integer pagina, Integer tamanho) {
        return new PedidoPagina(pagina == null ? 0 : pagina, tamanho == null ? TAMANHO_PADRAO : tamanho);
    }
}
