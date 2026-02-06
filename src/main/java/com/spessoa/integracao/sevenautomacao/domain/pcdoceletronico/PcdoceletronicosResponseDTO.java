package com.spessoa.integracao.sevenautomacao.domain.pcdoceletronico;

import java.util.List;

public record PcdoceletronicosResponseDTO(
        Integer paginaAtual,
        Integer totalPaginas,
        Integer tamanhoPagina,
        Integer registrosPaginaAtual,
        Integer totalRegistros,
        List<PcdoceletronicoModel> dados
) {
}
