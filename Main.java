
// package main:
public abstract class Main {
    
	public static void main(String[] args) {
		Greeter g = new Greeter();
		g.greet();
	}
	
	private static class Greeter {
	    private final String MSG = "Hello, World!";
	    private MessageService ms = new MessageService(MSG);
	    
	    public Greeter () {
	    }
	    
	    public void greet () {
    	    try {
    		    ms.print();
    		} catch (InvalidStringException e) {
    		    e.printStackTrace();
    		}
	    }
	};
};

// package lib:
class MessageService implements Runnable, Printer {
    private String message;
    private final Thread thread = new Thread(this);
    
    public MessageService () {   
    }
    
    public MessageService (String message) {
        this.message = message;
    }
    
    public void setMessage (String aMsg) {
        this.message = aMsg;
    }
    
    @Override
    public void run () {
        System.out.println(message);
    }
    
    @Override
    public synchronized void print () throws InvalidStringException {
        if (message==null) {
            throw new InvalidStringException("message is null");
        }
        thread.start();
    }
};

interface Printer {
    void print() throws InvalidStringException;
};

class InvalidStringException extends Exception {
    public InvalidStringException (String m) {
        super(m);
    }
}
