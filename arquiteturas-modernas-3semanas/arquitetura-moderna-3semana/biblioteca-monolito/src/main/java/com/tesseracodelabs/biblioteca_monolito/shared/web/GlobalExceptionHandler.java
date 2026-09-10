package com.tesseracodelabs.biblioteca_monolito.shared.web;

import com.tesseracodelabs.biblioteca_monolito.shared.domain.ConflitoException;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.RecursoNaoEncontradoException;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.RegraNegocioException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

/**
 * Traduz exceções para {@code ProblemDetail} (RFC 9457). Único ponto de
 * conversão de erro → resposta HTTP no monólito.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final URI TIPO_NAO_ENCONTRADO = URI.create("urn:biblioteca:erro:nao-encontrado");
    private static final URI TIPO_REGRA_NEGOCIO = URI.create("urn:biblioteca:erro:regra-de-negocio");
    private static final URI TIPO_CONFLITO = URI.create("urn:biblioteca:erro:conflito");
    private static final URI TIPO_VALIDACAO = URI.create("urn:biblioteca:erro:validacao");

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ProblemDetail tratarNaoEncontrado(RecursoNaoEncontradoException ex) {
        return problema(HttpStatus.NOT_FOUND, "Recurso nao encontrado", ex.getMessage(), TIPO_NAO_ENCONTRADO);
    }

    @ExceptionHandler(ConflitoException.class)
    public ProblemDetail tratarConflito(ConflitoException ex) {
        return problema(HttpStatus.CONFLICT, "Conflito de estado", ex.getMessage(), TIPO_CONFLITO);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ProblemDetail tratarRegraNegocio(RegraNegocioException ex) {
        return problema(HttpStatus.UNPROCESSABLE_ENTITY, "Regra de negocio violada", ex.getMessage(), TIPO_REGRA_NEGOCIO);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail tratarValidacao(MethodArgumentNotValidException ex) {
        List<Map<String, String>> erros = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> Map.of(
                        "campo", fe.getField(),
                        "mensagem", fe.getDefaultMessage() == null ? "invalido" : fe.getDefaultMessage()))
                .toList();

        ProblemDetail pd = problema(HttpStatus.BAD_REQUEST, "Requisicao invalida",
                "Um ou mais campos sao invalidos", TIPO_VALIDACAO);
        pd.setProperty("erros", erros);
        return pd;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail tratarArgumentoIlegal(IllegalArgumentException ex) {
        return problema(HttpStatus.BAD_REQUEST, "Requisicao invalida", ex.getMessage(), TIPO_VALIDACAO);
    }

    private ProblemDetail problema(HttpStatus status, String titulo, String detalhe, URI tipo) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detalhe);
        pd.setTitle(titulo);
        pd.setType(tipo);
        pd.setProperty("timestamp", OffsetDateTime.now(ZoneOffset.UTC));
        return pd;
    }
}
