package com.blackcode.arquitecturefacade.application.facade;

import java.time.LocalDate;

public interface ViagemFacade {
    void marcarViagem(String nome, LocalDate inicio, LocalDate fim, String numeroVoo, String matricula);
}
