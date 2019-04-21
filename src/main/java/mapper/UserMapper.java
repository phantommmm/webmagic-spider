package mapper;

import domain.User;

public interface UserMapper {
    public void add(User user);
    public int num();
    public void isExist();
}
