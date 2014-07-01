package org.jbake.launcher;

import java.io.File;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.jbake.app.ConfigUtil;
import org.jbake.app.FileUtil;
import org.jbake.app.Oven;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

/**
 * Launcher for JBake.
 * 
 * @author Jonathan Bullock <jonbullock@gmail.com>
 *
 */
public class Main {

	private final String USAGE_PREFIX 		= "Usage: jbake";
	private final String ALT_USAGE_PREFIX	= "   or  jbake";
    Baker baker = new Baker();
    WatchFolder watcher = new WatchFolder();
	
	/**
	 * Runs the app with the given arguments.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new Main().run(args);
	}
	


	private void run(String[] args) {
		LaunchOptions res = parseArguments(args);

		CompositeConfiguration config = null;
		try {
			config = ConfigUtil.load(res.getSource());
		} catch (ConfigurationException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("JBake " + config.getString("version") + " (" + config.getString("build.timestamp") + ") [http://jbake.org]");
		System.out.println();
		
		if (res.isHelpNeeded()) {
			printUsage(res);
		}
		
		if(res.isBake()) {
			baker.bake(res);
		}

		if (res.isInit()) {
			if (res.getSourceValue() != null) {
				// if type has been supplied then use it
				initStructure(config, res.getSourceValue());
			} else {
				// default to freemarker if no value has been supplied
				initStructure(config, "freemarker");
			}
		}

        if (res.isWatchFolder() ){
            watcher.run( "./templates", res);
        }
		
		if (res.isRunServer()) {
			if (res.getSource().getPath().equals(".")) {
				// use the default destination folder
				runServer(config.getString("destination.folder"), config.getString("server.port"));

			} else {
				runServer(res.getSource().getPath(), config.getString("server.port"));
			}
		}
		
	}
	private LaunchOptions parseArguments(String[] args) {
		LaunchOptions res = new LaunchOptions();
		CmdLineParser parser = new CmdLineParser(res);

		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			printUsage(res);
		}

		return res;
	}

	private void printUsage(Object options) {
		CmdLineParser parser = new CmdLineParser(options);
		StringWriter sw = new StringWriter();
		sw.append(USAGE_PREFIX + "\n");
		sw.append(ALT_USAGE_PREFIX + " <source> <destination>\n");
		sw.append(ALT_USAGE_PREFIX + " [OPTION]... [<value>...]\n\n");
		sw.append("Options:");
		System.out.println(sw.toString());
		parser.setUsageWidth(100);
		parser.printUsage(System.out);
		System.exit(0);
	}

	private void runServer(String path, String port) {
//        WatchFolder.run()
		JettyServer.run(path, port);
		System.exit(0);
	}
	
	private void initStructure(CompositeConfiguration config, String type) {
		Init init = new Init(config);
		try {
			File codeFolder = FileUtil.getRunningLocation();
			init.run(new File("."), codeFolder, type);
			System.out.println("Base folder structure successfully created.");
			System.exit(0);
		} catch (Exception e) {
			System.err.println("Failed to initalise structure!");
			e.printStackTrace();
			System.exit(1);
		}
	}
}
