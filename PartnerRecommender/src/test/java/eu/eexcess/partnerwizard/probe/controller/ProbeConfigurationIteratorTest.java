/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eexcess.partnerwizard.probe.controller;

import eu.eexcess.partnerwizard.probe.model.Pair;
import eu.eexcess.partnerwizard.probe.model.ProbeConfiguration;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author hgursch
 */
public class ProbeConfigurationIteratorTest{

	@Test
	public void testProbeConfigurationIterator(){
		System.out.println( "Test ProbeConfigurationIterator" );

		List<String> keywords = Arrays.asList( "keyword 1", "keyword 2", "keyword 3");
		List<String> generators = Arrays.asList( "Generator 1", "Generator 2", "Generator 3" );

		ProbeConfigurationIterator iterator = new ProbeConfigurationIterator(keywords, generators, true, true );

		int counter = 0;
		while( !iterator.isDone() ){
			Pair<ProbeConfiguration> pair = iterator.nextPair();
			iterator.storeResponse( true, 1 );
			counter++;
		}
		assertEquals( "There should have been 18 round.", 18, counter);

		ProbeConfiguration actualConfiguration = iterator.getWinningConfiguration();
		ProbeConfiguration expectedConfiguration = new ProbeConfiguration(null, "Generator 3", false, true );
		assertEquals( "Test expected winning configuration" , expectedConfiguration, actualConfiguration );
	}

}
