package com.spessoa.integracao.sevenautomacao.domain.pcdoceletronico;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Table(name = "pcdoceletronico")
@Entity
@Data
public class PcdoceletronicoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long numtransacao;
    @Lob
    private String xmlnfe;
    private LocalDateTime data;
}
