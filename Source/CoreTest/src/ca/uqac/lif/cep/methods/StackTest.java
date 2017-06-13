package ca.uqac.lif.cep.methods;

import static org.junit.Assert.*;

import org.junit.Test;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Connector.ConnectorException;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.tmf.QueueSource;

public class StackTest 
{
	@Test
	public void test1() throws ConnectorException
	{
		QueueSource qs_flag = new QueueSource(1);
		qs_flag.addEvent(true);
		qs_flag.addEvent(true);
		qs_flag.addEvent(false);
		QueueSource qs_evts = new QueueSource(1);
		qs_evts.addEvent("B");
		qs_evts.addEvent("C");
		qs_evts.addEvent("D");
		Stack s = new Stack("A");
		Connector.connect(qs_evts, 0, s, 0);
		Connector.connect(qs_flag, 0, s, 1);
		Pullable p = s.getPullableOutput();
		Object o;
		o = p.pull();
		assertNotNull(o);
		assertEquals("A", (String) o);
		o = p.pull();
		assertEquals("B", (String) o);
		o = p.pull();
		assertEquals("B", (String) o);
	}
}
