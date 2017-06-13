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

import ca.uqac.lif.cep.functions.UnaryFunction;
import ca.uqac.lif.cep.methods.MethodEvent.MethodCall;
import ca.uqac.lif.cep.methods.MethodEvent.MethodReturn;

/**
 * Function that parses a line of text and creates the corresponding method
 * event
 * @author Sylvain Hallé
 */
public class StringToEvent extends UnaryFunction<String,MethodEvent>
{
	/**
	 * A single instance of this function
	 */
	public static final StringToEvent instance = new StringToEvent();
	
	private StringToEvent() 
	{
		super(String.class, MethodEvent.class);
	}

	@Override
	public MethodEvent getValue(String x)
	{
		String[] parts = x.split("\\s+");
		if (parts[0].compareToIgnoreCase("call") == 0)
		{
			return new MethodCall(parts[1].trim());
		}
		return new MethodReturn(parts[1].trim());
	}
}
