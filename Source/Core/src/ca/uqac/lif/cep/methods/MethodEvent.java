/*
    BeepBeep palette for analyzing traces of method calls
    Copyright (C) 2017 Raphaël Khoury, Sylvain Hallé

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.lif.cep.methods;

public abstract class MethodEvent 
{
	/**
	 * The name of the method considered by this event
	 */
	protected final String m_methodName;
		
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
		/**
		 * The parameters associated to this method call
		 */
		protected final Object[] m_arguments;

		public MethodCall(String name, Object ... parameters)
		{
			super(name);
			m_arguments = parameters;
		}
		
		/**
		 * Gets the <i>i</i>-th argument of this method call
		 * @param index The index of the argument to retrieve
		 * @return The argument
		 */
		public Object getArgument(int index)
		{
			return m_arguments[index];
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
