package br.com.boardpadbackend.controllers;

import br.com.boardpadbackend.dto.StatusDto;
import br.com.boardpadbackend.service.StatusService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.models.Response;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/status")
@Api(value = "Status", tags = {"Status"})
public class StatusController {
    private StatusService statusService;

    @Autowired
    public StatusController(StatusService statusService) {
        this.statusService = statusService;
    }

    @ApiOperation(value = "Returns all status available")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 204, message = "No content to show")
    })
    @GetMapping
    public ResponseEntity<List<StatusDto>> listAllStatus() {
        List<StatusDto> statusList = statusService.listAllStatus();
        return (statusList.size() > 0) ? ResponseEntity.ok().body(statusList) :
                ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "Creates new status")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 400, message = "Request error"),
            @ApiResponse(code = 500, message = "Server error, please try later.")
    })
    @PostMapping
    public StatusDto createNewStatus(@RequestBody String statusName) {
        return statusService.createNewStatus(statusName);
    }

    @ApiOperation(value = "Deletes a status")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Error, please delete this status tasks before delete this status"),
            @ApiResponse(code = 500, message = "Server error, please try later.")
    })
    @DeleteMapping(path = "{id}")
    public ResponseEntity<String> deleteStatus(@PathVariable("id") Long idStatus) {
        try {
            statusService.deleteStatus(idStatus);
            return ResponseEntity.ok().build();
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Para remover essa coluna de status você deve primeiro remover suas tarefas.");
        }
    }

    @ApiOperation(value = "Updates the status name")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 500, message = "Server error, please try later.")
    })
    @PutMapping(path = "{id}")
    public ResponseEntity<?> updateStatusName (@PathVariable("id") Long idStatus, @RequestParam(name = "new-name") String newStatusName) {
        try {
            statusService.updateStatusName(idStatus, newStatusName);
            return ResponseEntity.ok().body("Atualizado com sucesso!");
        }catch(Exception ex){
            log.error(ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno do servidor, por favor tente novamente mais tarde.");
        }
    }
}