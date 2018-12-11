package com.arranger.apv.scene;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.arranger.apv.Main;

public class Forest extends Animation {

	private static final int MIN_TREES = 3;
	private static final int MAX_TREES = 12;
	
	private List<Tree> trees = new ArrayList<Tree>();
	
	public Forest(Main parent) {
		super(parent);
		
		int numTrees = (int)parent.random(MIN_TREES, MAX_TREES);
		IntStream.range(0, numTrees).parallel().forEach(i -> {
			trees.add(new Tree(parent));
		});
		
	}

	@Override
	public void drawScene() {
		super.drawScene();
		trees.forEach(tree -> tree.drawScene());
	}

	@Override
	public boolean isNew() {
		return trees.get(0).isNew();
	}

}
