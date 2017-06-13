package ca.uqac.lif.cep.methods;

import static ca.uqac.lif.cep.Connector.connect;
import static ca.uqac.lif.cep.Connector.INPUT;
import static ca.uqac.lif.cep.Connector.OUTPUT;
import ca.uqac.lif.cep.Connector.ConnectorException;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.ArgumentPlaceholder;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.CumulativeProcessor;
import ca.uqac.lif.cep.functions.Equals;
import ca.uqac.lif.cep.functions.FunctionProcessor;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.io.LineReader;
import ca.uqac.lif.cep.sets.Multiset;
import ca.uqac.lif.cep.sets.MultisetUnion;
import ca.uqac.lif.cep.sets.ToList;
import ca.uqac.lif.cep.sets.Wrap;
import ca.uqac.lif.cep.tmf.Filter;
import ca.uqac.lif.cep.tmf.Fork;

/**
 * Creates a call graph from a stream of method events
 * @author Sylvain Hallé
 *
 */
public class CallGraph
{
	public static void main(String[] args) throws ConnectorException
	{
		String filename = "trace-1.txt";
		// Setup source from an input file
		LineReader feeder = new LineReader(CallGraph.class.getResourceAsStream(filename));
		FunctionProcessor converter = new FunctionProcessor(StringToEvent.instance);
		connect(feeder, converter);
		Fork f1 = new Fork(3);
		connect(converter, f1);
		FunctionProcessor is_call = new FunctionProcessor(new FunctionTree(
				Equals.instance,
				new FunctionTree(GetEventType.instance, new ArgumentPlaceholder(0)),
				new Constant("call")
				));
		connect(f1, 1, is_call, INPUT);
		Fork f2 = new Fork(3);
		connect(is_call, f2);
		Filter fil_1 = new Filter();
		connect(f1, 0, fil_1, 0);
		connect(f2, 0, fil_1, 1);
		Stack stack = new Stack(new MethodEvent.MethodCall("main"));
		connect(f1, 2, stack, 0);
		connect(f2, 2, stack, 1);
		Filter fil_2 = new Filter();
		connect(stack, OUTPUT, fil_2, 0);
		connect(f2, 1, fil_2, 1);
		FunctionProcessor f_name_1 = new FunctionProcessor(GetMethodName.instance);
		connect(fil_1, f_name_1);
		FunctionProcessor f_name_2 = new FunctionProcessor(GetMethodName.instance);
		connect(fil_2, f_name_2);
		FunctionProcessor to_list = new FunctionProcessor(new ToList(String.class, String.class));
		connect(f_name_2, OUTPUT, to_list, 0);
		connect(f_name_1, OUTPUT, to_list, 1);
		CumulativeProcessor union = new CumulativeProcessor(new CumulativeFunction<Multiset>(MultisetUnion.instance));
		FunctionProcessor wrap = new FunctionProcessor(Wrap.instance);
		connect(to_list, wrap);
		connect(wrap, union);
		// Pull
		Pullable p = union.getPullableOutput();
		for (Object o : p)
		{
			System.out.println(o);
		}
	}
}
