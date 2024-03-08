package ru.biscuitka.allocator.users;

public class UserMapper {
    public static User fromDtoToUser(UserDto dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setTicketSeries(dto.getTicketSeries());
        user.setTicketNumber(dto.getTicketNumber());
        user.setTicketDate(dto.getTicketDate());
        return user;
    }

    public static UserDto fromUserToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setTicketSeries(user.getTicketSeries());
        dto.setTicketNumber(user.getTicketNumber());
        dto.setTicketDate(user.getTicketDate());
        return dto;
    }
}
