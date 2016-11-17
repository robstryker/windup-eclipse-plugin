package org.jboss.tools.windup.runtime;

import java.nio.file.Path;
import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.jboss.windup.tooling.ExecutionResults;

public interface IRemoteWindupExecutioner {
    public ExecutionResults execute(Path windupHome, Path projectPath, Path outputPath, 
    		HashMap<String, Object> options, IProgressMonitor monitor);

}
