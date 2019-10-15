package entity;

public class MessageError {

    private String status;
    private String message;

    public MessageError(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public MessageError() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
