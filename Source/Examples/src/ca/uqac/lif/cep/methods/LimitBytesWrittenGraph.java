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

import java.util.Scanner;

import ca.uqac.lif.cep.*;
import ca.uqac.lif.cep.Connector.ConnectorException;
import static ca.uqac.lif.cep.Connector.*;
import ca.uqac.lif.cep.functions.*;
import ca.uqac.lif.cep.gnuplot.GnuplotProcessor;
import ca.uqac.lif.cep.gnuplot.GnuplotScatterplot;
import ca.uqac.lif.cep.io.LineReader;
import ca.uqac.lif.cep.numbers.Addition;
import ca.uqac.lif.cep.numbers.NumberCast;
import ca.uqac.lif.cep.sets.*;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tuples.*;

/**
 * Chain of processors counting total bytes written for each method, and
 * plotting it as a function of time
 * @author Sylvain Hallé
 */
public class LimitBytesWrittenGraph 
{
	public static void main(String[] args) throws ConnectorException
	{
		String filename = "trace-1.txt";
		//Function count_function = new Timestamp();
		Function count_function = new FunctionTree(new CumulativeFunction<Number>(Addition.instance),
				new Constant(1));
		// Setup source from an input file
		LineReader feeder = new LineReader(CallGraphPipe.class.getResourceAsStream(filename));
		FunctionProcessor converter = new FunctionProcessor(StringToEvent.instance);
		connect(feeder, converter);
		Fork f1 = new Fork(2);
		connect(converter, f1);
		FunctionProcessor name_p = new FunctionProcessor(GetMethodName.instance);
		connect(f1, LEFT, name_p, INPUT);
		Function byte_count = new ByteCount(new Scanner(LimitBytesWrittenGraph.class.getResourceAsStream("write-signatures.txt")));
		FunctionProcessor byte_count_p = new FunctionProcessor(byte_count);
		connect(f1, RIGHT, byte_count_p, INPUT);
		FunctionProcessor merge_1 = new FunctionProcessor(new MergeScalars("name", "bytes"));
		connect(name_p, OUTPUT, merge_1, LEFT);
		connect(byte_count_p, OUTPUT, merge_1, RIGHT);
		
		GroupProcessor sum = new GroupProcessor(1, 1);
		{
			// Create sum
			FunctionProcessor get_bytes = new FunctionProcessor(new FunctionTree(
					NumberCast.instance, new FunctionTree(new FetchAttribute("bytes"), new ArgumentPlaceholder(0))));
			CumulativeProcessor total = new CumulativeProcessor(new CumulativeFunction<Number>(Addition.instance));
			connect(get_bytes, total);
			sum.addProcessors(get_bytes, total);
			sum.associateInput(INPUT, get_bytes, INPUT);
			sum.associateOutput(OUTPUT, total, OUTPUT);
		}
		SlicerMap slicer = new SlicerMap(new FetchAttribute("name"), sum);
		connect(merge_1, slicer);
		FunctionProcessor to_tuple = new FunctionProcessor(MapToTuple.instance);
		connect(slicer, to_tuple);
		Fork f2 = new Fork(2);
		connect(to_tuple, f2);
		FunctionProcessor time_tuple = new FunctionProcessor(new FunctionTree(
				new ScalarIntoTuple("time"), new FunctionTree(
						count_function, new ArgumentPlaceholder(0)
						)));
		FunctionProcessor merge_tuples = new FunctionProcessor(new MergeTuples(2));
		connect(f2, LEFT, merge_tuples, LEFT);
		connect(f2, RIGHT, time_tuple, INPUT);
		connect(time_tuple, OUTPUT, merge_tuples, RIGHT);
		FunctionProcessor wrap = new FunctionProcessor(Wrap.instance);
		connect(merge_tuples, wrap);
		CumulativeProcessor union = new CumulativeProcessor(new CumulativeFunction<Multiset>(MultisetUnion.instance));
		connect(wrap, union);
		// Plot
		GnuplotScatterplot scatterplot = new GnuplotScatterplot("time", false);
		scatterplot.setStacked(true);
		FunctionProcessor plot = new FunctionProcessor(scatterplot);
		connect(union, plot);
		// Pull
		Processor to_pull = plot;
		Pullable p = to_pull.getPullableOutput();
		
		for (Object o : p)
		{
			System.out.println(o);
		}
	}

}
