package it.epicode.flavor_hub.errors;


import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // gestisce eccezioni tramite spring e verifica la presenza dei metodi che gestiscono gli errori
public class ErrorHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFound(EntityNotFoundException error){
        String strError = error.getMessage();
        return new ResponseEntity<>(strError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<String> handleEntityExists(EntityExistsException error){
        String strError = error.getMessage();
        return new ResponseEntity<>(strError, HttpStatus.FOUND);
    }

    // Handler dell'errore di validazione

    /*
    Restituisce una mappa cosi composta:
       - nome del campo che e' andato in errore
       - stringa che contiene l'errore generato
      Il front end otterra' una cosa simile a quella che segue:
       Json:
       {
       "nome" : "Il nome non puo essere vuoto",
       "cognome" : "Il cognome non puo essere vuoto"
       }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException error){
        Map errorResponse = new HashMap();
        error.getBindingResult().getAllErrors().forEach(
                er->{
                    FieldError frError = (FieldError) er; // recupera il nome del campo non valido
                    String nomeCampo = frError.getField(); // recupera il nome del campo non valido
                    String errorMessage = er.getDefaultMessage(); // recupera il messaggio dell'annotazione @NotEmpy dentro il nostro Dto
                    errorResponse.put(nomeCampo, errorMessage);
                }
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
 }
