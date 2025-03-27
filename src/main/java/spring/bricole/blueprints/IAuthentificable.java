package spring.bricole.blueprints;

public interface IAuthentificable {
    void login(String username, String password);
    void logout();
    void register(String username, String password);
    void resetPassword(String username);
}
