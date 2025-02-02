import java.lang.annotation.*;

@UserDefined(user = "iasonas")
public abstract class Main {

	public static void main(String[] args) {
        // check annotations
        Console.printInfo("Checking annotations...");
        AnnotationApplyer.check();
        Console.printInfo("Annnotation checking finished.");

        // start the program
        Console.printInfo("Starting printing proccess...");
        new Greeter().greet();
        Console.printInfo("Printing proccess finished.");
        Console.printInfo("Aborting.");
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
                Console.printInfo("Executing MessageSerice.print()...");
    		    ms.print();
                Console.printInfo("Message printed!");
    		} catch (InvalidMessageException e) {
                Console.printInfo("Cannot greet. "+e.toString());
    		}
        }
	    
	    public void greet () {
            Console.printInfo("thread started, waiting for response...");
    	    thread.start();
            try {
                thread.join();
            } catch (Exception e) {
                Console.printInfo(e);
            }
            Console.printInfo("thread finnished successfully");
	    }
	};
};

@UserException
@UserDefined(user = "iasonas")
class AnnotationApplyer {
    public static void check () {
        Console.printInfo("passing classes...");
        checkAnnotations(CheckType.STRICT, UserException.class, Printer.class, InvalidMessageException.class);
        checkAnnotations(CheckType.STRICT, Main.class, AnnotationApplyer.class, MessageService.class, UserDefined.class);
        Console.printInfo("All classes has checked successfull");
    }

    enum CheckType {
        STRICT,
        LAX
    }

    public synchronized static <C extends Class<?>> void checkAnnotations (CheckType ct, C... classes) {
        for (Class<?> clazz : classes) {            
            if (clazz.isAnnotationPresent(UserException.class)) {
                Console.printStats("Class marked with UserException found, passing class...");
                checkAnnotations(ct, clazz);
            } else if (clazz.isAnnotationPresent(UserDefined.class)) {
                Console.printStats("Class marked with UserDefined found, passing class...");
                checkUserDefined(clazz);
            }
        }
    }

    public synchronized static void checkUserDefined (Class<?> clazz) {
        final String className = clazz.getName();
        final String annotationVal = clazz.getAnnotation(UserDefined.class).user();
        Console.printStats(String.format("Class '%s', has made by %s.", className, annotationVal));
    }

    public synchronized static void checkAnnotations (CheckType ct, Class<?> clazz) {
        if (!Exception.class.isAssignableFrom(clazz)) {
            final String msg = "Error: "+clazz.getName()+" does not extend java.lang.Exception";
            if (ct==CheckType.STRICT) {
                Console.printErr(msg);
            } else {
                Console.printInfo(msg);
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
        Console.printInfo("Printing message...");
        Console.printMessage(message);
        Console.printInfo("Message printed successfully.");
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
    }
};

abstract class Console {
    public static void printInfo (Object o) {
        System.out.print("\u001B[33m[INFO] ");
        System.out.println(o.toString()+"\u001B[0m");
    }
    public static void printErr (Object o) {
        System.err.print("\u001B[31m[ERR] ");
        System.out.println(o.toString()+"\u001B[0m");
    }
    public static void printStats (Object o) {
        System.out.print("\u001B[34m[STAT] ");
        System.out.println(o.toString()+"\u001B[0m");
    }
    public static void printMessage (Object o) {
        System.out.print("\u001B[32m[MSG] ");
        System.out.println(o.toString()+"\u001B[0m");
    }
}
