package com.tesseracode_labs.fundamentos_2semanas.shared.exception;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Handler centralizado de erros no formato RFC 9457 (ProblemDetail). */
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(TarefaNaoEncontradaException.class)
	public ProblemDetail handleTarefaNaoEncontrada(TarefaNaoEncontradaException ex) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
		problem.setTitle("Tarefa não encontrada");
		return problem;
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ProblemDetail handleValidacao(MethodArgumentNotValidException ex) {
		List<Map<String, String>> erros = ex.getBindingResult().getFieldErrors().stream()
				.map(fe -> Map.of(
						"campo", fe.getField(),
						"mensagem", Objects.requireNonNullElse(fe.getDefaultMessage(), "inválido")))
				.toList();

		ProblemDetail problem = ProblemDetail.forStatusAndDetail(
				HttpStatus.BAD_REQUEST, "Um ou mais campos são inválidos");
		problem.setTitle("Dados de entrada inválidos");
		problem.setProperty("erros", erros);
		return problem;
	}

	/**
	 * Cenário de estudo do CAP Theorem (docs/01-cap-service.md e
	 * docs/09-tasks-EDA-CAP-REST.md): quando o PostgreSQL está indisponível
	 * (partição simulada), retorna 503 em vez de vazar um erro 500 genérico —
	 * API prioriza Consistency sobre Availability (comportamento CP).
	 *
	 * Duas exceções distintas cobrem os dois pontos de falha observados:
	 * CannotCreateTransactionException quando o Hikari não consegue abrir
	 * conexão (timeout de 30s do pool) e DataAccessResourceFailureException
	 * para outras falhas de acesso a dados.
	 */
	@ExceptionHandler({CannotCreateTransactionException.class, DataAccessResourceFailureException.class})
	public ProblemDetail handleBancoIndisponivel(Exception ex) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(
				HttpStatus.SERVICE_UNAVAILABLE,
				"Banco de dados indisponível no momento. Tente novamente em instantes.");
		problem.setTitle("Serviço indisponível — partição de dados");
		return problem;
	}
}
