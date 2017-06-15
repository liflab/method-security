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

import static ca.uqac.lif.cep.Connector.connect;

import java.util.Scanner;

import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.Connector.ConnectorException;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.CumulativeProcessor;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.functions.FunctionProcessor;
import ca.uqac.lif.cep.io.LineReader;
import ca.uqac.lif.cep.numbers.Addition;

/**
 * Chain of processors counting total bytes written by all methods
 * in the execution of the program
 * @author Sylvain Hallé
 */
public class LimitBytesWrittenTotal 
{
	public static void main(String[] args) throws ConnectorException
	{
		String filename = "trace-1.txt";
		// Setup source from an input file
		LineReader feeder = new LineReader(CallGraphPipe.class.getResourceAsStream(filename));
		FunctionProcessor converter = new FunctionProcessor(StringToEvent.instance);
		connect(feeder, converter);
		Function byte_count = new ByteCount(new Scanner(LimitBytesWrittenTotal.class.getResourceAsStream("write-signatures.txt")));
		FunctionProcessor byte_count_p = new FunctionProcessor(byte_count);
		connect(converter, byte_count_p);
		CumulativeProcessor sum = new CumulativeProcessor(new CumulativeFunction<Number>(Addition.instance));
		connect(byte_count_p, sum);
		// Pull
		Pullable p = sum.getPullableOutput();
		for (Object o : p)
		{
			System.out.println(o);
		}
	}

}
