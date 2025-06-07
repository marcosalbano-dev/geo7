package org.geo7.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "dados_sobre_uso", schema = "geo7")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DadosSobreUso implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dados_sobre_uso_seq")
    @SequenceGenerator(name = "dados_sobre_uso_seq", sequenceName = "geo7.dados_sobre_uso_id_seq", allocationSize = 1)
    private Long id;

     @ManyToOne(optional = true)
     private Lote lote;

    @Column(name = "area_total_rotacao")
    private Double areaTotalRotacao;

    @Column(name = "area_total_consorcio")
    private Double areaTotalConsorcio;

    @Column(name = "area_total_isolado")
    private Double areaTotalIsolado;

    @OneToMany(mappedBy = "dadosSobreUso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items;

    @Override
    public String toString() {
        return items != null ? items.toString() : "";
    }
}
