package org.jboss.tools.windup.runtime.model;

import java.util.List;

/**
 * 
 * This file seems to have been changed upstream to have less details, 
 * but the underlying help.xml seems to still have all the data required
 * to generate the old objects with more information
 * 
 * @author <a href="mailto:jesse.sightler@gmail.com">Jesse Sightler</a>
 */
public class OptionDescription
{
    private String name;
    private String description;

    // Replaced back in
    private String type, uiType;
    private List<String> availableOptions;
    
    public OptionDescription(String name, String description)
    {
        this.name = name;
        this.description = description;
    }
    
    public OptionDescription(String name, String description, String type, String uiType, List<String> availableOptions)
    {
        this.name = name;
        this.description = description;
        this.type = type;
        this.uiType = uiType;
        this.availableOptions = availableOptions;
    }
    
    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

	public String getType() {
		return type;
	}

	public String getUiType() {
		return uiType;
	}

	public List<String> getAvailableOptions() {
		return availableOptions;
	}
}
