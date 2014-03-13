package uk.ac.ed.maize.exceptions;


public final class EvaluationError extends CompilerError
{
	private static final long serialVersionUID = 1L;

	public EvaluationError(String msg)
	{
		super(msg);
	}

	@Override
	public ErrorType getType() { return ErrorType.EvaluationError; }

}
