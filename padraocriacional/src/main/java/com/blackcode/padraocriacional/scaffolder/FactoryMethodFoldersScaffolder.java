package com.blackcode.padraocriacional.scaffolder;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class FactoryMethodFoldersScaffolder {

    public static void main(String[] args) throws IOException {
        // raiz do projeto; se não passar, usa a pasta atual
        String root = args.length > 0 ? args[0] : ".";
        String basePkg = "com.blackcode.padraocriacional.factorymethod";

        Path baseMain = Paths.get(root, "src", "main", "java", basePkg.replace('.', '/'));
        Path baseTest = Paths.get(root, "src", "test", "java", basePkg.replace('.', '/'));

        List<Path> dirs = List.of(
                baseMain.resolve("api/product"),
                baseMain.resolve("api/creator"),
                baseMain.resolve("core/product"),
                baseMain.resolve("core/creator"),
                baseMain.resolve("app"),
                baseMain.resolve("config"),
                baseMain.resolve("adapter/notification"),
                baseMain.resolve("adapter/storage"),
                baseTest // pacote de testes correspondente (vazio)
        );

        for (Path d : dirs) {
            Files.createDirectories(d);
            // opcional: .gitkeep para versionar diretórios vazios
            Path keep = d.resolve(".gitkeep");
            if (Files.notExists(keep)) Files.createFile(keep);
            System.out.println("created: " + d.toAbsolutePath());
        }

        System.out.println("factory-method folders ready.");
    }
}

