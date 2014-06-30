package org.jbake.launcher;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.jbake.app.Oven;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;


public class WatchFolder {
    private  LaunchOptions options;
    private  static Baker baker;

    public static void main(String [] args ) throws Exception{
        WatchFolder wf = new WatchFolder();
        wf.run("/home/richard/workspace/workspace_ricston/jbake/src/test", null );
    }

    public void run(String FOLDER, final LaunchOptions options)  {
        try {

            System.out.println("Watching " + FOLDER + " for changes .. ");
            // The monitor will perform polling on the folder every 5 seconds
            this.options = options;
            final long pollingInterval = 5 * 1000;

            File folder = new File(FOLDER);

            if (!folder.exists()) {
                // Test to see if monitored folder exists
                throw new RuntimeException("Directory not found: " + FOLDER);
            }

            final FileAlterationObserver observer = new FileAlterationObserver(folder);
            final FileAlterationMonitor monitor =
                    new FileAlterationMonitor(pollingInterval);



            final FileAlterationListener listener = new FileAlterationListenerAdaptor() {
                // Is triggered when a file is created in the monitored folder
                @Override
                public void onFileCreate(File file) {
                    try {
                        // "file" is the reference to the newly created file
                        System.out.println("File created: "  + file.getCanonicalPath());
                        Baker baker = new Baker();
                        baker.bake(options);
                    } catch (IOException e) {
                        e.printStackTrace(System.err);
                    }
                }

                // Is triggered when a file is deleted from the monitored folder
                @Override
                public void onFileDelete(File file) {
                    try {
                        // "file" is the reference to the removed file
                        System.out.println("File removed: "
                                + file.getCanonicalPath());
                        // "file" does not exists anymore in the location
                        System.out.println("File still exists in location: " + file.exists());
                        Baker baker = new Baker();
                        baker.bake(options);
                    } catch (IOException e) {
                        e.printStackTrace(System.err);
                    }
                }

                public void onFileChange(File file) {
                    try {
                        System.out.println("File modified: " + file.getCanonicalPath());
                        Baker baker = new Baker();
                        baker.bake(options);
                    } catch (IOException e) {
                        e.printStackTrace(System.err);
                    }
                }
            };

            observer.addListener(listener);
            monitor.addObserver(observer);
            monitor.start();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

//    private void bake(LaunchOptions options) {
//        try {
//            Oven oven = new Oven(options.getSource(), options.getDestination(), options.isClearCache());
//            oven.setupPaths();
//            oven.bake();
//            final List<String> errors = oven.getErrors();
//            if (!errors.isEmpty()) {
//                // TODO: decide, if we want the all error here
//                System.err.println(MessageFormat.format("JBake failed with {0} errors:", errors.size()));
//                int errNr = 1;
//                for (String msg : errors) {
//                    System.err.println(MessageFormat.format("{0}. {1}", errNr, msg));
//                    ++errNr;
//                }
//                System.exit(1);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.exit(1);
//        }
//    }
}

