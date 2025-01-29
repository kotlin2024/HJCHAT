package hjp.hjchat.infra.security.exception

class DuplicateUsernameException(message: String) : RuntimeException(message)

class DuplicateEmailException(message: String) : RuntimeException(message)

class PasswordMismatchException(message: String) : RuntimeException(message)