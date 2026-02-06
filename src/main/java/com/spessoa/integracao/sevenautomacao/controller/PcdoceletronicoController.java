package com.spessoa.integracao.sevenautomacao.controller;

import com.spessoa.integracao.sevenautomacao.domain.pcdoceletronico.PcdoceletronicoModel;
import com.spessoa.integracao.sevenautomacao.domain.pcdoceletronico.PcdoceletronicoRepository;
import com.spessoa.integracao.sevenautomacao.domain.pcdoceletronico.PcdoceletronicosResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pcdoceletronico")
public class PcdoceletronicoController {
    @Autowired
    private PcdoceletronicoRepository pcdoceletronicoRepository;

    DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @GetMapping("/xml-periodo")
    public ResponseEntity<?> obterXmlsPeriodo(
            @RequestParam() String dataInicial,
            @RequestParam() String dataFinal,
            @RequestParam() Integer pagina
    ) {
        LocalDate dataInicialF = null;
        LocalDate dataFinalF = null;
        int anoMinimo = 2025;
        try {
            dataInicialF = LocalDate.parse(dataInicial, formatter);
            if (dataInicialF.getYear() < anoMinimo) {
                return ResponseEntity.badRequest().body("Não permitido busca em anos anteriores a " + anoMinimo);
            }
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Parâmetro 'dataInicial' inválido!");
        }
        try {
            dataFinalF = LocalDate.parse(dataFinal, formatter);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Parâmetro 'dataFinal' inválido!");
        }
        if (pagina == null) {
            return ResponseEntity.badRequest().body("Parâmetro 'pagina' inválido!");
        }

        int tamanhoPagina = 500;
        Integer quantidadeRegistros = pcdoceletronicoRepository.obterQuantidadeXmlsPeriodoOrdernadoNumTrans(dataInicialF, dataFinalF);
        int qtdTotalPaginas = 0;
        if (quantidadeRegistros != 0) {
            qtdTotalPaginas = (int) Math.ceil((double) quantidadeRegistros / tamanhoPagina);
        }
        List<PcdoceletronicoModel> xmls = new ArrayList<>();
        if (pagina <= qtdTotalPaginas) {
            xmls = pcdoceletronicoRepository.obterXmlsPeriodoOrdernadoNumTransPaginacao(dataInicialF, dataFinalF, pagina, tamanhoPagina);
        }

        PcdoceletronicosResponseDTO dto = new PcdoceletronicosResponseDTO(pagina, qtdTotalPaginas, tamanhoPagina, xmls.size(), quantidadeRegistros, xmls);

        return ResponseEntity.ok(dto);
    }
}
