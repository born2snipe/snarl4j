package snarl4j.client;


public enum SnarlResponse {
    ALREADY_REGISTERED("203"),
    BAD_PACKET("107"),
    CLASS_ALREADY_EXISTS("204"),
    FAILED("101"),
    NOT_REGISTERED("202"),
    NOT_RUNNING("201"),
    SUCCESS("0"),
    TIMED_OUT("103"),
    UNKNOWN_COMMAND("102");

    private String code;

    SnarlResponse(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static SnarlResponse find(String responseCode) {
        for (SnarlResponse response : values()) {
            if (responseCode.equals(response.code)) {
                return response;
            }
        }
        return null;
    }
}
