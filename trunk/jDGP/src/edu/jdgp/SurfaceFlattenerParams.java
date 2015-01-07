package edu.jdgp;

public class SurfaceFlattenerParams {
	private float lambda;
	private int maxIter;
	private int currentIter;
	
	public SurfaceFlattenerParams(float lambdaParam, int maxIterParam) {
		lambda = lambdaParam;
		maxIter = maxIterParam;
		currentIter = 0;
	}
	
	public float getLambda() {
		return lambda;
	}
	
	public void setLambda(float lambda) {
		this.lambda = lambda;
	}
	
	public int getIterations() {
		return maxIter;
	}
	
	public void setIterations(int iterations) {
		this.maxIter = iterations;
	}

	public boolean hasToContinue() {
		return (++currentIter == maxIter);
	}
}
