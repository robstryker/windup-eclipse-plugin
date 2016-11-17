package org.jboss.tools.windup.runtime.model;

/**
 * A class holding constnats that can be found either in help.xml or
 * other parts of the windup code itself.
 * 
 */
public interface IWindupConstants {
	
	// TODO create a unit test that scans help.xml and makes sure these options exist!
	
	public static final String SOURCE_MODE_OPTION = "sourceMode";    //SourceModeOption.NAME;
	public static final String TARGET_OPTION = "target";    //TargetOption.NAME;
	public static final String SOURCE_OPTION = "source";    //SourceOption.NAME;
	public static final String SKIP_REPORTS_RENDERING_OPTION = "skipReports"; //SkipReportsRenderingOption.NAME;
	public static final String SCAN_PACKAGES_OPTION = "packages"; //ScanPackagesOption.NAME;
	public static final String USER_RULES_DIRECTORY_OPTION = "userRulesDirectory"; //UserRulesDirectoryOption.NAME;
	
	
	
	// The following are tooling options, which have no option-description 
	// but is rather exposed via api methods, so we'll assign them a string key 
	// just for our tools
	public static final String TOOLING_PREFIX= "tooling_";

	public static final String T_IGNORE = TOOLING_PREFIX + "IgnorePattern";
	
	
	
	// TODO Unit test that these constants match also!
	public static final String QUICKFIX_REPLACE=("Quickfix REPLACE token");
    /**
     * It searches for a token and deletes whole found line
     */
	public static final String QUICKFIX_DELETE_LINE=("Quickfix DELETE_LINE");
    /**
     * It searches for a token and inserts a new line after found line
     */
	public static final String QUICKFIX_INSERT_LINE=("Quickfix INSERT_LINE");
	
}
