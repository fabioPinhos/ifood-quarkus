package com.fabio.ifood.cadastro.dto;

import com.fabio.ifood.cadastro.Prato;
import org.mapstruct.Mapper;

@Mapper(componentModel = "jakarta")
public interface PratoMapper {

    Prato toPrato(AdicionarPratoDTO dto);


}
