package com.projeto.backend;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class DataController {

    @GetMapping("/dados")
    public Map<String, Object> getDados() {
        Map<String, Object> response = new LinkedHashMap<>();
       response.put("mensagem", "Projeto do Grupo rodando na AWS com Docker.");
        response.put("status", "OK");
        response.put("porta", 25000);
        response.put("itens", List.of(
            "Item 1 - Foi dificil de fazer",
            "Item 2 - Corrige com carinho por favor",
            "Item 3 - Se Deus quiser vem nota boa"
        ));
        return response;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        return response;
    }
}
