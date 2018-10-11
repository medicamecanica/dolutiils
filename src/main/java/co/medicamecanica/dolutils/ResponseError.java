package co.medicamecanica.dolutils;

public class ResponseError {
    private Error error;

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public class Error{
        private int code;
        private String message;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "Error{" +
                    "code=" + code +
                    ", message='" + message + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ResponseError{" +
                "error=" + error +
                '}';
    }
}
