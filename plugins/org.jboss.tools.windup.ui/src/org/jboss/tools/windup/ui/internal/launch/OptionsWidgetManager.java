package org.jboss.tools.windup.ui.internal.launch;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.windup.runtime.model.OptionDescription;
import org.jboss.tools.windup.runtime.model.OptionFacades;
import org.jboss.tools.windup.runtime.model.OptionFacades.OptionsFacadeManager;
import org.jboss.tools.windup.ui.internal.launch.OptionUiFacades.OptionUiFacade;
import org.jboss.tools.windup.windup.ConfigurationElement;
import org.jboss.tools.windup.windup.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Manages UI facades for option descriptions.
 */
public class OptionsWidgetManager {
	
	private Map<String, OptionUiFacade> optionTypeControls = Maps.newHashMap();
	private OptionsFacadeManager optionsFacadeManager;
	
	private final Runnable optionChangedCallback;
	private ConfigurationElement configuration;
	
	public OptionsWidgetManager(OptionsFacadeManager optionsFacadeManager, 
			Composite parent, ConfigurationElement configuration, Runnable optionChangedCallback) {
		this.optionChangedCallback = optionChangedCallback;
		this.optionsFacadeManager = optionsFacadeManager;
		this.configuration = configuration;
		build(parent);
	}
	
	private void build(Composite parent) {
		for (OptionDescription option : optionsFacadeManager.getOptionDescriptions()) {
			create(option, parent);
		}
	}
	
	private OptionUiFacade create(OptionDescription option, Composite parent) {
		if (!optionTypeControls.containsKey(option)) {
			OptionUiFacade control = createControl(option, parent);
			optionTypeControls.put(option.getName(), control);
		}
		return optionTypeControls.get(option);
	}
	
	private OptionUiFacade createControl(OptionDescription option, Composite parent) {
		OptionUiFacade optionTypeControl = OptionUiFacades.createOptionUiFacade(
				option, optionsFacadeManager, optionChangedCallback);
		optionTypeControl.createControls(parent);
		return optionTypeControl; 
	}
	
	public OptionUiFacade getOptionUiFacade(String name) {
		return optionTypeControls.get(name);
	}
	
	public List<String> getOptions() {
		List<String> options = Lists.newArrayList();
    	 for (OptionDescription option : optionsFacadeManager.getOptionDescriptions()) {
    		 boolean filter = false;
    		 // Filter existing single-valued options.
    		 for (Pair pair : configuration.getOptions()) {
    			 if (pair.getKey().equals(option.getName()) && OptionFacades.isSingleValued(option)) {
    				 filter = true;
    				 break;
    			 }
    		 }
    		 if (!filter) {
    			 options.add(option.getName());
    		 }
    	 }
    	 Collections.sort(options);
		return options;
	}
}
