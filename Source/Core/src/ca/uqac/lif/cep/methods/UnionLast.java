package ca.uqac.lif.cep.methods;

import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.UniformProcessor;
import ca.uqac.lif.cep.sets.Multiset;

public class UnionLast extends UniformProcessor 
{
	protected final Multiset m_set;

	public UnionLast() 
	{
		super(1, 1);
		m_set = new Multiset();
	}

	@Override
	public UnionLast clone()
	{
		return new UnionLast();
	}

	@Override
	protected boolean compute(Object[] inputs, Object[] outputs) throws ProcessorException 
	{
		m_set.addAll((Multiset) inputs[0]);
		outputs[0] = m_set;
		return true;
	}

}
