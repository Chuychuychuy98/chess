package dataaccess;

public class DuplicateEntryException extends Exception{
    public DuplicateEntryException(String message) {
        super(message);
    }
}
