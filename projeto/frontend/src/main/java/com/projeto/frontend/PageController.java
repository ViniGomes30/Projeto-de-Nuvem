package com.projeto.frontend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Controller
public class PageController {

    @Value("${backend.url}")
    private String backendUrl;

    @GetMapping("/")
    public String index(Model model) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            Map response = restTemplate.getForObject(backendUrl + "/api/dados", Map.class);
            model.addAttribute("mensagem", response.get("mensagem"));
            model.addAttribute("status", response.get("status"));
            model.addAttribute("itens", response.get("itens"));
            model.addAttribute("erro", false);
        } catch (Exception e) {
            model.addAttribute("mensagem", "Erro ao conectar com o backend: " + e.getMessage());
            model.addAttribute("erro", true);
        }
        return "index";
    }
}
