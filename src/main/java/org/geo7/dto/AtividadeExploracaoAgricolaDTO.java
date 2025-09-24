    package org.geo7.dto;
    
    import org.geo7.model.entity.AtividadeExploracaoAgricola;
    
    
    public record AtividadeExploracaoAgricolaDTO(
            Long id, Double areaExplorada, String indicadorRestricao
    ) {
    
        public static AtividadeExploracaoAgricolaDTO fromEntity(AtividadeExploracaoAgricola entity) {
            return new AtividadeExploracaoAgricolaDTO(
                    entity.getId(), entity.getAreaExplorada(), entity.getIndicadorRestricao()
            );
        }
    
        public AtividadeExploracaoAgricola toEntity() {
            AtividadeExploracaoAgricola entity = new AtividadeExploracaoAgricola();
            entity.setId(id);
            entity.setAreaExplorada(areaExplorada);
            entity.setIndicadorRestricao(indicadorRestricao);
            return entity;
        }
    }
