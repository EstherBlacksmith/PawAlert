package itacademy.pawalert.application.port.inbound;

public record AlertWithContactDTO(String id,
                                  String petId,
                                  String userId,
                                  String title,
                                  String description,
                                  String status,
                                  String creatorPhone,
                                  String creatorName  ) {

}
