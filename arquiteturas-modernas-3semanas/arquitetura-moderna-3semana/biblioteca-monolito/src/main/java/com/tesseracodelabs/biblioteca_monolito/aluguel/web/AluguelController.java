package com.tesseracodelabs.biblioteca_monolito.aluguel.web;

import com.tesseracodelabs.biblioteca_monolito.aluguel.application.AlugarUseCase;
import com.tesseracodelabs.biblioteca_monolito.aluguel.application.BuscarAluguelUseCase;
import com.tesseracodelabs.biblioteca_monolito.aluguel.application.DevolverUseCase;
import com.tesseracodelabs.biblioteca_monolito.aluguel.application.ListarAlugueisPorLocatarioUseCase;
import com.tesseracodelabs.biblioteca_monolito.aluguel.application.ListarAtrasadosUseCase;
import com.tesseracodelabs.biblioteca_monolito.aluguel.domain.Aluguel;
import com.tesseracodelabs.biblioteca_monolito.aluguel.web.dto.AlugarRequest;
import com.tesseracodelabs.biblioteca_monolito.aluguel.web.dto.AluguelResponse;
import com.tesseracodelabs.biblioteca_monolito.aluguel.web.dto.DevolucaoRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Endpoints REST de aluguéis, sob {@code /api/alugueis}.
 */
@RestController
@RequestMapping("/api/alugueis")
@Tag(name = "Alugueis", description = "Alugar, devolver e consultar alugueis")
public class AluguelController {

    private final AlugarUseCase alugar;
    private final DevolverUseCase devolver;
    private final BuscarAluguelUseCase buscar;
    private final ListarAlugueisPorLocatarioUseCase listarPorLocatario;
    private final ListarAtrasadosUseCase listarAtrasados;
    private final AluguelWebMapper mapper;

    public AluguelController(AlugarUseCase alugar,
                            DevolverUseCase devolver,
                            BuscarAluguelUseCase buscar,
                            ListarAlugueisPorLocatarioUseCase listarPorLocatario,
                            ListarAtrasadosUseCase listarAtrasados,
                            AluguelWebMapper mapper) {
        this.alugar = alugar;
        this.devolver = devolver;
        this.buscar = buscar;
        this.listarPorLocatario = listarPorLocatario;
        this.listarAtrasados = listarAtrasados;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Aluga um livro (a taxa e calculada no servidor)")
    public ResponseEntity<AluguelResponse> alugar(@Valid @RequestBody AlugarRequest request,
                                                  UriComponentsBuilder uriBuilder) {
        Aluguel aluguel = alugar.executar(new AlugarUseCase.Comando(
                request.livroId(), request.nomeLocatario(), request.dataRetirada(), request.dias()));
        URI location = uriBuilder.path("/api/alugueis/{id}").buildAndExpand(aluguel.getId()).toUri();
        return ResponseEntity.created(location).body(mapper.paraResponse(aluguel));
    }

    @PostMapping("/{id}/devolucao")
    @Operation(summary = "Registra a devolucao de um aluguel")
    public AluguelResponse devolver(@PathVariable Long id,
                                    @RequestBody(required = false) DevolucaoRequest request) {
        var quando = request == null ? null : request.dataDevolucao();
        return mapper.paraResponse(devolver.executar(id, quando));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um aluguel pelo ID")
    public AluguelResponse buscar(@PathVariable Long id) {
        return mapper.paraResponse(buscar.executar(id));
    }

    @GetMapping
    @Operation(summary = "Lista alugueis de um locatario")
    public List<AluguelResponse> porLocatario(@RequestParam String locatario) {
        return listarPorLocatario.executar(locatario).stream().map(mapper::paraResponse).toList();
    }

    @GetMapping("/atrasados")
    @Operation(summary = "Lista alugueis atrasados (e marca-os como ATRASADO)")
    public List<AluguelResponse> atrasados() {
        return listarAtrasados.executar().stream().map(mapper::paraResponse).toList();
    }
}
