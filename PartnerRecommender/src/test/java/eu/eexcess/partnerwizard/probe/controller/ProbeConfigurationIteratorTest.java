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
	public void testPairsWithWinners(){
		System.out.println( "Test ProbeConfigurationIterator generating the correct winner." );

		List<String> keywords = Arrays.asList( "keyword 1", "keyword 2", "keyword 3" );
		List<String> generators = Arrays.asList( "Generator 1", "Generator 2", "Generator 3" );

		ProbeConfigurationIterator iterator = new ProbeConfigurationIterator(keywords, generators, true, true );

		int counter = 0;
		while( !iterator.isDone() ){
			Pair<ProbeConfiguration> pair = iterator.nextPair();
			// Alsways the second configuration wins.
			iterator.storeResponse( true, 1 );
			counter++;
		}
		assertEquals( "There should have been 18 round.", 18, counter);

		ProbeConfiguration actualConfiguration = iterator.getWinningConfiguration();
		ProbeConfiguration expectedConfiguration = new ProbeConfiguration(null, "Generator 3", false, true );
		assertEquals( "Test expected winning configuration" , expectedConfiguration, actualConfiguration );
	}

	@Test
	public void testPairsAllDrawns(){
		System.out.println( "Test ProbeConfigurationIterator with no winners." );

		List<String> keywords = Arrays.asList( "keyword 1", "keyword 2", "keyword 3" );
		List<String> generators = Arrays.asList( "Generator 1", "Generator 2", "Generator 3" );

		ProbeConfigurationIterator iterator = new ProbeConfigurationIterator(keywords, generators, true, true );

		int counter = 0;
		while( !iterator.isDone() ){
			Pair<ProbeConfiguration> pair = iterator.nextPair();
			iterator.storeResponse( false, -1 );
			counter++;
		}
		assertEquals( "There should have been 18 round.", 18, counter);

		ProbeConfiguration actualConfiguration = iterator.getWinningConfiguration();
		ProbeConfiguration expectedConfiguration = new ProbeConfiguration(null, "Generator 1", false, false );
		assertEquals( "Test expected first configuration to be taken as a winner." , expectedConfiguration, actualConfiguration );
	}

}
