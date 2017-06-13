package ca.uqac.lif.cep.methods;

public abstract class MethodEvent 
{
	/**
	 * The name of the method considered by this event
	 */
	protected String m_methodName;
	
	public MethodEvent(String name)
	{
		super();
		m_methodName = name;
	}
	
	public String getMethodName()
	{
		return m_methodName;
	}
	
	/**
	 * Event representing a method call
	 */
	public static class MethodCall extends MethodEvent
	{
		public MethodCall(String name)
		{
			super(name);
		}
		
		@Override
		public String toString()
		{
			return "call " + m_methodName;
		}
	}

	/**
	 * Event representing a method return
	 */
	public static class MethodReturn extends MethodEvent
	{
		public MethodReturn(String name)
		{
			super(name);
		}
		
		@Override
		public String toString()
		{
			return "return " + m_methodName;
		}
	}
}
