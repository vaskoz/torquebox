package org.torquebox.messaging.deployers;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;

import org.junit.Test;
import org.torquebox.test.mc.vdf.AbstractDeployerTestCase;

public class DeployersTest extends AbstractDeployerTestCase {
	
	@Test
	public void testJBossBeansXml() throws Exception {
		//File jbossBeansXml = new File( new File( new File( new File( new File( System.getProperty( "user.dir" ) , "src" ), "assembly" ), "resources" ), "META-INF" ), "torquebox-messaging-int-jboss-beans.xml" );
		URL jbossBeansXml = getClass().getResource( "/torquebox-messaging-int-jboss-beans.xml" );
		
		String deploymentName = addDeployment( jbossBeansXml );
		
		processDeployments( true );
		
		assertNotNull( getBean( "ManagedTopicDeployer" ) );
		assertNotNull( getBean( "ManagedQueueDeployer" ) );
		assertNotNull( getBean( "TopicsYamlParsingDeployer" ) );
		assertNotNull( getBean( "QueuesYamlParsingDeployer" ) );
		assertNotNull( getBean( "MessagingRbParsingDeployer" ) );
		assertNotNull( getBean( "MessageProcessorDeployer" ) );
		assertNotNull( getBean( "MessagingRuntimePoolDeployer" ) );
		
		undeploy( deploymentName );
	}

}
