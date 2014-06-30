package org.jbake.launcher;

import org.jbake.app.Oven;

import java.text.MessageFormat;
import java.util.List;

/**
 * Created by richard on 6/30/14.
 */
public class Baker {
    public  void bake(LaunchOptions options) {
        try {
            Oven oven = new Oven(options.getSource(), options.getDestination(), options.isClearCache());
            oven.setupPaths();
            oven.bake();
            final List<String> errors = oven.getErrors();
            if (!errors.isEmpty()) {
                // TODO: decide, if we want the all error here
                System.err.println(MessageFormat.format("JBake failed with {0} errors:", errors.size()));
                int errNr = 1;
                for (String msg : errors) {
                    System.err.println(MessageFormat.format("{0}. {1}", errNr, msg));
                    ++errNr;
                }
                System.exit(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
