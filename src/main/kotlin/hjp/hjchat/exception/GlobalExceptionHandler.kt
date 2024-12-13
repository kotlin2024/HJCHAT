import hjp.hjchat.exception.DuplicateEmailException
import hjp.hjchat.exception.DuplicateUsernameException
import hjp.hjchat.exception.PasswordMismatchException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus
import jakarta.servlet.http.HttpServletRequest

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateUsernameException::class)
    fun handleDuplicateUsernameException(ex: DuplicateUsernameException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse(message = ex.message ?: "중복된 사용자 이름이 존재합니다."),
            HttpStatus.BAD_REQUEST // 400 상태 코드
        )
    }

    @ExceptionHandler(DuplicateEmailException::class)
    fun handleDuplicateEmailException(ex: DuplicateEmailException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse(message = ex.message ?: "중복된 이메일이 존재합니다."),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(PasswordMismatchException::class)
    fun handlePasswordMismatchException(ex: PasswordMismatchException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse(message = ex.message ?: "비밀번호와 확인 비밀번호가 일치하지 않습니다."),
            HttpStatus.BAD_REQUEST
        )
    }




    @ExceptionHandler(Exception::class)
    fun handleGenericException(request: HttpServletRequest, ex: Exception): ResponseEntity<ErrorResponse> {
        // Swagger 관련 경로 제외
        if (request.requestURI.contains("/v3/api-docs") || request.requestURI.contains("/swagger-ui")) {
            // Swagger 경로는 예외 처리에서 제외합니다.
            return ResponseEntity(
                ErrorResponse(message = "Swagger 요청 중 오류가 발생했습니다."),
                HttpStatus.INTERNAL_SERVER_ERROR
            )
        }

        // 다른 경로에 대한 예외 처리
        return ResponseEntity(
            ErrorResponse(message = "서버에서 처리 중 오류가 발생했습니다."),
            HttpStatus.INTERNAL_SERVER_ERROR
        )
    }

    data class ErrorResponse(
        val message: String
    )
}
