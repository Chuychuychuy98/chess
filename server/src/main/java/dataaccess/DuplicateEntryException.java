package dataaccess;

public class DuplicateEntryException extends DataAccessException{
    public DuplicateEntryException(String message) {
        super(message);
    }
}
