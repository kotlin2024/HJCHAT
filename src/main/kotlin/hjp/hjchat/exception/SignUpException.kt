package hjp.hjchat.exception

class DuplicateUsernameException(message: String) : RuntimeException(message)

class DuplicateEmailException(message: String) : RuntimeException(message)

class PasswordMismatchException(message: String) : RuntimeException(message)