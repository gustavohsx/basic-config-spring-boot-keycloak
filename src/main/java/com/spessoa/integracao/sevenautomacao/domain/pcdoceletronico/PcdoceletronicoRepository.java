package com.spessoa.integracao.sevenautomacao.domain.pcdoceletronico;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PcdoceletronicoRepository extends JpaRepository<PcdoceletronicoModel, Long> {
    @Query(value = """
            SELECT
                count(numtransacao)
            FROM
                pcdoceletronico p
            WHERE
                p.numtransacao IN (
                    SELECT
                        numtransvenda
                    FROM
                        pcpedc
                    WHERE
                        data BETWEEN :datai AND :dataf
                        AND condvenda = 1
                        AND dtcancel IS NULL
                        AND codfilial IN ('1')
                )
            ORDER BY
                p.numtransacao
            """, nativeQuery = true)
    Integer obterQuantidadeXmlsPeriodoOrdernadoNumTrans(
            @Param("datai") LocalDate dataInicial,
            @Param("dataf") LocalDate dataFinal
    );

    @Query(value = """
            SELECT
                numtransacao,
                xmlnfe,
                dtmxsalter as data
            FROM (
                SELECT
                    t.*,
                    ROWNUM AS rn
                FROM (
                    SELECT
                        p.numtransacao,
                        p.xmlnfe,
                        p.dtmxsalter
                    FROM
                        pcdoceletronico p
                    WHERE
                        p.numtransacao IN (
                            SELECT
                                numtransvenda
                            FROM
                                pcpedc
                            WHERE
                                data BETWEEN :datai AND :dataf
                                AND condvenda = 1
                                AND dtcancel IS NULL
                                AND codfilial IN ('1')
                        )
                    ORDER BY
                        p.numtransacao
                ) t
                WHERE
                    ROWNUM <= :pagina * :tamanhoPagina
            )
            WHERE
                rn > (:pagina - 1) * :tamanhoPagina
            """, nativeQuery = true)
    List<PcdoceletronicoModel> obterXmlsPeriodoOrdernadoNumTransPaginacao(
            @Param("datai") LocalDate dataInicial,
            @Param("dataf") LocalDate dataFinal,
            @Param("pagina") Integer pagina,
            @Param("tamanhoPagina") Integer tamanhoPagina
    );

    @Query(value = """
            SELECT
                ceil(count(1)/:tamanhoPagina)
            FROM
                pcpedc
            WHERE
                data BETWEEN :datai AND :dataf
                AND condvenda = 1
                AND dtcancel IS NULL
                AND codfilial IN ('1')
            """, nativeQuery = true)
    Integer quantidadePaginas(
            @Param("datai") LocalDate dataInicial,
            @Param("dataf") LocalDate dataFinal,
            @Param("tamanhoPagina") Integer tamanhoPagina
    );
}
