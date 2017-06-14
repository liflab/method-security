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

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import ca.uqac.lif.cep.SingleProcessor;

/** 
 * Computation of the call graph, without using any of BeepBeep's classes
 * except {@code SingleProcessor}. Contrarily to the processor chain shown in
 * {@link CallGraphPipe} (about 150 lines for the classes + 30 for the piping),
 * this processor directly parses lines of text and updates an internal stack
 * and map using Java primitives. As a result, it is roughly 80 lines big.
 * <p>
 * However, this code still presents several disadvantages with respect to the
 * piping approach:
 * <ul>
 * <li>Events are not structured objects with accessor
 *  methods, but simple character strings. Moreover, the parsing of the string 
 *  is done inside the processor, and could not
 *  be reused by another processor. Should we decide to use the
 *  {@code MethodEvent} class instead of strings, the size of the code would
 *  be increased by 60 lines</li>
 * <li>The internal stack only memorizes the name of the method, and not the
 *  possible additional data associated to an event. Moreover, this stack
 *  is not reusable by other processors as an independent unit.</li>
 * </ul>
 * @author Sylvain Hallé
 */
public class CallGraphBundle extends SingleProcessor
{
	protected ArrayDeque<String> m_methods;
	
	protected Map<CallPair,Integer> m_calls;
	
	public CallGraphBundle()
	{
		super(1, 1);
		m_methods = new ArrayDeque<String>();
		m_calls = new HashMap<CallPair,Integer>();
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
	{
		String[] parts = ((String) inputs[0]).split("\\s+");
		if (parts[0].compareTo("call") == 0)
		{
			if (!m_methods.isEmpty())
			{
				CallPair key = new CallPair(m_methods.peek(), parts[1].trim());
				if (!m_calls.containsKey(key))
				{
					m_calls.put(key, 0);
				}
				int value = m_calls.get(key);
				m_calls.put(key, value + 1);
				m_methods.push(parts[1].trim());				
			}
		}
		else
		{
			if (!m_methods.isEmpty())
			{
				m_methods.pop();
			}
		}
		Object[] out = new Object[1];
		out[0] = m_calls;
		outputs.add(out);
		return true;
	}

	@Override
	public CallGraphBundle clone() 
	{
		CallGraphBundle cgl = new CallGraphBundle();
		cgl.m_methods = m_methods.clone();
		cgl.m_calls.putAll(m_calls);
		return cgl;
	}
	
	protected static class CallPair
	{
		public String m_caller = "";
		public String m_callee = "";
		
		public CallPair(String caller, String callee)
		{
			super();
			m_caller = caller;
			m_callee = callee;
		}
		
		@Override
		public int hashCode()
		{
			return m_caller.hashCode() + m_callee.hashCode();
		}
		
		@Override
		public boolean equals(Object o)
		{
			return ((CallPair) o).m_caller.compareTo(m_caller) == 0 && ((CallPair) o).m_callee.compareTo(m_callee) == 0;
		}
		
		@Override
		public String toString()
		{
			// We implement toString, since if we use BeepBeep's MathList class,
			// we do have a string representation for free
			return m_caller + "," + m_callee;
		}
	}

}
