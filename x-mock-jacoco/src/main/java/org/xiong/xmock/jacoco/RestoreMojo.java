package org.xiong.xmock.jacoco;

import java.io.File;
import java.io.IOException;
import org.codehaus.plexus.util.FileUtils;

/**
 * Restores original classes as they were before offline instrumentation.
 *
 * @since 0.6.2
 */
public class RestoreMojo{

	public void execute( String originalClassesFile ) throws Exception {
		final File originalClassesDir = new File("target",originalClassesFile );
		final File classesDir = new File("target","classes");
		try {

			FileUtils.copyDirectoryStructure(originalClassesDir, classesDir);
		} catch (final IOException e) {
			throw new Exception("Unable to restore classes.", e);
		}
	}

}