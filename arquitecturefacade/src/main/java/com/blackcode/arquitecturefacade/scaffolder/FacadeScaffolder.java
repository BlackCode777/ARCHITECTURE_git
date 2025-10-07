package com.blackcode.arquitecturefacade.scaffolder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

public class FacadeScaffolder {

    public static void main(String[] args) throws IOException {
        String root = args.length > 0 ? args[0] : ".";
        String basePkg = "com.blackcode.arquiteturafacade"; // pacote alvo solicitado

        // Cria SOMENTE em src/main/java/com/blackcode/arquiteturafacade
        Path pkgRoot = Paths.get(root, "src", "main", "java", basePkg.replace('.', '/'));

        List<Path> dirs = List.of(
                pkgRoot.resolve("domain"),
                pkgRoot.resolve("application/facade"),
                pkgRoot.resolve("application/service"),
                pkgRoot.resolve("infrastructure/adapters/dao"),
                pkgRoot.resolve("infrastructure/adapters/crm"),
                pkgRoot.resolve("infrastructure/adapters/ldap"),
                pkgRoot.resolve("infrastructure/adapters/files"),
                pkgRoot.resolve("infrastructure/config"),
                pkgRoot.resolve("api")
        );

        int createdDirs = 0;
        int createdFiles = 0;

        for (Path d : dirs) {
            Files.createDirectories(d);
            createdDirs++;
            Path keep = d.resolve(".gitkeep");
            if (Files.notExists(keep)) {
                Files.write(keep, new byte[0], StandardOpenOption.CREATE_NEW);
                createdFiles++;
            }
            System.out.println("created: " + d.toAbsolutePath());
        }

        // Stubs mínimos solicitados na estrutura
        createdFiles += writeIfAbsent(
                pkgRoot.resolve("application/facade/UsuarioFacade.java"),
                """
                package %s.application.facade;

                public interface UsuarioFacade {
                    void criarUsuario(Object usuario);
                }
                """.formatted(basePkg)
        );

        createdFiles += writeIfAbsent(
                pkgRoot.resolve("application/facade/ViagemFacade.java"),
                """
                package %s.application.facade;

                import java.time.LocalDate;

                public interface ViagemFacade {
                    void marcarViagem(String nome, LocalDate inicio, LocalDate fim, String numeroVoo, String matricula);
                }
                """.formatted(basePkg)
        );

        System.out.printf("Facade scaffolding ready. Dirs: %d, Files: %d%n", createdDirs, createdFiles);
    }

    private static int writeIfAbsent(Path file, String content) throws IOException {
        if (Files.notExists(file)) {
            Files.createDirectories(file.getParent());
            Files.writeString(file, content, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
            System.out.println("seed: " + file.toAbsolutePath());
            return 1;
        } else {
            System.out.println("exists, skip: " + file.toAbsolutePath());
            return 0;
        }
    }
}
