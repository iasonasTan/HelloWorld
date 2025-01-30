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
	private static class Greeter implements Runnable {
	    private final String MSG = "Hello, World!";
	    private Printer ms = new MessageService<String>(MSG);
        private Thread thread = new Thread(this);
	    
	    public Greeter () {
	    }

        @Override
        public void run () {
            try {
    		    ms.print();
    		} catch (InvalidMessageException e) {
                System.out.println("Cannot greet. "+e.toString());
    		}
        }
	    
	    public void greet () {
    	    thread.start();
	    }
	};
};

@UserDefined(user = "iasonas")
class AnnotationApplyer {
    public static void check () {
        checkUserException(CheckType.STRICT, Main.class, AnnotationApplyer.class, MessageService.class, UserDefined.class);
        checkUserException(CheckType.STRICT, UserException.class, Printer.class, InvalidMessageException.class);
    }

    enum CheckType {
        STRICT,
        LAX
    }

    public static void checkUserException (CheckType ct, Class<?>... classes) {
        for (Class<?> clazz : classes) {
            if (!clazz.isAnnotationPresent(UserException.class)) {
                continue;
            }

            if (!Exception.class.isAssignableFrom(clazz)) {
                System.out.println("Error: "+clazz.getName()+" does not extend java.lang.Exception");
                if (ct==CheckType.STRICT) {
                    System.exit(0);
                }
            }
        }
    }
};

record MessageService <T> (T message) implements Printer {
    @Override
    public synchronized void print () throws InvalidMessageException {
        if (message==null) {
            throw new InvalidMessageException("message is null");
        }
        System.out.println(message);
    }
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface UserDefined {
    String user() default "Not specified";
};

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@interface UserException {

};

@FunctionalInterface
interface Printer {
    void print() throws InvalidMessageException;
};

@UserException
class InvalidMessageException extends Exception {
    public InvalidMessageException (String m) {
        super(m);
        System.exit(0);
    }
};
