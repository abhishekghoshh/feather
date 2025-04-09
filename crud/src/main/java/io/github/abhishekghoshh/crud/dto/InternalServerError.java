package io.github.abhishekghoshh.crud.dto;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import io.github.abhishekghoshh.crud.exception.InternalServerException;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class InternalServerError {

    private String message;

    public InternalServerError() {
    }

    public InternalServerError(String message) {
        this.message = message;
    }

    public static InternalServerError create(Throwable throwable) {
        return new InternalServerError(throwable.getMessage());
    }

    public static InternalServerError create(InternalServerException internalServerException) {
        return new InternalServerError(internalServerException.getMessage());
    }

    public static InternalServerError create(IllegalArgumentException illegalArgumentException) {
        if (illegalArgumentException.getCause() == null) {
            return new InternalServerError(illegalArgumentException.getMessage());
        }

        Throwable cause = illegalArgumentException.getCause();

        if (cause instanceof UnrecognizedPropertyException unrecognizedPropertyException) {
            return new InternalServerError(
                    unrecognizedPropertyException.getPropertyName() + " is an unknown property"
            );
        } else if (cause instanceof MismatchedInputException mismatchedInputException) {
            // TODO: Parse the exception more thoroughly if needed
            return new InternalServerError(mismatchedInputException.getOriginalMessage());
        } else {
            return new InternalServerError(cause.getMessage());
        }
    }

    public static InternalServerError create(String message) {
        return new InternalServerError(message);
    }

}
