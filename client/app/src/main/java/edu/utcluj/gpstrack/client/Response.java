package edu.utcluj.gpstrack.client;

public class Response {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String email;

    public Response(String token, String type, Long id, String email) {
        this.token = token;
        this.type = type;
        this.id = id;
        this.email = email;
    }

    public String getToken() {
        return token;
    }
}
