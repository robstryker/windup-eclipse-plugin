package org.jboss.tools.windup.runtime;

import org.jboss.tools.windup.runtime.internal.JMXWindupExecutioner;

public class WindupExecutioner {
	
	private static IRemoteWindupExecutioner executioner = null;
	public synchronized static IRemoteWindupExecutioner get() {
		if( executioner == null ) {
			executioner = new JMXWindupExecutioner();
		}
		return executioner;
	}
}
