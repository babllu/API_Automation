package utils;

public class GetCallerClass {

	
	/**
	 * This method is used to return the class object of its calling class 
	 * under testsuite package
	 * @author arpan
	 */
     public static Class<?> getCallerClass() throws ClassNotFoundException
     {
    	 Class<?> data = null;
    	 StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
    	 for(int i = 0;i<stElements.length;i++)
    	 {
    		if(stElements[i].toString().contains("testsuite")) 
    		{
    			String rawFQN = stElements[i].toString().split("\\(")[0];
    			data= Class.forName(rawFQN.substring(0, rawFQN.lastIndexOf('.')));
    			break;
    		}
    	 }
		return data;
     }

}
