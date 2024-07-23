package com.fabio.ifood.cadastro.dto;

import com.fabio.ifood.cadastro.Restaurante;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "jakarta")
public interface RestauranteMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    @Mapping(target = "localizacao.id", ignore = true)
    Restaurante toRestaurante(AdicionarRestauranteDTO dto);

    @Mapping(target="dataCriacao", dateFormat = "dd/MM/yyyy HH:mm:ss")
    RestauranteDTO toRestauranteDTO(Restaurante restaurante);

    void toRestaurante(AtualizarRestauranteDTO dto, @MappingTarget Restaurante restaurante);
}
