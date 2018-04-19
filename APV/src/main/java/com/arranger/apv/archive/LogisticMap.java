package com.arranger.apv.archive;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator;

/**
 * @see https://en.wikipedia.org/wiki/Logistic_map
 * @see https://en.wikipedia.org/wiki/Feigenbaum_constants
 * @see https://www.youtube.com/watch?v=ETrYE4MdoLQ
 * 
 * x    = r * x  * (1 - x )
 *  n+1         n        n
 *  
 *  where r is the 'fertility' ratio 
 *  where xsubn is the % of total possible population (between 0 and 1)
 */
public class LogisticMap extends APVPlugin {

	private static final float FERTILITY_RATIO = 3.2f; // Must be between 1 & 4, but will die off below 2
	private static final int DEFAULT_FRAMES_TO_SKIP = 50;
	
	private float previousN = .5f;
	
	public LogisticMap(Main parent, int framesToSkip) {
		super(parent);
		
		parent.getSetupEvent().register(() -> {
			parent.getAgent().registerHandler(() -> {
				doLogisticMap();
			}, framesToSkip);
		});
	}
	
	public LogisticMap(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getInt(0, DEFAULT_FRAMES_TO_SKIP));
	}

	private void doLogisticMap() {
		float nextN = FERTILITY_RATIO * previousN * (1 - previousN);
		parent.getParticles().setPct(nextN);
		//System.out.printf("Logistic Map: previousN[%1f] nextN[%2f]\n", previousN, nextN);
		previousN = nextN;
	}
}
