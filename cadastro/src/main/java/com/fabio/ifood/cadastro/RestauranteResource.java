package com.fabio.ifood.cadastro;

import com.fabio.ifood.cadastro.dto.*;
import com.fabio.ifood.cadastro.infra.ConstraintViolationResponse;
import com.oracle.svm.core.annotate.Delete;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.*;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Path("/restaurantes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "restaurante")
@RolesAllowed("proprietario")
@SecurityScheme(securitySchemeName = "ifood-oauth", type = SecuritySchemeType.OAUTH2,
        flows = @OAuthFlows(password = @OAuthFlow(tokenUrl = "http://localhost:8180/auth/realms/ifood/protocol/openid-connect/token")))
public class RestauranteResource {
    @Inject
    RestauranteMapper restauranteMapper;

    @Inject
    PratoMapper pratoMapper;

    @GET
    public List<RestauranteDTO> list() {
        Stream<Restaurante> restaurante = Restaurante.streamAll();

        return restaurante.map(r -> restauranteMapper.toRestauranteDTO(r)).collect(Collectors.toList());
    }

    @POST
    @Transactional
    @APIResponse(responseCode = "201", description = "Caso restaurante seja cadastrado com sucesso")
    @APIResponse(responseCode = "400", content = @Content(schema = @Schema(allOf = ConstraintViolationResponse.class)))
    public Response adicionar(@Valid AdicionarRestauranteDTO dto){
        Restaurante restaurante = restauranteMapper.toRestaurante(dto);
        restaurante.persist();
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public void atualizar(@PathParam("id") Long id, AtualizarRestauranteDTO dto){
        Optional<Restaurante> restauranteOp = Restaurante.findByIdOptional(id);

        if(restauranteOp.isEmpty()){
            throw new NotFoundException();
        }

        Restaurante restaurante = restauranteOp.get();

        restauranteMapper.toRestaurante(dto, restaurante);
        restaurante.persist();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public void apagar(@PathParam("id") Long id){
        Optional<Restaurante> byIdOptional = Restaurante.findByIdOptional(id);
        byIdOptional.ifPresentOrElse(Restaurante::delete, ()->{
            throw new NotFoundException();
        });
    }

    @GET
    @Path("{idRestaurante}")
    @Tag(name = "pratos")
    public List<Prato> buscarPratos(@PathParam("idRestaurante") Long idRestaurante){
        Optional<Restaurante> restauranteOp = Restaurante.findByIdOptional(idRestaurante);
        if(restauranteOp.isEmpty()){
            throw new NotFoundException("Restaurante não existe");
        }
        return Prato.list("restaurante", restauranteOp.get());
    }

    @POST
    @Path("{idRestaurante}")
    @Tag(name = "pratos")
    @Transactional
    public Response adicionarPrato(@PathParam("idRestaurante") Long idRestaurante, Prato dto){
        Optional<Restaurante> restauranteOp = Restaurante.findByIdOptional(idRestaurante);

        if(restauranteOp.isEmpty()){
            throw new NotFoundException();
        }

        Prato prato = new Prato();
        prato.descricao = dto.descricao;
        prato.nome = dto.nome;
        prato.restaurante = restauranteOp.get();
        prato.preco = dto.preco;
        prato.persist();

        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path(("{idRestaurante}/pratos/{id}"))
    @Tag(name = "pratos")
    @Transactional
    public void atualizarPrato(@PathParam("idRestaurante") Long idRestaurante, @PathParam("id") Long id, Prato dto){
        Optional<Restaurante> restauranteOp = Restaurante.findByIdOptional(idRestaurante);

        if(restauranteOp.isEmpty()){
            throw new NotFoundException("Restaurante não existe");
        }

        Optional<Prato> pratoOp = Prato.findByIdOptional(id);

        if (pratoOp.isEmpty()){
            throw new NotFoundException("Prato não existe");
        }

        Prato prato = pratoOp.get();
        prato.preco = dto.preco;
        prato.persist();

    }

    @Delete
    @Path("{idRestaurante}/pratos/{id}")
    @Tag(name = "pratos")
    @Transactional
    public void deletarPrato(@PathParam("idRestaurante") Long idRestaurante, @PathParam("id") Long id){
        Optional<Restaurante> restauranteOp = Restaurante.findByIdOptional(idRestaurante);

        if (restauranteOp.isEmpty()){
            throw new NotFoundException("Restaurante não existe");
        }

        Optional<Prato> pratoOp = Prato.findByIdOptional(id);
        pratoOp.ifPresentOrElse(Prato::delete, ()-> {
            throw new NotFoundException("Prato não existe");
        });
    }

}
