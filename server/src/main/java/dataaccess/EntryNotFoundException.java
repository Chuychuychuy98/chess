package dataaccess;

public class EntryNotFoundException extends DataAccessException{
    public EntryNotFoundException(String message) {
        super(message);
    }
}
