package kb.hackathon.ssh.domain.member.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateMemberIdException extends RuntimeException {

    public DuplicateMemberIdException(String message) {
        super(message);
    }
}
