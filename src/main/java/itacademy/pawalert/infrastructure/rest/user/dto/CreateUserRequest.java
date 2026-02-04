package itacademy.pawalert.infrastructure.rest.user.dto;

public class CreateUserRequest {
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;


    public CreateUserRequest(String username, String email, String password,
                             String fullName, String phoneNumber) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public String getPhoneNumber() { return phoneNumber; }
}
