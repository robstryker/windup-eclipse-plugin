package org.jboss.tools.windup.runtime.internal;

import java.nio.file.Path;
import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.jboss.tools.windup.runtime.IRemoteWindupExecutioner;
import org.jboss.windup.tooling.ExecutionResults;

public class JMXWindupExecutioner implements IRemoteWindupExecutioner {

	@Override
	public ExecutionResults execute(Path windupHome, Path projectPath, Path outputPath, HashMap<String, Object> options,
			IProgressMonitor monitor) {
//		
//		WindupProgressMonitor windupProgressMonitor = new WindupProgressMonitorAdapter(monitor);
//        ExecutionBuilder execBuilder = WindupService.getServiceFromFurnace(ExecutionBuilder.class, windupProgressMonitor);
//        ExecutionBuilderSetOptions options2 = execBuilder.begin(windupHome)
//                .setInput(projectPath)
//                .setOutput(outputPath.toFile().toPath())
//                .setProgressMonitor(windupProgressMonitor);
//        
//        Iterator<String> optsIt = options.keySet().iterator();
//        while(optsIt.hasNext()) {
//        	String k = optsIt.next();
//        	if( !k.startsWith(IWindupOptions.TOOLING_PREFIX)) {
//        		options2 = options2.setOption(k, options.get(k));
//        	} else {
//        		if( IWindupOptions.T_IGNORE.equals(k)) {
//        			options2 = options2.ignore((String)options.get(k));
//        		}
//        	}
//        }
//		
//		return options2.execute();
		return null;
	}

}
