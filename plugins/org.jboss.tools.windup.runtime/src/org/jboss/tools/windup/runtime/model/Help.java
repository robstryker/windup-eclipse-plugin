package org.jboss.tools.windup.runtime.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:jesse.sightler@gmail.com">Jesse Sightler</a>
 */
public class Help
{
    private static final String HELP = "help";
    private static final String OPTION = "option";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";

    private List<OptionDescription> options = new ArrayList<>();

    public List<OptionDescription> getOptions()
    {
        return options;
    }

    private void addOption(OptionDescription optionDescription)
    {
        this.options.add(optionDescription);
    }
}
