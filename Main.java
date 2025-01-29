import java.lang.annotation.*;

@UserDefined(user = "iasonas")
public abstract class Main {

	public static void main(String[] args) {
        // check annotations
        AnnotationApplyer.check();

        // start the program
        new Greeter().greet();
	}
	
    @UserDefined(user = "iasonas")
	private static class Greeter {
	    private final String MSG = "Hello, World!";
	    private Printer ms = new MessageService<String>(MSG);
	    
	    public Greeter () {
	    }
	    
	    public void greet () {
    	    try {
    		    ms.print();
    		} catch (InvalidMessageException e) {
                System.out.println("Cannot greet. "+e.toString());
    		}
	    }
	};
};

@UserDefined(user = "iasonas")
class AnnotationApplyer {
    public static void check () {
        checkUserException(Main.class, AnnotationApplyer.class, MessageService.class, UserDefined.class);
        checkUserException(UserException.class, Printer.class, InvalidMessageException.class);
        
    }

    public static void checkUserException (Class<?>... classes) {
        for (Class<?> clazz : classes) {
            if (!clazz.isAnnotationPresent(UserException.class)) {
                continue;
            }

            if (!Exception.class.isAssignableFrom(clazz)) {
                System.out.println("Error: "+clazz.getName()+" does not extend java.lang.Exception");
            }
        }
    }
}

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@interface UserException {

}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface UserDefined {
    String user() default "Not specified";
}

class MessageService <T> implements Runnable, Printer {
    private T message;
    private final Thread thread = new Thread(this);
    private int printCounter;
    
    public MessageService () {   
    }
    
    public MessageService (T message) {
        this.message = message;
    }

    public T getMessage () {
        return message;
    }
    
    public void setMessage (T aMsg) {
        this.message = aMsg;
    }

    public int getTimes () {
        return printCounter;
    }
    
    @Override
    public void run () {
        System.out.println(message.toString());
        printCounter++;
    }
    
    @Override
    public synchronized void print () throws InvalidMessageException {
        if (message==null) {
            throw new InvalidMessageException("message is null");
        }
        thread.start();
    }
};

interface Printer {
    void print() throws InvalidMessageException;
};

@UserException // well placed annotation for checking
class InvalidMessageException extends Exception {
    public InvalidMessageException (String m) {
        super(m);
        System.exit(0);
    }
}
